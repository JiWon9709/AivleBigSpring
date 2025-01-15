package kt.aivle.clnt_co.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.clnt_co.model.*;

import java.util.List;

@DATA_SOURCE
public interface ClntCoMapper {


  /**
   * 테스트 삽입
   *
   * @param testRequest
   * @return
   */
  public int regTest(TestRequest testRequest);

  /**
   * 테스트리스트
   *
   * @param testRequest
   * @return
   */
  public List<TestList> testList(TestRequest testRequest);
}
