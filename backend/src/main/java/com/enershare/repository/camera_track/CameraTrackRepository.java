package com.enershare.repository.camera_track;

import com.enershare.model.camera_track.CameraTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CameraTrackRepository extends JpaRepository<CameraTrack, String> {

    @Query("""
    SELECT ct FROM CameraTrack ct
    WHERE ct.plate IN :plates
    AND ct.tracktime >= :startOfDay AND ct.tracktime < :endOfDay
""")
    List<CameraTrack> findTracksForDay(
            @Param("plates") List<String> plates,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
    );

}
