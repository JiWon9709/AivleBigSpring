package kt.aivle.rslt_list.api;


import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.rslt_list.model.JutaekInfo;
import kt.aivle.rslt_list.model.JutaekListRequest;
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
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResModel.class)})
  @GetMapping(value = "/rslt-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResListModel<JutaekInfo> rsltList(
      @ApiParam(value = "페이지 크기", required = true) @RequestParam int pageSize,
      @ApiParam(value = "페이지 번호", required = true) @RequestParam int pageNum,
      @ApiParam(value = "지역 리스트", required = false) @RequestParam(required = false) List<String> location,
      @ApiParam(value = "최대 크기", required = false) @RequestParam(required = false) String maxSize,
      @ApiParam(value = "최소 크기", required = false) @RequestParam(required = false) String minSize,
      @ApiParam(value = "최소 보증금", required = false) @RequestParam(required = false) String minGuarantee,
      @ApiParam(value = "최대 보증금", required = false) @RequestParam(required = false) String maxGuarantee,
      @ApiParam(value = "최소 월세", required = false) @RequestParam(required = false) String minMonthly,
      @ApiParam(value = "최대 월세", required = false) @RequestParam(required = false) String maxMonthly) {
    BaseResListModel<JutaekInfo> response = new BaseResListModel();
    try {
      JutaekListRequest jutaekListRequest = new JutaekListRequest();
      jutaekListRequest.setPageSize(pageSize);
      jutaekListRequest.setPageNum(pageNum);
      jutaekListRequest.setLocation(location);
      jutaekListRequest.setMinSize(minSize);
      jutaekListRequest.setMinGuarantee(minGuarantee);
      jutaekListRequest.setMaxSize(maxSize);
      jutaekListRequest.setMaxGuarantee(maxGuarantee);
      jutaekListRequest.setMinMonthly(minMonthly);
      jutaekListRequest.setMaxMonthly(maxMonthly);
      response = rsltListService.rsltList(jutaekListRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }
}