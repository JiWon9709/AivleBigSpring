package kt.aivle.rslt_list.api;


import io.swagger.annotations.*;
import kt.aivle.base.BaseListModel;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.clnt_co.model.HospitalInfoListResponse;
import kt.aivle.clnt_co.model.TestList;
import kt.aivle.clnt_co.model.TestRequest;
import kt.aivle.clnt_co.model.TestRequest2;
import kt.aivle.clnt_co.service.ClntCoService;
import kt.aivle.rslt_list.model.JutaekInfoListResponse;
import kt.aivle.rslt_list.service.RsltListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "rslt_list", description = "결과 리스트 API")
public class RsltListController {

  @Autowired
  private RsltListService rsltListService;

  @ApiOperation(value = "결과 리스트")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResModel.class)})
  @GetMapping(value = "/rslt-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public JutaekInfoListResponse rsltList(
      @ApiParam(value = "페이지 크기", required = true) @RequestParam int pageSize,
      @ApiParam(value = "페이지 번호", required = true) @RequestParam int pageNum) {
    JutaekInfoListResponse response = new JutaekInfoListResponse();
    try {
      BaseListModel baseListModel = new BaseListModel();
      baseListModel.setPageSize(pageSize);
      baseListModel.setPageNum(pageNum);
      response = rsltListService.rsltList(baseListModel);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }
}