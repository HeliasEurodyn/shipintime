package com.enershare.dto.shiping_order;

import com.enershare.dto.base.BaseDTO;
import com.enershare.model.base.MainEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
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

    private String s1truck;

    private String truck;

    private boolean checkedIn;

    private Instant checkedInDate;

    private boolean executed;

    private Instant executionDate;

    private Integer status;

    private boolean loading;

    private Instant loadingDate;

    private String customers;

}
