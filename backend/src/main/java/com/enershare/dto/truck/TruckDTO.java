package com.enershare.dto.truck;

import com.enershare.dto.base.BaseDTO;
import com.enershare.dto.company.CompanyDTO;
import com.enershare.dto.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TruckDTO extends BaseDTO {
    private String code;

    private String name;

    private String type;

    private Boolean active;

    private String grossWeight;

    private String netWeight;

    private String tareWeight;

//    private UserDTO user;

    private CompanyDTO company;

}
