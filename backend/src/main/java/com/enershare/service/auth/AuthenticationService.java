package com.enershare.service.auth;

import com.enershare.dto.apifon.Message;
import com.enershare.dto.apifon.SmsRequest;
import com.enershare.dto.apifon.Subscriber;
import com.enershare.dto.auth.AuthenticationRequestSmsOtp;
import com.enershare.dto.auth.AuthenticationRequestUp;
import com.enershare.dto.auth.AuthenticationResponse;
import com.enershare.dto.user.UserDTO;
import com.enershare.enums.TokenType;
import com.enershare.exception.ApplicationException;
import com.enershare.exception.AuthenticationException;
import com.enershare.mapper.UserMapper;
import com.enershare.model.token.Token;
import com.enershare.model.user.User;
import com.enershare.repository.token.TokenRepository;
import com.enershare.repository.user.UserRepository;
import com.enershare.rest.apifon.ApifonRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    private final ApifonRest apifonRest;

    public AuthenticationResponse authenticateSmsOtp(AuthenticationRequestSmsOtp request) {

        if (request.getPhoneNumber() == null || request.getLogincode() == null) {
            throw new AuthenticationException("Phone number and logincode are required");
        }

        var user = userRepository.findByPhone(request.getPhoneNumber()).orElseThrow(() -> new ApplicationException("1000","User Not Found By Phone"));

        Instant loginRequestDate = user.getLoginRequestDate();
        Instant now = Instant.now();
        long diff = Duration.between(loginRequestDate, now).toMinutes();
//        if (diff > 3) {
//            throw new AuthenticationException("Login request is older than 3 minutes");
//        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getLogincode()));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        //revokeAllTokensOfUser(user);
        saveUserToken(user, jwtToken);
        UserDTO userDTO = userMapper.map(user);

        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).user(userDTO).build();
    }

//    public AuthenticationResponse authenticateStore(AuthenticationRequestUp request) {
//
//        if (request.getUsername() == null || request.getPassword() == null) {
//            throw new AuthenticationException("Username and password are required");
//        }
//
//        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new ApplicationException("1002","User Not Found By Username"));
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
//        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//
//        saveUserToken(user, jwtToken);
//        UserDTO userDTO = userMapper.map(user);
//
//        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).user(userDTO).build();
//    }
//
    public AuthenticationResponse authenticateUp(AuthenticationRequestUp request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new AuthenticationException("Username and password are required");
        }

        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new ApplicationException("1002","User Not Found By Username"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, jwtToken);
        UserDTO userDTO = userMapper.map(user);

        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).user(userDTO).build();
    }

    public void requestAuthentication(AuthenticationRequestSmsOtp request) {

        if (request.getPhoneNumber() == null) {
            throw new AuthenticationException("Phone number is required");
        }

        User user = userRepository.findByPhone(request.getPhoneNumber()).orElseThrow(() -> new ApplicationException("1000","User Not Found By Phone"));

        Random random = new Random();
        int randomCode = random.nextInt(1000000);
        String formattedCode = String.format("%06d", randomCode);
        userRepository.setLoginCode(formattedCode, user.getId());

        String token = apifonRest.auth();

        String languageCode = user.getLanguage();
        String message = "Καλώς ήρθατε στο Ship In Time! Ο κωδικός εισόδου σας είναι ο " + formattedCode;
        if(languageCode.equals("BG")){
            message = "Καλώς ήρθατε στο Ship In Time! Ο κωδικός εισόδου σας είναι ο " + formattedCode;
        } else if("GB"){
            message = "Καλώς ήρθατε στο Ship In Time! Ο κωδικός εισόδου σας είναι ο " + formattedCode;
        } else if("AL"){
            message = "Καλώς ήρθατε στο Ship In Time! Ο κωδικός εισόδου σας είναι ο " + formattedCode;
        }

        Message message = Message.builder()
                .text(message)
                .sender_id("ShipInTime")
                .build();

        String phoneNumber = (request.getPhoneNumber().length()>10)?request.getPhoneNumber(): "30"+request.getPhoneNumber();
        Subscriber subscriber = Subscriber.builder()
                .number(phoneNumber)
                .build();

        SmsRequest smsRequest =
                SmsRequest.builder()
                .message(message)
                        .subscribers(new Subscriber[]{subscriber})
                        .build();

        apifonRest.sendSms(token, smsRequest);
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
        var token = Token.builder().userId(user.getId()).token(jwtToken).tokenType(TokenType.BEARER).revoked(false).expired(false).expirationDate(expirationDate).build();

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
                var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public Map checkToken() throws IOException {
        String userId = jwtService.getUserId();
        return Collections.singletonMap("active",true);
    }


}
