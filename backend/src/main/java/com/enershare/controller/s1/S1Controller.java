package com.enershare.controller.s1;

import com.enershare.service.s1.S1Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/s1")
public class S1Controller {

    @Autowired
    private S1Service s1Service;

    @PostMapping(path = "/request/{objclass}/{objfunction}")
    public String request(@PathVariable String objclass,
                                  @PathVariable String objfunction,
                                  @RequestBody Map<String, String> parameters ) throws JsonProcessingException {
        return s1Service.request(objclass, objfunction, parameters);
    }
}
