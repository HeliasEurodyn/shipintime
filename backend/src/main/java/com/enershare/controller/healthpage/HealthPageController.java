package com.enershare.controller.healthpage;

import com.enershare.dto.auth.AuthenticationRequestSmsOtp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/health-page")
public class HealthPageController {

    @GetMapping
    String getObject() {
        return "ok";
    }

    @PostMapping
    String samplePost() {
        return "ok";
    }

}
