package kt.aivle.mypage.api;

import io.swagger.annotations.*;
import kt.aivle.base.BaseMsg;
import kt.aivle.mypage.model.*;
import kt.aivle.mypage.service.MyPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@RestController
@Api(tags = "mypage", description = "mypage API")
@CrossOrigin(origins = "http://localhost:3000") // React 앱의 도메인 허용
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
    @PostMapping(value = "/mypage",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@ApiParam(value = "마이페이지", required = true) @RequestBody UserInfo UserInfo) {
        try {
            myPageService.updateUser(UserInfo);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(500).body("업데이트 중 오류가 발생했습니다: " + e.getMessage());
//            response.setResultMsg(e.getMessage());
//            response.setResultCode(BaseMsg.FAILED.getCode());
        }
    }

    // 비밀번호변경 페이지
    @ApiOperation(value = "password변경")
    @PostMapping(value = "/change-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatepw(@ApiParam(value = "비밀번호변경", required = true) @RequestBody UserPwRequest userPwRequest) {
        UserPwRequest pwRequest = new UserPwRequest();
        boolean isChanged;
        try {
            pwRequest.setNewPassword(userPwRequest.getNewPassword());
            pwRequest.setUserSn(userPwRequest.getUserSn());
            pwRequest.setCurrentPassword(userPwRequest.getCurrentPassword());
            isChanged = myPageService.updatePw(pwRequest);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않거나 변경에 실패했습니다.");
        } if (isChanged) return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        else {
            return ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않거나 변경에 실패했습니다.");
        }
    }

    // 관심주택 페이지
    @ApiOperation(value = "favorites관리")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = UserInfo.class)})
    @GetMapping(value = "/favorites",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FavoriteListResponse getUserfavorite(@ApiParam(value = "관심주택페이지", required = true) @RequestParam("userSn") int userSn) {
        FavoriteListResponse response = new FavoriteListResponse();
        try {
            System.out.println("받은 데이터: " + response); // 요청 데이터 로그
            response = myPageService.getuserFavorite(userSn);
        } catch (Exception e) {
            // 예외 처리
//            response.setResultMsg(e.getMessage());
//            response.setResultCode(BaseMsg.FAILED.getCode());
        }
        return response;
    }
}
