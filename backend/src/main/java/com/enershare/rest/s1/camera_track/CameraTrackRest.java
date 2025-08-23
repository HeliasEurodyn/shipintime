package com.enershare.rest.s1.camera_track;

import com.enershare.rest.GZipHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@Service
public class CameraTrackRest {

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


        if (response.getStatusCode() != HttpStatus.OK) return null;

        byte[] raw = response.getBody();
        if (raw == null) return null;

        String contentEncoding = Optional.ofNullable(response.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING))
                .map(String::toLowerCase)
                .orElse("");

        // Determine charset; default to windows-1253 if server doesn’t specify
        Charset charset = Optional.ofNullable(response.getHeaders().getContentType())
                .map(MediaType::getCharset)
                .orElse(Charset.forName("windows-1253"));

        try {
            byte[] decoded;

            if (contentEncoding.contains("gzip") || GZipHelper.looksLikeGzip(raw)) {
                decoded = GZipHelper.gunzip(raw);
            } else if (contentEncoding.contains("deflate")) {
                decoded = GZipHelper.inflate(raw);
            } else {
                decoded = raw; // plain
            }

            return new String(decoded, charset);
        } catch (IOException io) {
            // Optional: add logging with details and the first bytes for diagnostics
            throw new RuntimeException("Failed to decode response body", io);
        }

    }

    public String getPlates(String from, String to) throws JsonProcessingException {
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
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/getPlates", HttpMethod.POST,
                request, byte[].class);

        if (response.getStatusCode() != HttpStatus.OK) return null;

        byte[] raw = response.getBody();
        if (raw == null) return null;

        String contentEncoding = Optional.ofNullable(response.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING))
                .map(String::toLowerCase)
                .orElse("");

        // Determine charset; default to windows-1253 if server doesn’t specify
        Charset charset = Optional.ofNullable(response.getHeaders().getContentType())
                .map(MediaType::getCharset)
                .orElse(Charset.forName("windows-1253"));

        try {
            byte[] decoded;

            if (contentEncoding.contains("gzip") || GZipHelper.looksLikeGzip(raw)) {
                decoded = GZipHelper.gunzip(raw);
            } else if (contentEncoding.contains("deflate")) {
                decoded = GZipHelper.inflate(raw);
            } else {
                decoded = raw; // plain
            }

            return new String(decoded, charset);
        } catch (IOException io) {
            // Optional: add logging with details and the first bytes for diagnostics
            throw new RuntimeException("Failed to decode response body", io);
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
