package kt.aivle.gongo1111.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.gongo1111.model.dto.GongoDTO;

import java.util.List;

@DATA_SOURCE
public interface GongoMapper {
    List<GongoDTO> selectActiveGongos();
    GongoDTO selectGongoByGongoSn(Integer gongoSn);
}
