package com.enershare.controller.stats_board;

import com.enershare.rest.plate.PlateImageRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/plate-image")
public class PlateImageController {

    @Autowired
    private PlateImageRest plateImageRest;

    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, Object> body) {
        Instant startTime = Instant.parse(body.get("startTime").toString());
        Instant endTime = Instant.parse(body.get("endTime").toString());
        String plateSearchMask = body.get("plateSearchMask").toString();

        log.info("Searching plates between {} and {} for mask '{}'", startTime, endTime, plateSearchMask);
        return plateImageRest.searchPlateNames(startTime, endTime, plateSearchMask);
    }

    /**
     * Get an image by its "name" from the C# service.
     * Example: GET /plate-image/image?name=ch01_0000000019700052144_01@20250809065409_NKX8120
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@RequestParam("name") String name) {
        log.info("Fetching image for name '{}'", name);
        byte[] imageBytes = plateImageRest.fetchImageBytes(name);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name + ".jpg\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Instant startTime = Instant.parse("2025-08-08T21:00:00Z");
        Instant endTime = Instant.parse("2025-08-09T20:59:59Z");
        String plateSearchMask = "NKX8120";

        log.info("Searching plates between {} and {} for mask '{}'", startTime, endTime, plateSearchMask);
        return plateImageRest.searchPlateNames(startTime, endTime, plateSearchMask);
    }

}
