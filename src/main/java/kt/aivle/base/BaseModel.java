package kt.aivle.base;

import java.io.Serializable;
import lombok.Data;

/**
 * 리스트 이외 요청시
 */
@Data
public class BaseModel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 로그인고유번호
   */
  private Long userSn;

  /**
   * 로그인아이디
   */
  private String userId;

}
