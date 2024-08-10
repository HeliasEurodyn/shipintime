package com.enershare.mapper.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.mapper.base.BaseMapper;
import com.enershare.model.shiping_order.ShipingOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring")
public abstract class ShipingOrderMapper extends BaseMapper<ShipingOrderDTO, ShipingOrder> {

    public List<ShipingOrder> toEntities(List<ShipingOrderDTO> all) {
        List<ShipingOrder> shipingOrders = new ArrayList<>();

        all.stream().forEach(dto -> {
            ShipingOrder model = this.map(dto);
            model.setId(dto.getId());
            shipingOrders.add(model);
        });

        return shipingOrders;
    }

}

