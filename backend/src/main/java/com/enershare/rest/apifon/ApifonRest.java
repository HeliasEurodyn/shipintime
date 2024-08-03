package com.enershare.rest.apifon;

import com.enershare.dto.apifon.SmsRequest;
import com.enershare.dto.apifon.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApifonRest {

    @Autowired
    private RestTemplate restTemplate;

    public String auth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "ugO1NP2NEGVIoelPtBGRYq8dLcY4KJJEayAxRjWLgHjxRbqqej8shk8kkwshpS9u");
        params.put("client_secret", "ICA6kVuezNd217RCnsq_fxw4b8ga");
        params.put("scope", "smsGateway");

        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (requestBody.length() > 0) {
                requestBody.append("&");
            }
            requestBody.append(entry.getKey()).append("=").append(entry.getValue());
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange("https://ids.apifon.com/oauth2/token", HttpMethod.POST, requestEntity, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            TokenResponse tokenResponse = mapper.readValue(response.getBody(), TokenResponse.class);
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token response", e);
        }
    }

    public String sendSms(String token, SmsRequest smsRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<SmsRequest> requestEntity = new HttpEntity<>(smsRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange("https://ars.apifon.com/services/api/v1/sms/send", HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

}
