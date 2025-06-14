package com.enershare.dto.shiping_order;

import com.enershare.dto.base.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode()
@Accessors(chain = true)

public class CameraTrackDTO extends BaseDTO {

    private String plate;

    @JsonFormat(pattern = "yyyyMMdd'T'HHmmss'Z'", timezone = "UTC")
    private Instant tracktime;

}
