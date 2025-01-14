package kt.aivle.base;

import java.util.List;
import lombok.Data;

/**
 * @author sensiakwon
 */
@Data
public class ExcelFormInfo {

  //엑셀다운로드 파일명
  private String fileNm;

  //조회 컬럼들
  private List<String> columns;

  //엑셀헤드명(컬럼 이름 - 컬럼과 1:1 순차매칭)
  private List<String> names;

}
