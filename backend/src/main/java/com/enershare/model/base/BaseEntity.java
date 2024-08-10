package com.enershare.model.base;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;


import java.util.UUID;

@Data
@MappedSuperclass
@OptimisticLocking(type = OptimisticLockType.VERSION)
@EqualsAndHashCode
@Accessors(chain = true)
public class BaseEntity {

    @Id
    @Column( updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @PrePersist
    @PreUpdate
    void setIdIfMissing() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
