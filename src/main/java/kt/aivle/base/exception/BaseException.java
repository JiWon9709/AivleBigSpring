package kt.aivle.base.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BaseException(String errorMsg) {

    super(errorMsg);
  }

}
