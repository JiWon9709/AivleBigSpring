package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImgEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 참조테이블
   */
  @ApiModelProperty(required = true, notes = "참조테이블", position = 1)
  private String refTable;

  /**
   * 참조테이블고유번호
   */
  @ApiModelProperty(required = true, notes = "참조테이블고유번호", position = 2)
  private Long refSn;

  /**
   * 경로
   */
  @ApiModelProperty(required = true, notes = "경로", position = 3)
  private String path;

  /**
   * 파일명
   */
  @ApiModelProperty(required = true, notes = "파일명", position = 4)
  private String fileName;

  /**
   * 원본파일명
   */
  @ApiModelProperty(required = true, notes = "원본파일명", position = 5)
  private String oriFileName;

  /**
   * 확장자
   */
  @ApiModelProperty(required = true, notes = "확장자", position = 6)
  private String ext;

}
