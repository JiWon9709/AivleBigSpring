package kt.aivle.rslt_list.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.clnt_co.model.TestList;
import kt.aivle.rslt_list.model.JutaekInfo;
import kt.aivle.rslt_list.model.JutaekListRequest;

import java.util.List;

@DATA_SOURCE
public interface RsltListMapper {

  /**
   * 현재 활성화되어있는 공고 sn 찾기
   *
   * @return
   */
  public int getActiveGongoSn();

  /**
   * 공고 sn으로 해당하는 공고 dtlsn 찾기
   *
   * @param sn
   * @return
   */
  public List<Integer> getDtlSnByGongoSn(int sn);

  /**
   * 공고 dtlsn으로 해당하는 주택 sn 찾기
   * @param gongoDtlSnList
   * @return
   */
  public List<Integer> getJutaekSnByDtlSn(List<Integer> gongoDtlSnList);

  /**
   * 주택 sn으로 주택 리스트 찾기 cnt
   * @param jutaekListRequest
   * @return
   */
  public int getJutaekListCnt(JutaekListRequest jutaekListRequest);

  /**
   * 주택 sn으로 주택 리스트 찾기
   * @param jutaekListRequest
   * @return
   */
  public List<JutaekInfo> getJutaekList(JutaekListRequest jutaekListRequest);

  /**
   * 결과 리스트
   *
   * @return
   */
  public List<TestList> rsltList();
}
