package kt.aivle.base;


import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 리스트 요청시
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseListModel extends BaseModel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 페이지번호
   */
  private int pageNum;

  /**
   * 페이지 offset
   */
  private int pageOffset;

  /**
   * 페이지사이즈
   */
  private int pageSize;

  public BaseListModel() {
    this.pageSize = 20;
    this.pageNum = 1;
    this.pageOffset = 0;
  }

  public void changeCurrunt() {
    this.pageOffset = (this.pageNum - 1) * this.pageSize;
  }

}
