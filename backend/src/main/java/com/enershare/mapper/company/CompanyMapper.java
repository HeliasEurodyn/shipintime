package com.enershare.mapper.company;


import com.enershare.dto.company.CompanyDTO;
import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.mapper.base.BaseMapper;
import com.enershare.model.company.Company;
import com.enershare.model.shiping_order.ShipingOrder;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CompanyMapper extends BaseMapper<CompanyDTO, Company> {

    public List<Company> toEntities(List<CompanyDTO> all) {
        List<Company> companies = new ArrayList<>();

        all.stream().forEach(dto -> {
            Company model = this.map(dto);
            model.setId(dto.getId());
            companies.add(model);
        });

        return companies;
    }

}
