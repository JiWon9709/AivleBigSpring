package kt.aivle.base;


import java.io.Serializable;
import lombok.Data;

@Data
public class BaseDataModel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 에러 코드
   */
  private int errorCode;

  /**
   * 에러 메시지
   */
  private String errorMsg;

}
