package com.enershare.rest.s1.s1;

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

}
