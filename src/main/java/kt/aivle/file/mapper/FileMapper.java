package kt.aivle.file.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.file.model.Img;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DATA_SOURCE
public interface FileMapper {

    /**
     * 이미지 정보를 저장합니다.
     * @param img 이미지 정보 객체
     */
    void saveImage(Img img);

    /**
     * 참조 ID(refSn)에 해당하는 이미지 리스트를 조회합니다.
     * @param refSn 참조 ID
     * @return 이미지 리스트
     */
    List<Img> getImagesByRefSn(@Param("refSn") int refSn);

    /**
     * 이미지 정보를 소프트 삭제(useYn = 'N') 처리합니다.
     * @param imgSn 이미지 ID
     */
    void deleteImageBySn(@Param("imgSn") int imgSn);
}
