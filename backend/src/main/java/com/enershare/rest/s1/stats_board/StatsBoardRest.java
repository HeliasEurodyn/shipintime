package com.enershare.rest.s1.stats_board;

import com.enershare.rest.GZipHelper;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
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
            String decompressedData = GZipHelper.decompressGzip(compressedData, "windows-1253");
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

    public String getWaitingStates(String from, String to) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Optionally advertise we accept gzip; many servers compress only when asked:
        headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");

        Map<String, String> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/getWaitingStates",
                HttpMethod.POST,
                request,
                byte[].class
        );

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

    public void checkInSelected(Object body) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimePlateController/checkInSelected", HttpMethod.POST,
                request, byte[].class);

    }

    public String getOrderDetails(String findoc) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("findoc", findoc);
        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime.ShipInTimeSaldocController/getFindocDetailsById", HttpMethod.POST,
                request, byte[].class);


        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] compressedData = response.getBody();
            String decompressedData = GZipHelper.decompressGzip(compressedData, "windows-1253");
            return decompressedData;

        } else {
            return null;
        }

    }

}
