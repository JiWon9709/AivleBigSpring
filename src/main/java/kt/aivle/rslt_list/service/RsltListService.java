package kt.aivle.rslt_list.service;

import kt.aivle.base.BaseListModel;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.clnt_co.model.*;
import kt.aivle.rslt_list.mapper.RsltListMapper;
import kt.aivle.rslt_list.model.JutaekInfo;
import kt.aivle.rslt_list.model.JutaekInfoListResponse;
import kt.aivle.rslt_list.model.JutaekListRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RsltListService {
  private static final Logger log = LogManager.getLogger(RsltListService.class);
  @Autowired
  private RsltListMapper rsltListMapper;

  @Value("${file.path}")
  private String dir;

  /**
   * 테스트 리스트
   *
   * @return
   */
  @Transactional
  public JutaekInfoListResponse rsltList(BaseListModel baseListModel) {
    JutaekInfoListResponse result = new JutaekInfoListResponse();

    // 페이징 파라미터 전달
    result.setPageNum(baseListModel.getPageNum());
    result.setPageSize(baseListModel.getPageSize());
    // 페이지 계산
    if (baseListModel.getPageNum() > 0) {
      baseListModel.setPageOffset((baseListModel.getPageNum() - 1) * baseListModel.getPageSize());
    }

    // 일단 지금 사용중인 공고 sn을 찾고

    int activeGongo = rsltListMapper.getActiveGongoSn();
    if (activeGongo == 0) {
      result.setResultMsg(BaseMsg.FAILED.getValue());
      result.setResultCode(BaseMsg.FAILED.getCode());
    }

    // 공고 sn으로 해당하는 공고 dtl sn들을 받기
    List<Integer> dtlSnList = rsltListMapper.getDtlSnByGongoSn(activeGongo);
    if (dtlSnList.size() == 0) {
      result.setResultMsg(BaseMsg.FAILED.getValue());
      result.setResultCode(BaseMsg.FAILED.getCode());
    }

    // 공고 dtlsn으로 해당하는 주택 sn 찾기
    List<Integer> jutaekSnList = rsltListMapper.getJutaekSnByDtlSn(dtlSnList);
    if (jutaekSnList.size() == 0) {
      result.setResultMsg(BaseMsg.FAILED.getValue());
      result.setResultCode(BaseMsg.FAILED.getCode());
    } else {
      // 그걸로 주택 sn 받아서 주택의 정보 리스트로 던져주기
      JutaekListRequest jutaekListRequest = new JutaekListRequest();
      jutaekListRequest.setJutaekSnList(jutaekSnList);
      jutaekListRequest.setPageNum(baseListModel.getPageNum());
      jutaekListRequest.setPageSize(baseListModel.getPageSize());
      jutaekListRequest.setPageOffset(baseListModel.getPageOffset());

      int jutaekListCnt = rsltListMapper.getJutaekListCnt(jutaekListRequest);
      if (jutaekListCnt == 0) {
        result.setResultMsg("조회 결과가 없습니다.");
        result.setResultCode(BaseMsg.FAILED.getCode());
      } else {
        List<JutaekInfo> infoList = rsltListMapper.getJutaekList(jutaekListRequest);
        result.setJutaekInfoList(infoList);
      }
    }
    // 추후 필터링까지 쿼리에 적용
    return result;
  }
}
