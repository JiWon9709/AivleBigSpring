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

    Long rank  = jutaekListRequest.getInputRank();   // 1, 2, or 3
    Long score = jutaekListRequest.getInputScore();  // depends on the rank
    Long predScore = 0L; // default or sentinel value
    if (rank != null) {
      if (rank == 1) {
        // rank=1 => predScore = 22 + score
        predScore = 22 + score;
      } else if (rank == 2) {
        // rank=2 => predScore = 11 + score
        predScore = 11 + score;
      } else if (rank == 3) {
        // rank=3 => predScore = score
        predScore = score;
      }
    }
    jutaekListRequest.setQtyPred(predScore);

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
        for (int i = 0; i < infoList.size(); i++) {
          infoList.get(i).setInputRank(jutaekListRequest.getInputRank());
          infoList.get(i).setInputScore(jutaekListRequest.getInputScore());
          infoList.get(i).setInputWholeScore(predScore);
          //사진정보 넣기
          List<String> jutaekImgInfo = rsltListMapper.getJutaekImg(infoList.get(i).getJutaekDtlSn());
          if (jutaekImgInfo != null && jutaekImgInfo.size() > 0) {
            infoList.get(i).setJutaekImg(jutaekImgInfo);
          }
          //즐겨찾기 정보 넣기
          FavExChkRequest favExChkRequest = new FavExChkRequest();
          favExChkRequest.setGongoSn(jutaekListRequest.getGongoSn());
          favExChkRequest.setUserSn(jutaekListRequest.getUserSn());
          favExChkRequest.setJutaekDtlSn(infoList.get(i).getJutaekDtlSn());
          Long favoriteSn = rsltListMapper.getFavExChk(favExChkRequest);
          if (favoriteSn != null && favoriteSn > 0) {
            String yn = rsltListMapper.getFavYn(favoriteSn);
            if  (yn != null && yn.equals("Y")) {
              infoList.get(i).setFavYn("Y");
            } else {
              infoList.get(i).setFavYn("N");
            }
          } else {
            infoList.get(i).setFavYn("N");
          }
          //예측점수 순위로 변경
          int qtyPred = infoList.get(i).getQtyPred();
          if (qtyPred >= 22 && qtyPred <= 32) {
            infoList.get(i).setPredRank(1);
            infoList.get(i).setPredScore(qtyPred - 22);
          } else if (qtyPred >= 11 && qtyPred <= 21) {
            infoList.get(i).setPredRank(2);
            infoList.get(i).setPredScore(qtyPred - 11);
          } else if (qtyPred >= 0 && qtyPred <= 10) {
            infoList.get(i).setPredRank(3);
            infoList.get(i).setPredScore(qtyPred);
          } else {
            System.out.println("Unexpected qtyPred value: " + qtyPred);
          }
        }
        result.setData(infoList);
        result.setTotalCount(jutaekListCnt);
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
    JutaekListRequest jutaekListRequest = new JutaekListRequest();
    jutaekListRequest.setGongoSn(jutaekDtlRequest.getGongoSn());
    jutaekListRequest.setUserSn(jutaekDtlRequest.getUserSn());
    Long inputSn = rsltListMapper.getActiveInputSn(jutaekListRequest);
    if (inputSn > 0) {
      InputInfo inputInfo = rsltListMapper.getInputInfo(inputSn);
      jutaekDtlRequest.setInputRank(inputInfo.getInputRank());
    } else {
      result.setResultMsg(BaseMsg.FAILED.getValue());
      result.setResultCode(BaseMsg.FAILED.getCode());
    }
    try {
      JutaekDtlInfo info = rsltListMapper.getJutaekDtl(jutaekDtlRequest);
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
   * 즐겨찾기
   *
   * @return
   */
  @Transactional
  public BaseResModel favoriteCtl(FavExChkRequest favExChkRequest) {
    BaseResModel result = new BaseResModel();
    try {
      Long favoriteSn = rsltListMapper.getFavExChk(favExChkRequest);
      if (favoriteSn != null && favoriteSn > 0) {
        int cnt = rsltListMapper.updFavYn(favoriteSn);
        if (cnt == 0) {
          result.setResultMsg("즐겨찾기 변경에 실패했습니다.");
          result.setResultCode(BaseMsg.FAILED.getCode());
        }
      } else {
        int cnt = rsltListMapper.regFav(favExChkRequest);
        if (cnt == 0) {
          result.setResultMsg("즐겨찾기 등록에 실패했습니다.");
          result.setResultCode(BaseMsg.FAILED.getCode());
        }
      }
    } catch (Exception e) {
      result.setResultMsg("즐겨찾기 중 에러가 발생했습니다." + e);
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
      imgEntity.setPath("http://4.217.186.166:8081/uploads/img/");
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
        result.setResultMsg("이미지를 서버에 저장 중 에러가 발생하였습니다." + e);
        return result;
      }
    }
    return result;
  }


}
