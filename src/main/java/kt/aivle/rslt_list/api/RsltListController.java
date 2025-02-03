package kt.aivle.rslt_list.api;


import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.rslt_list.model.*;
import kt.aivle.rslt_list.service.RsltListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "rslt_list", description = "결과 리스트 API")
public class RsltListController {

  @Autowired
  private RsltListService rsltListService;

  @ApiOperation(value = "결과 리스트")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResListModel.class)})
  @PostMapping(value = "/rslt-list",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResListModel<JutaekInfo> rsltList(@ApiParam(value = "결과 리스트", required = true) @RequestBody JutaekListRequest jutaekListRequest) {
    BaseResListModel<JutaekInfo> response = new BaseResListModel<>();
    try {
      response = rsltListService.rsltList(jutaekListRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(value = "주택상세정보")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResModel.class)})
  @GetMapping(value = "/jutaek-dtl", produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResModel<JutaekDtlInfo> rsltList(
      @ApiParam(value = "주택상세고유번호", required = true) @RequestParam Long jutaekDtlSn) {
    BaseResModel<JutaekDtlInfo> response = new BaseResModel();
    try {
      JutaekDtlRequest jutaekDtlRequest = new JutaekDtlRequest();
      jutaekDtlRequest.setJutaekDtlSn(jutaekDtlSn);
      response = rsltListService.jutaekDtl(jutaekDtlRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(value = "결과 리스트")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResListModel.class)})
  @PostMapping(value = "/reg-img",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResModel regImg(@ApiParam(value = "결과 리스트", required = true) @RequestBody ImgRegRequest imgRegRequest) {
    BaseResModel response = new BaseResModel<>();
    try {
      response = rsltListService.testImgReg(imgRegRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }
}