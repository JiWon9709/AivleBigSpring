package kt.aivle.member.api;

import io.swagger.annotations.ApiOperation;
import kt.aivle.base.BaseResModel;
import kt.aivle.member.model.*;
import kt.aivle.member.service.UserMypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "http://localhost:3000") // React 앱의 도메인 허용. SecurityConfig에서 전역으로 CORS 설정해서 개별 설정 불필요.
public class UserMypageController {

    @Autowired
    private UserMypageService userMypageService;

    // 마이페이지 기본 페이지 - 사용자 정보 보여주기
    @ApiOperation(value = "사용자 정보불러오기")
    @GetMapping(value = "/mypage")
    public BaseResModel<UserBasicInfoResponse> getUserInfo(Authentication authentication) {
        Long userSn = Long.valueOf(authentication.getName());
        UserBasicInfoResponse userInfo = userMypageService.getUserInfo(userSn);

        BaseResModel<UserBasicInfoResponse> result = new BaseResModel<>();
        result.setResultCode(200);  // 성공 코드
        result.setResultMsg("성공");
        result.setData(userInfo);

        return result;
    }

    // 마이페이지 프로필 수정 - 사용자 정보 보여주기
    @ApiOperation(value = "사용자 회원정보 수정")
    @GetMapping(value = "/profile")
    public BaseResModel<UserProfileResponse> getUserProfile(Authentication authentication) {
        Long userSn = Long.valueOf(authentication.getName());
        UserProfileResponse userProfile = userMypageService.getUserProfile(userSn);

        BaseResModel<UserProfileResponse> result = new BaseResModel<>();
        result.setResultCode(200);  // 성공 코드
        result.setResultMsg("성공");
        result.setData(userProfile);

        return result;
    }


    // 사용자 정보 고치기
    @ApiOperation(value = "회원정보수정")
    @PutMapping(value = "/editprofile")
    public BaseResModel<Void> updateUserProfile(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequest request) {

        Long userSn = Long.valueOf(authentication.getName());
        userMypageService.updateUserProfile(userSn, request);

        BaseResModel<Void> result = new BaseResModel<>();
        result.setResultCode(200);
        result.setResultMsg("성공");
        return result;
    }

    @ApiOperation(value = "password 일치 여부 확인")
    @PostMapping(value = "/check-password")
    public BaseResModel<Void> checkPassword(
            Authentication authentication,
            @RequestBody PasswordCheckRequest request) {

        Long userSn = Long.valueOf(authentication.getName());
        boolean isValid = userMypageService.checkPassword(userSn, request.getCurrentPassword());

        BaseResModel<Void> result = new BaseResModel<>();
        if (isValid) {
            result.setResultCode(200);
            result.setResultMsg("현재 비밀번호가 일치합니다.");
        } else {
            result.setResultCode(400);
            result.setResultMsg("현재 비밀번호가 일치하지 않습니다.");
        }
        return result;

    }

    @ApiOperation(value = "password 변경")
    @PutMapping("/change-password")
    public BaseResModel<Void> changePassword(
            Authentication authentication,
            @RequestBody PasswordChangeRequest request) {

        Long userSn = Long.valueOf(authentication.getName());
        userMypageService.changePassword(userSn, request.getNewPassword());

        BaseResModel<Void> result = new BaseResModel<>();
        result.setResultCode(200);
        result.setResultMsg("비밀번호가 성공적으로 변경되었습니다.");
        return result;
    }

    @ApiOperation(value = "회원 탈퇴")
    @PostMapping("/withdrawal")
    public BaseResModel<Void> withdrawal(
            Authentication authentication,
            @RequestBody WithdrawalRequest request) {

        Long userSn = Long.valueOf(authentication.getName());

        BaseResModel<Void> result = new BaseResModel<>();

        try {
            userMypageService.withdrawUser(userSn, request.getCurrentPassword());

            result.setResultCode(200);
            result.setResultMsg("회원 탈퇴가 완료되었습니다.");

        } catch (Exception e) {
            result.setResultCode(400);
            result.setResultMsg(e.getMessage());
        }
        return result;
    }

}