package com.enershare.model.shiping_order;

import com.enershare.enums.TokenType;
import com.enershare.model.base.BaseEntity;
import com.enershare.model.base.MainEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shiping_order")
public class ShipingOrder extends MainEntity {

    @Column
    private String s1id;

    @Column
    private String s1Code;

    @Column
    private Integer s1Number;

    @Column
    private Instant shipingDate;

    private Instant insDate;

    @Column(name = "owner_id")
    private String ownerId;

    @Column
    private boolean checkedIn;

    @Column
    private Instant checkedInDate;

    @Column
    private boolean executed;

    @Column
    private Instant executionDate;
}
