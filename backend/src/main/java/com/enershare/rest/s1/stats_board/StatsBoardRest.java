package com.enershare.rest.s1.stats_board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
public class StatsBoardRest {


    @Value("${s1.api.pwd}")
    private String pwd;

    @Autowired
    private RestTemplate restTemplate;


    public String getIgnoreList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/getIgnoreListPlates", HttpMethod.POST,
                request, byte[].class);


        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] compressedData = response.getBody();
            String decompressedData = decompressGzip(compressedData, "windows-1253");
            return decompressedData;
        } else {
            return null;
        }

    }

    public String getOrderStats(String from, String to) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimeSaldocController/getBoardStats", HttpMethod.POST,
                request, byte[].class);


        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] compressedData = response.getBody();
            String decompressedData = decompressGzip(compressedData, "windows-1253");
            return decompressedData;

        } else {
            return null;
        }

    }

    public String getWaitingStates(String from, String to) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/getWaitingStates", HttpMethod.POST,
                request, byte[].class);


        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] compressedData = response.getBody();
            String decompressedData = decompressGzip(compressedData, "windows-1253");
            return decompressedData;

        } else {
            return null;
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

    public void addToIgnoreList(String plate) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("plate", plate);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/addPlateToIgnoreList", HttpMethod.POST,
                request, byte[].class);

    }

    public void removeFromIgnoreList(String plate) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("plate", plate);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/removePlateFromIgnoreList", HttpMethod.POST,
                request, byte[].class);

    }

}
