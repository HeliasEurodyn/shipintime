package com.enershare.controller.notifications;

import com.enershare.config.MyWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private MyWebSocketHandler myWebSocketHandler;

    @PostMapping("/vehicle-traced")
    public void vehicleTraced(@RequestBody Map<String, String> parameters) {

        String formattedNow = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        // HTML with dynamic date injection
//        String html = """
//                <div class="card-body">
//                  <p class="mb-1">
//                    <i class="bi bi-images size-16"></i>
//                    <small class="text-opac text-color-theme">&nbsp; Πινακίδα</small>
//                  </p>
//                  <a class="text-normal">
//                    <h6 class="text-color-theme">%s</h6>
//                  </a>
//                  <div class="row">
//                    <div class="col">
//                      <p class="mb-0">
//                        <small>Ημ. Κάμερας: %s</small><br />
//                      </p>
//                    </div>
//                  </div>
//                </div>
//                """.formatted(parameters.get("plate"), parameters.get("tracktime"));


        Map<String, String> payload = Map.of(
                "type", parameters.get("direction").equals("forward") ? "vehicle-forward" : "vehicle-reversed",
                "plate", parameters.get("plate"),
                "tracktime", parameters.get("tracktime"),
                "id", parameters.get("id"),
                "direction", parameters.get("direction")
        );

        myWebSocketHandler.broadcastObject(payload);
    }

}
