package com.enershare.service.stats_board;

import com.enershare.rest.s1.stats_board.StatsBoardRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsBoardService {

    @Autowired
    private StatsBoardRest statsBoardRest;

    public String getOrderStats(String from, String to) throws JsonProcessingException {
       return statsBoardRest.getOrderStats(from, to);
    }


    public String getPlateWaitingStates(String from, String to) throws JsonProcessingException {
        return statsBoardRest.getWaitingStates(from, to);
    }

    public void checkInSelected(Object body) throws JsonProcessingException {
         statsBoardRest.checkInSelected(body);
    }
}
