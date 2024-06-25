package com.enershare.mapper.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.mapper.base.BaseMapper;
import com.enershare.model.shiping_order.ShipingOrder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Collections;

@Mapper(componentModel = "spring")
public abstract class ShipingOrderMapper extends BaseMapper<ShipingOrderDTO, ShipingOrder> {
}

