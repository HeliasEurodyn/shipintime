package com.enershare.mapper.truck;

import com.enershare.dto.truck.TruckDTO;
import com.enershare.mapper.base.BaseMapper;
import com.enershare.model.truck.Truck;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class TruckMapper extends BaseMapper<TruckDTO, Truck> {
}
