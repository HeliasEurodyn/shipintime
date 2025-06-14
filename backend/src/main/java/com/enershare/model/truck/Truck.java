package com.enershare.model.truck;

import com.enershare.model.base.MainEntity;
import com.enershare.model.company.Company;
import com.enershare.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Truck")
@Table(name = "truck")
@Accessors(chain = true)
public class Truck extends MainEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "gross_weight")
    private Double grossWeight;

    @Column(name = "net_weight")
    private Double netWeight;

    @Column(name = "tare_weight")
    private Double tareWeight;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Company.class)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

}
