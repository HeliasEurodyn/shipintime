package com.enershare.controller.shiping_order;

import com.enershare.dto.shiping_order.CameraTrackDTO;
import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.dto.shiping_order.ShipingOrderLoadDTO;
import com.enershare.service.shiping_order.ShipingOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/shiping-order")
public class ShipingOrderController {

    @Autowired
    private ShipingOrderService shipingOrderService;

    @GetMapping
    List<ShipingOrderDTO> getObject() {
        return shipingOrderService.getObject();
    }

    @GetMapping(path = "/period")
    List<ShipingOrderDTO> getOnPeriod(@RequestParam("from") Instant from,
                                    @RequestParam("to") Instant to) {
        return shipingOrderService.getOnPeriod(from, to);
    }

    @GetMapping(path = "/all/period")
    List<ShipingOrderDTO> getAllOnPeriod(@RequestParam("from") Instant from,
                                      @RequestParam("to") Instant to) {
        return shipingOrderService.getAllOnPeriod(from, to);
    }

    @GetMapping(path = "/by-id")
    ShipingOrderDTO getObject(@RequestParam("id") String id) throws Exception {
        return shipingOrderService.getObject(id);
    }

    @DeleteMapping
    public void deleteObject(@RequestParam("id") String id) throws Exception {
        shipingOrderService.deleteObject(id);
    }

    @PostMapping(path = "/sync")
    public void sync(@RequestBody List<ShipingOrderDTO> shipingOrdersDTO) {
        shipingOrderService.sync(shipingOrdersDTO);
    }

    @PostMapping(path = "/sync/force")
    public void syncForce(@RequestBody List<ShipingOrderDTO> shipingOrdersDTO) {
        shipingOrderService.syncForce(shipingOrdersDTO);
    }

    @PostMapping(path = "/creation-notify")
    public void creationNotify(@RequestBody Map<String, String> parameters) {
        shipingOrderService.creationNotify(parameters.get("id"));
    }

    @PostMapping(path = "/check-in")
    public void checkIn(@RequestBody Map<String, String> parameters) throws IOException {
        shipingOrderService.checkIn(parameters.get("id"));
    }

    @PostMapping(path = "/check-in/by-camera-tracks")
    public void checkInByPlates(@RequestBody List<CameraTrackDTO> cameraTracks) throws JsonProcessingException {
        shipingOrderService.checkInByCameraTrack(cameraTracks);
    }

    @PostMapping(path = "/load")
    public void load(@RequestBody ShipingOrderLoadDTO shipingOrdersDTO) {
        shipingOrderService.load(shipingOrdersDTO);
    }

    @PostMapping(path = "/su/check-in")
    public void suCheckIn(@RequestBody Map<String, String> parameters) {
        shipingOrderService.suCheckIn(parameters.get("id"));
    }

    @PostMapping(path = "/su/load")
    public void suLoad(@RequestBody Map<String, String> parameters) {
        shipingOrderService.suLoad(parameters.get("id"));
    }

    @PostMapping(path = "/su/execute")
    public void execute(@RequestBody Map<String, String> parameters) {
        shipingOrderService.execute(parameters.get("id"));
    }

    @PostMapping(path = "/su/reset")
    public void reset(@RequestBody Map<String, String> parameters) {
        shipingOrderService.reset(parameters.get("id"));
    }

//    @PostMapping
//    public ShipingOrderDTO postObject(@RequestBody ShipingOrderDTO shipingOrderDTO) {
//        return shipingOrderService.postObject(shipingOrderDTO);
//    }
//
//    @PutMapping
//    public ShipingOrderDTO putObject(@RequestBody ShipingOrderDTO shipingOrderDTO) {
//        return shipingOrderService.postObject(shipingOrderDTO);
//    }

}
