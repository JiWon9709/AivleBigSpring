package kt.aivle.gongo.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.gongo.model.Gongo;

import java.util.List;

@DATA_SOURCE
public interface GongoMapper {
    List<Gongo> selectActiveGongos();
    Gongo selectGongoByGongoSn(Integer gongoSn);
}
