package com.enershare.rest.s1.shiping_order;

import com.enershare.dto.apifon.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class ShipingOrderRest {

    @Autowired
    private RestTemplate restTemplate;

    public  String login() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("Service", "login");
        body.put("Username", "webuser");
        body.put("Password", "123456789");
        body.put("AppId", "10001");
        body.put("Company", "1001");
        body.put("Branch", "1000");
        body.put("Module", "0");
        body.put("RefId", "1");

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange("https://agrohellas.oncloud.gr/s1services", HttpMethod.POST, request, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] compressedData = response.getBody();
            String decompressedData = decompressGzip(compressedData, "windows-1253");

            Map tokenResponse = mapper.readValue(decompressedData, Map.class);
            return tokenResponse.get("clientID").toString();
        } else {
            return null;
        }
    }

    public void checkIn(String id, String clientId) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("FINDOC", id);

        if(!clientId.equals("")){
            body.put("clientID", clientId);
        }

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimeContoller/orderCheckIn", HttpMethod.POST,
                request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseString = response.getBody();
        }

    }

    public static String decompressGzip(byte[] compressed, String charset) {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
             InputStreamReader reader = new InputStreamReader(gis, Charset.forName(charset));
             BufferedReader in = new BufferedReader(reader)) {

            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                outStr.append(line);
            }
            return outStr.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress GZIP response", e);
        }
    }

}
