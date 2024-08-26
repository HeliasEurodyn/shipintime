package com.enershare.controller.auth;

import com.enershare.dto.auth.AuthenticationRequestSmsOtp;
import com.enershare.dto.auth.AuthenticationRequestUp;
import com.enershare.dto.auth.AuthenticationResponse;
import com.enershare.service.auth.AuthenticationService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/request-sms-otp-authentication")
    public Map requestAuthentication(@RequestBody AuthenticationRequestSmsOtp request) {
        return authenticationService.requestAuthentication(request);
    }

    @PostMapping("/authenticate-sms-otp")
    public ResponseEntity<AuthenticationResponse> authenticateSmsOtp(@RequestBody AuthenticationRequestSmsOtp request) {
        return ResponseEntity.ok(authenticationService.authenticateSmsOtp(request));
    }

    @PostMapping("/authenticate-up")
    public ResponseEntity<AuthenticationResponse> authenticateUp(@RequestBody AuthenticationRequestUp request) {
        return ResponseEntity.ok(authenticationService.authenticateUp(request));
    }

//    @PostMapping("/authenticate-store")
//    public ResponseEntity<AuthenticationResponse> authenticateStore(@RequestBody AuthenticationRequestUp request) {
//        return ResponseEntity.ok(authenticationService.authenticateUp(request));
//    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, java.io.IOException {
        authenticationService.refreshToken(request, response);
    }

    @GetMapping("/check-token")
    public Map checkToken() throws IOException, java.io.IOException {
        return authenticationService.checkToken();
    }

}
