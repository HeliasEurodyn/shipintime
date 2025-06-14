package com.enershare.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestSmsOtp {

    private String phoneNumber;

    private String prefix;

    private String logincode;

    private String language;

}
