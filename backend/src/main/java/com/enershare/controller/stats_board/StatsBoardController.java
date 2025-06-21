package com.enershare.controller.stats_board;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.service.camera_track.CameraTrackService;
import com.enershare.service.stats_board.StatsBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/stats-board")
public class StatsBoardController {

    @Autowired
    private StatsBoardService statsBoardService;

    @GetMapping(path = "/orders")
    public String getPlates(@RequestParam("from") String from,
                            @RequestParam("to") String to) throws JsonProcessingException {
        return statsBoardService.getOrderStats(from, to);
    }

    @GetMapping(path = "/plate-waiting-states")
    public String getPlateWaitingStates(@RequestParam("from") String from,
                            @RequestParam("to") String to) throws JsonProcessingException {
        return statsBoardService.getPlateWaitingStates(from, to);
    }

    @PostMapping(path = "/check-in-selected")
    public void checkInSelected(@RequestBody Object body) throws JsonProcessingException {
        statsBoardService.checkInSelected(body);
    }

    @GetMapping(path = "/order-details")
    public String getOrderDetails(@RequestParam("findoc") String findoc) throws JsonProcessingException {
        return statsBoardService.getOrderDetails(findoc);
    }

}
