package kt.aivle.clnt_co.api;


import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.clnt_co.model.*;
import kt.aivle.clnt_co.service.ClntCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "clnt_co", description = "의뢰사관리 API")
public class ClntCoController {

  @Autowired
  private ClntCoService clntCoService;

  @ApiOperation(value = "테스트 등록")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResModel.class)})
  @PostMapping(value = "/reg-test-tt",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResModel regTest(@ApiParam(value = "테스트등록", required = true) @RequestBody TestRequest testRequest) {
    BaseResModel response = new BaseResModel();
    try {
      response = clntCoService.regTest(testRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(value = "병원정보")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResModel.class)})
  @PostMapping(value = "/get-hospital-info",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public HospitalInfoListResponse emergencyResult(@ApiParam(value = "테스트등록", required = true) @RequestBody TestRequest2 testRequest2) {
    HospitalInfoListResponse response = new HospitalInfoListResponse();
    try {
      response = clntCoService.emergencyResult(testRequest2);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

}