package kt.aivle.rslt_list.service;

import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.base.exception.BaseErrorCode;
import kt.aivle.rslt_list.mapper.RsltListMapper;
import kt.aivle.rslt_list.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class RsltListService {
  private static final Logger log = LogManager.getLogger(RsltListService.class);
  @Autowired
  private RsltListMapper rsltListMapper;

  @Value("${file.path}")
  private String dir;

  /**
   * 결과 리스트
   *
   * @return
   */
  @Transactional
  public BaseResListModel<JutaekInfo> rsltList(JutaekListRequest jutaekListRequest) {
    BaseResListModel<JutaekInfo> result = new BaseResListModel();

    // 페이징 파라미터 전달
    result.setPageNum(jutaekListRequest.getPageNum());
    result.setPageSize(jutaekListRequest.getPageSize());

    // 페이지 계산
    if (jutaekListRequest.getPageNum() > 0) {
      jutaekListRequest.setPageOffset((jutaekListRequest.getPageNum() - 1) * jutaekListRequest.getPageSize());
    }

    //inputSn구해서 input 정보 불러오기
    Long inputSn = rsltListMapper.getActiveInputSn(jutaekListRequest);
    if (inputSn > 0) {
      InputInfo inputInfo = rsltListMapper.getInputInfo(inputSn);
      jutaekListRequest.setInputRank(inputInfo.getInputRank());
      jutaekListRequest.setInputType(inputInfo.getInputType());
      jutaekListRequest.setInputScore(inputInfo.getInputScore());
    } else {
      result.setResultMsg(BaseMsg.FAILED.getValue());
      result.setResultCode(BaseMsg.FAILED.getCode());
    }

    // 공고 sn으로 해당하는 공고 dtl sn들을 받기
    List<Integer> dtlSnList = rsltListMapper.getDtlSnByGongoSn(jutaekListRequest.getGongoSn());
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
      jutaekListRequest.setJutaekSnList(jutaekSnList);
      int jutaekListCnt = rsltListMapper.getJutaekListCnt(jutaekListRequest);
      if (jutaekListCnt == 0) {
        result.setResultMsg("조회 결과가 없습니다.");
        result.setResultCode(BaseMsg.FAILED.getCode());
      } else {
        List<JutaekInfo> infoList = rsltListMapper.getJutaekList(jutaekListRequest);
        result.setData(infoList);
      }
    }
    return result;
  }

  /**
   * 주택 상세정보
   *
   * @return
   */
  @Transactional
  public BaseResModel<JutaekDtlInfo> jutaekDtl(JutaekDtlRequest jutaekDtlRequest) {
    BaseResModel<JutaekDtlInfo> result = new BaseResModel();
    try {
      JutaekDtlInfo info = rsltListMapper.getJutaekDtl(jutaekDtlRequest.getJutaekDtlSn());
      if (info.getJutaekSn() > 0) {
        result.setData(info);
      } else {
        result.setResultMsg("조회 결과가 없습니다.");
        result.setResultCode(BaseMsg.FAILED.getCode());
      }
    } catch (Exception e) {
      result.setResultMsg("조회 중 에러가 발생했습니다." + e);
      result.setResultCode(BaseMsg.FAILED.getCode());
    }
    return result;
  }

  /**
   * 주택 상세정보
   *
   * @return
   */
  @Transactional
  public BaseResModel<JutaekDtlInfo> testImgReg(ImgRegRequest imgRegRequest) throws IOException {
    BaseResModel<JutaekDtlInfo> result = new BaseResModel();
    for (int i = 0; i < imgRegRequest.getImgList().size(); i++) {
      BufferedImage image = ImageIO.read(imgRegRequest.getImgList().get(i).getInputStream());
      String uploadFolder = dir + "img";
      String uploadImgNm = UUID.randomUUID().toString();
      String imgNm = imgRegRequest.getImgList().get(i).getOriginalFilename();
      String imgExt = imgNm.substring(imgNm.lastIndexOf(".") + 1);
      // int xSize = image.getWidth();
      // int ySize = image.getHeight();
      uploadImgNm = uploadImgNm.substring(uploadImgNm.lastIndexOf("\\") + 1);
      ImgEntity imgEntity = new ImgEntity();
      imgEntity.setRefSn(imgRegRequest.getRefSn());
      imgEntity.setRefTable(imgRegRequest.getRefTable());
      imgEntity.setPath("/img");
      imgEntity.setFileName(uploadImgNm + "." + imgExt);
      imgEntity.setExt(imgExt);
      imgEntity.setOriFileName(imgNm);

      int cnt = rsltListMapper.regImg(imgEntity);
      if (cnt > 0) {
        result.setResultCode(BaseErrorCode.SUCCESS.getCode());
        result.setResultMsg("이미지 정보를 DB에 저장했습니다.");
      } else {
        result.setResultCode(BaseErrorCode.ERROR_ETC.getCode());
        result.setResultMsg("이미지 정보를 DB에 저장하지 못했습니다.");
        return result;
      }
      File saveFile = new File(uploadFolder, uploadImgNm + "." + imgExt);
      try {
        imgRegRequest.getImgList().get(i).transferTo(saveFile);
      } catch (Exception e) {
        result.setResultCode(BaseErrorCode.ERROR_ETC.getCode());
        result.setResultMsg("이미지를 서버에 저장 중 에러가 발생하였습니다.");
        return result;
      }
    }

    return result;
  }


}
