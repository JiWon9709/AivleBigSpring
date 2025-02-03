package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImgRegRequest {
  private static final long serialVersionUID = 1L;

  /**
   * 사진리스트
   */
  @ApiModelProperty(required = true, notes = "사진리스트", position = 1)
  private List<MultipartFile> imgList;

  /**
   * 참조테이블
   */
  @ApiModelProperty(required = true, notes = "참조테이블", position = 2)
  private String refTable;

  /**
   * 참조테이블고유번호
   */
  @ApiModelProperty(required = true, notes = "참조테이블고유번호", position = 3)
  private Long refSn;

}
