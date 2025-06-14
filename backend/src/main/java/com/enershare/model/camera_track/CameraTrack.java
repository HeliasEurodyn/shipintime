package com.enershare.model.camera_track;

import com.enershare.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CameraTrack")
@Table(name = "camera_track")
@Accessors(chain = true)
public class CameraTrack extends BaseEntity {

    @Column(name = "plate")
    private String plate;

    @Column(name = "tracktime")
    private Instant tracktime;

}
