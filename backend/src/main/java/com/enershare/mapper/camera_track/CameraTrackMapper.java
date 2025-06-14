package com.enershare.mapper.camera_track;

import com.enershare.dto.shiping_order.CameraTrackDTO;
import com.enershare.mapper.base.BaseMapper;
import com.enershare.model.camera_track.CameraTrack;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CameraTrackMapper extends BaseMapper<CameraTrackDTO, CameraTrack> {
}
