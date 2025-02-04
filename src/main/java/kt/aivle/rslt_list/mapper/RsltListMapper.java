package kt.aivle.rslt_list.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.clnt_co.model.TestList;
import kt.aivle.rslt_list.model.*;

import java.util.List;

@DATA_SOURCE
public interface RsltListMapper {

  /**
   * 방금 입력한 input sn 찾기
   *
   * @return
   */
  public Long getActiveInputSn(JutaekListRequest jutaekListRequest);

  /**
   * input sn으로 input 정보 받아오기
   *
   * @return
   */
  public InputInfo getInputInfo(Long inputSn);

  /**
   * 공고 sn으로 해당하는 공고 dtlsn 찾기
   *
   * @param sn
   * @return
   */
  public List<Integer> getDtlSnByGongoSn(Long sn);

  /**
   * 공고 dtlsn으로 해당하는 주택 sn 찾기
   *
   * @param gongoDtlSnList
   * @return
   */
  public List<Integer> getJutaekSnByDtlSn(List<Integer> gongoDtlSnList);

  /**
   * 주택 sn으로 주택 리스트 찾기 cnt
   *
   * @param jutaekListRequest
   * @return
   */
  public int getJutaekListCnt(JutaekListRequest jutaekListRequest);

  /**
   * 주택 sn으로 주택 리스트 찾기
   *
   * @param jutaekListRequest
   * @return
   */
  public List<JutaekInfo> getJutaekList(JutaekListRequest jutaekListRequest);

  /**
   * 주택 상세정보 조회
   *
   * @param jutaekDtlSn
   * @return
   */
  public JutaekDtlInfo getJutaekDtl(Long jutaekDtlSn);

  /**
   * 이미지 서버 등록
   *
   * @param imgEntity
   * @return
   */
  public int regImg(ImgEntity imgEntity);
}
