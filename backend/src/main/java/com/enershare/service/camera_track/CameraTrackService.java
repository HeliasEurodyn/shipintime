package com.enershare.service.camera_track;

import com.enershare.dto.shiping_order.CameraTrackDTO;
import com.enershare.model.camera_track.CameraTrack;
import com.enershare.repository.camera_track.CameraTrackRepository;
import com.enershare.rest.s1.camera_track.CameraTrackRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CameraTrackService {

    @Autowired
    private CameraTrackRest cameraTrackRest;

    public String getPlates(String from, String to) throws JsonProcessingException {
        return cameraTrackRest.getPlates(from, to);
    }

    public String getIgnoreList() throws JsonProcessingException {
        return cameraTrackRest.getIgnoreList();
    }

    public void addToIgnoreList(String plate) throws JsonProcessingException {
        cameraTrackRest.addToIgnoreList(plate);
    }

    public void removeFromIgnoreList(String plate) throws JsonProcessingException {
        cameraTrackRest.removeFromIgnoreList(plate);
    }


    @Autowired
    private CameraTrackRepository cameraTrackRepository;


    public void registerTracks(List<CameraTrackDTO> cameraTrackDTOs) {
        if (cameraTrackDTOs == null || cameraTrackDTOs.isEmpty()) return;

        // Group by plate and truncate tracktime to day (UTC)
        Map<String, List<CameraTrackDTO>> groupedByPlate = cameraTrackDTOs.stream()
                .collect(Collectors.groupingBy(CameraTrackDTO::getPlate));

        Set<CameraTrack> toSave = new HashSet<>();

        for (Map.Entry<String, List<CameraTrackDTO>> entry : groupedByPlate.entrySet()) {
            String plate = entry.getKey();
            List<CameraTrackDTO> plateTracks = entry.getValue();

            // Get unique days (UTC) for this plate
            Map<LocalDate, List<CameraTrackDTO>> byDate = plateTracks.stream()
                    .collect(Collectors.groupingBy(dto -> dto.getTracktime().atZone(ZoneOffset.UTC).toLocalDate()));

            for (Map.Entry<LocalDate, List<CameraTrackDTO>> dateEntry : byDate.entrySet()) {
                LocalDate day = dateEntry.getKey();
                Instant startOfDay = day.atStartOfDay().toInstant(ZoneOffset.UTC);
                Instant endOfDay = day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

                // Fetch existing tracks for this plate & day range
                List<CameraTrack> existingTracks = cameraTrackRepository.findTracksForDay(
                        List.of(plate), startOfDay, endOfDay
                );

                Set<Instant> existingTimes = existingTracks.stream()
                        .map(CameraTrack::getTracktime)
                        .collect(Collectors.toSet());

                for (CameraTrackDTO dto : dateEntry.getValue()) {
                    if (!existingTimes.contains(dto.getTracktime())) {
                        toSave.add(CameraTrack.builder()
                                .plate(dto.getPlate())
                                .tracktime(dto.getTracktime())
                                .build());
                    }
                }
            }
        }

        if (!toSave.isEmpty()) {
            cameraTrackRepository.saveAll(toSave);
        }
    }

}
