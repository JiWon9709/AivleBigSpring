package kt.aivle.base;

import java.io.Serializable;
import lombok.Data;

@Data
public class BaseResModel<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private int resultCode =  BaseMsg.SUCCESS.getCode();

  private String resultMsg = BaseMsg.SUCCESS.getValue();

  private T data;

}
