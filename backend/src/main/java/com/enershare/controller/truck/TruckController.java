package com.enershare.controller.truck;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.dto.truck.TruckDTO;
import com.enershare.repository.truck.TruckRepository;
import com.enershare.service.truck.TruckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/truck")
public class TruckController {

    @Autowired
    public TruckService truckService;

    @GetMapping("/by-current-user")
    public List<TruckDTO> getByCurrentUser() {
        return truckService.getByCurrentUser();
    }

}
