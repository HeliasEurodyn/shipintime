package com.enershare.rest.plate;

import com.enershare.dto.apifon.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlateImageRest {

    @Autowired
    private RestTemplate restTemplate;

    private String baseUrl = "http://192.168.2.34:18085";

    /**
     * Calls the C# /search endpoint and returns the JSON as a Map.
     */
    public Map<String, Object> searchPlateNames(Instant startTime, Instant endTime, String plateSearchMask) {
        String url = baseUrl + "/search";

        Map<String, Object> body = new HashMap<>();
        body.put("startTime", startTime.toString());
        body.put("endTime", endTime.toString());
        body.put("plateSearchMask", plateSearchMask);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }

    /**
     * Calls the C# /image endpoint and returns the raw bytes (e.g., image/jpeg).
     */
    public byte[] fetchImageBytes(String name) {
        String url = baseUrl + "/image";

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.IMAGE_JPEG, MediaType.ALL));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
        return response.getBody();
    }
}
