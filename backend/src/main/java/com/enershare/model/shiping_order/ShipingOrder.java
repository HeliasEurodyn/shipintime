package com.enershare.model.shiping_order;

import com.enershare.enums.TokenType;
import com.enershare.model.base.BaseEntity;
import com.enershare.model.base.MainEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shiping_order")
@Accessors(chain = true)
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
    private String s1truck;

    @Column
    private String truck;

    @Column(updatable = false)
    private boolean checkedIn;

    @Column(updatable = false)
    private Instant checkedInDate;

    @Column(updatable = false)
    private boolean loading;

    @Column(updatable = false)
    private Instant loadingDate;

    @Column(updatable = false)
    private boolean executed;

    @Column(updatable = false)
    private Instant executionDate;


    @Column(updatable = false)
    private Integer status;
}
