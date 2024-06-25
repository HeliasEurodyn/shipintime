package com.enershare.controller.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.service.shiping_order.ShipingOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

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

    @GetMapping(path = "/by-id")
    ShipingOrderDTO getObject(@RequestParam("id") String id) throws Exception {
        return shipingOrderService.getObject(id);
    }

    @PostMapping
    public ShipingOrderDTO postObject(@RequestBody ShipingOrderDTO shipingOrderDTO) throws IOException {
        return shipingOrderService.postObject(shipingOrderDTO);
    }

    @PutMapping
    public ShipingOrderDTO putObject(@RequestBody ShipingOrderDTO shipingOrderDTO) {
        return shipingOrderService.postObject(shipingOrderDTO);
    }

    @DeleteMapping
    public void deleteObject(@RequestParam("id") String id) throws Exception {
        shipingOrderService.deleteObject(id);
    }

}
