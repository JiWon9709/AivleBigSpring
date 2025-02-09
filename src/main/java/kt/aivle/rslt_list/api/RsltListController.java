package kt.aivle.rslt_list.api;


import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResListModel;
import kt.aivle.base.BaseResModel;
import kt.aivle.rslt_list.mapper.RsltListMapper;
import kt.aivle.rslt_list.model.*;
import kt.aivle.rslt_list.service.RsltListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@Api(tags = "rslt_list", description = "결과 리스트 API")
public class RsltListController {

  @Autowired
  private RsltListMapper rsltListMapper;

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
      @ApiParam(value = "주택상세고유번호", required = true) @RequestParam Long jutaekDtlSn,
      @ApiParam(value = "사용자고유번호", required = true) @RequestParam Long userSn,
      @ApiParam(value = "공고고유번호", required = true) @RequestParam Long gongoSn) {
    BaseResModel<JutaekDtlInfo> response = new BaseResModel();
    try {
      JutaekDtlRequest jutaekDtlRequest = new JutaekDtlRequest();
      jutaekDtlRequest.setJutaekDtlSn(jutaekDtlSn);
      jutaekDtlRequest.setUserSn(userSn);
      jutaekDtlRequest.setGongoSn(gongoSn);
      response = rsltListService.jutaekDtl(jutaekDtlRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(value = "즐겨찾기 등록, 해제")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResListModel.class)})
  @PostMapping(value = "/fav-ctl",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResModel favoriteCtl(@ApiParam(value = "즐겨찾기 등록, 해제", required = true) @RequestBody FavExChkRequest favExChkRequest) {
    BaseResModel response = new BaseResModel<>();
    try {
      response = rsltListService.favoriteCtl(favExChkRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(value = "사진 등록")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResListModel.class)})
  @PostMapping(value = "/reg-img",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResModel regImg(@ApiParam(value = "사진 등록", required = true) ImgRegRequest imgRegRequest) {
    BaseResModel response = new BaseResModel<>();
    try {
      response = rsltListService.testImgReg(imgRegRequest);
    } catch (Exception e) {
      response.setResultMsg(e.getMessage());
      response.setResultCode(BaseMsg.FAILED.getCode());
    }
    return response;
  }

  @ApiOperation(
      value = "file.1 파일다운로드",
      notes = "파일다운로드",
      response = Resource.class   // Use Resource.class here
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Resource.class)
  })
  @GetMapping(
      value = "/v1/download/{sn}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE  // force the content type
  )
  public ResponseEntity<Resource> downloadPdf(@PathVariable Long sn) throws IOException {
    String filePath = rsltListMapper.getPdfFileById(sn);
    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      return ResponseEntity.notFound().build();
    }
    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }
}