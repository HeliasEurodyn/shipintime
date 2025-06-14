package com.enershare.controller.camera_track;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.service.camera_track.CameraTrackService;
import com.enershare.service.company.CompanyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/camera-track")
public class CameraTrackController {

    @Autowired
    private CameraTrackService cameraTrackService;

    @GetMapping
    public String getPlates(@RequestParam("from") String from,
                         @RequestParam("to") String to) throws JsonProcessingException {
        return cameraTrackService.getPlates(from, to);
    }

    @GetMapping(path = "/ignore-list")
    public String getIgnoreList() throws JsonProcessingException {
        return cameraTrackService.getIgnoreList();
    }

    @PostMapping(path = "/to-ignore-list")
    public void addToIgnoreList(@RequestParam("plate") String plate) throws JsonProcessingException {
        cameraTrackService.addToIgnoreList(plate);
    }

    @PostMapping(path = "/remove-from-ignore-list")
    public void removeFromIgnoreList(@RequestParam("plate") String plate) throws JsonProcessingException {
        cameraTrackService.removeFromIgnoreList(plate);
    }

}
