package com.enershare.rest.plate;

import com.enershare.dto.apifon.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlateImageRest {

    @Autowired
    private RestTemplate restTemplate;

    // private String baseUrl = "http://193.92.90.132:18085";
    private String baseUrl = "http://192.168.2.34:18085";

    /**
     * Calls the C# /search endpoint and returns the JSON as a Map.
     */
//    public Map<String, Object> searchPlateNames(Instant startTime, Instant endTime, String plateSearchMask) {
//        String url = baseUrl + "/search";
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("startTime", startTime.toString());
//        body.put("endTime", endTime.toString());
//        body.put("plateSearchMask", plateSearchMask);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
//        return response.getBody();
//    }

    public Map<String, Object> searchPlateNames(Instant startTime, Instant endTime, String plateSearchMask) {
        String url = "https://shipintime.gr/api/camera/search/";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("startTime", startTime.toString())
                .queryParam("endTime", endTime.toString())
                .queryParam("plateSearchMask", plateSearchMask);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }



    /**
     * Calls the C# /image endpoint and returns the raw bytes (e.g., image/jpeg).
     */
//    public byte[] fetchImageBytes(String name) {
//        String url = baseUrl + "/image";
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("name", name);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(List.of(MediaType.IMAGE_JPEG, MediaType.ALL));
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
//        return response.getBody();
//    }

    public byte[] fetchImageBytes(String name) {
        // Build the URL with query parameter
        String url = String.format("https://shipintime.gr/api/camera/image/?name=%s",
                UriUtils.encodeQueryParam(name, StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.IMAGE_JPEG, MediaType.ALL));

        // GET request with no body
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        return response.getBody();
    }



}
