package com.enershare.dto.shiping_order;

import com.enershare.dto.base.BaseDTO;
import com.enershare.model.base.MainEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ShipingOrderDTO extends BaseDTO {

    private String s1id;

    private String s1Code;

    private Integer s1Number;

    private Instant shipingDate;

    private Instant insDate;

    private String ownerId;

    private boolean checkedIn;

    private Instant checkedInDate;

    private boolean executed;

    private Instant executionDate;
}
