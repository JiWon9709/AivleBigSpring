package kt.aivle.base;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 리스트형 response data
 */
@Data
public class BaseResListModel<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private int resultCode =  BaseMsg.SUCCESS.getCode();

  private String resultMsg = BaseMsg.SUCCESS.getValue();

  /**
   * 페이지번호
   */
  private int pageNum;

  /**
   * 페이지사이즈
   */
  private int pageSize;

  private int totalCount;

  private List<T> data;

}
