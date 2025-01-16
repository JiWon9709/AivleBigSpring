package kt.aivle.mypage.api;

import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.mypage.model.UserInfo;
import kt.aivle.mypage.model.UserInfoRequest;
import kt.aivle.mypage.model.UserInfoResponse;
import kt.aivle.mypage.service.MyPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "mypage_info", description = "mypage API")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 마이페이지 기본 페이지 - 사용자 정보 보여주기
    @ApiOperation(value = "mypage관리")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = UserInfo.class)})
    @GetMapping(value = "/mypage",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfoResponse getUserInfo(@ApiParam(value = "마이페이지", required = true) @RequestParam("userSn") int userSn) {
        UserInfoResponse response = new UserInfoResponse();
        try {
            response = myPageService.getuserInfo(userSn);
        } catch (Exception e) {
            // 예외 처리
//            response.setResultMsg(e.getMessage());
//            response.setResultCode(BaseMsg.FAILED.getCode());
        }
        return response;
    }

    // 마이페이지 기본 페이지 - 사용자 정보 고치기
    @ApiOperation(value = "mypage수정")
    @PostMapping(value = "/mypage/{userSn}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@ApiParam(value = "마이페이지", required = true) @PathVariable("userSn") Integer userSn, @RequestBody UserInfoRequest userInfoRequest) {
        try {
            myPageService.updateUser(userSn, userInfoRequest);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(500).body("업데이트 중 오류가 발생했습니다: " + e.getMessage());
//            response.setResultMsg(e.getMessage());
//            response.setResultCode(BaseMsg.FAILED.getCode());
        }
    }
}
