package com.enershare.service.s1;

import com.enershare.rest.s1.camera_track.CameraTrackRest;
import com.enershare.rest.s1.s1.S1Rest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class S1Service {

    @Autowired
    private S1Rest s1Rest;

    public String request(String objclass, String objfunction, Map<String, String> parameters) throws JsonProcessingException {
        return s1Rest.request(objclass, objfunction, parameters);
    }
}
