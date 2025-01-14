package kt.aivle.base;

import lombok.Data;

/**
 * API 데이터를 위한 Map구조체
 */
@Data
public class BaseMap {

  private String key;
  private Object value;

  public BaseMap(String key, Object value) {
    this.key = key;
    this.value = value;
  }
}
