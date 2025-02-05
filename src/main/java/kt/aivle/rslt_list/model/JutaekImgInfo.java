package kt.aivle.rslt_list.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class JutaekImgInfo {
  private static final long serialVersionUID = 1L;

  /**
   * 경로
   */
  @ApiModelProperty(required = true, notes = "경로", position = 1)
  private String path;

  /**
   * 파일명
   */
  @ApiModelProperty(required = true, notes = "파일명", position = 2)
  private String fileName;

}
