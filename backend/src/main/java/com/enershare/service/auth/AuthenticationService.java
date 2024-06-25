package com.enershare.service.auth;

import com.enershare.dto.auth.AuthenticationRequest;
import com.enershare.dto.auth.AuthenticationResponse;
import com.enershare.dto.user.UserDTO;
import com.enershare.enums.TokenType;
import com.enershare.exception.AuthenticationException;
import com.enershare.mapper.UserMapper;
import com.enershare.model.token.Token;
import com.enershare.model.user.User;
import com.enershare.repository.token.TokenRepository;
import com.enershare.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String VALID_USERNAME = "enershare";
    private static final String VALID_PASSWORD = "enersh@r3";
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        if ((request.getEmail() == null || request.getEmail().isEmpty()) && (request.getPassword() == null || request.getPassword().isEmpty())) {
            throw new AuthenticationException("Email and password are required");
        } else if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new AuthenticationException("Email is required");
        } else if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new AuthenticationException("Password is required");
        } else {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail()
                                , request.getPassword()));
                User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                        new AuthenticationException("User not found")
                );

                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user);
                revokeAllTokensOfUser(user);
                saveUserToken(user, jwtToken);

                UserDTO userDTO = userMapper.map(user);

                return AuthenticationResponse.
                        builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .user(userDTO)
                        .build();
            } catch (BadCredentialsException e) {
                throw new AuthenticationException("Invalid email or password", e);
            }
        }
    }

    private void revokeAllTokensOfUser(User user) {
        var validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var expirationDate = jwtService.extractExpiration(jwtToken);
        var token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .expirationDate(expirationDate)
                .build();

        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, java.io.IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllTokensOfUser(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseEntity<Void> authenticateAndExecute(String authHeader, Runnable action) {
        String[] credentials = decodeAuthHeader(authHeader);
        if (credentials == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = credentials[0];
        String password = credentials[1];

        if (!isValidCredentials(username, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        action.run(); // Execute the provided action
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private String[] decodeAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                return null;
            }

            return values;
        } catch (IllegalArgumentException e) {
            // This catches exceptions from Base64 decoding
            return null;
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}
