package com.enershare.rest.s1.s1;

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
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@Service
public class S1Rest {

    @Value("${s1.api.pwd}")
    private String pwd;

    @Autowired
    private RestTemplate restTemplate;

    public String request(String objclass, String objfunction, Map<String, String> body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        body.put("pwd", pwd);

        String requestBody = mapper.writeValueAsString(body);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://agrohellas.oncloud.gr/s1services/JS/shipInTime."+objclass+"/"+objfunction, HttpMethod.POST,
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

}
