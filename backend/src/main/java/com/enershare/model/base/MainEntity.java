package com.enershare.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@MappedSuperclass
@OptimisticLocking(type = OptimisticLockType.VERSION)
@EqualsAndHashCode
@Accessors(chain = true)
public abstract class MainEntity extends BaseEntity {

    @CreatedDate
    @Column(name = "created_on", updatable = false, nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdOn;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "modified_on", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant modifiedOn;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "short_order")
    private Long shortOrder;

}
