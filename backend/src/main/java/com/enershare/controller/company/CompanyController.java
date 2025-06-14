package com.enershare.controller.company;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.service.company.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping(path = "/sync")
    public void sync(@RequestBody List<CompanyDTO> companyDTOS) {
        companyService.sync(companyDTOS);
    }

    @PostMapping(path = "/sync/force")
    public void syncForce(@RequestBody List<CompanyDTO> companyDTOS) {
        companyService.syncForce(companyDTOS);
    }

    @GetMapping("/by-current-user")
    public List<CompanyDTO> getByCurrentUser() {
        return companyService.getByCurrentUser();
    }

}
