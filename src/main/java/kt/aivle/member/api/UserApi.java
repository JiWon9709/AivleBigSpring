package kt.aivle.member.api;

import io.swagger.annotations.ApiOperation;
import kt.aivle.member.model.*;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.signup(signupRequest);
            return ResponseEntity.ok()
                    .body(new UserException(200, "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new UserException(400, e.getMessage()));
        }

    }

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse userResponse = userService.login(loginRequest);
            TokenDto tokenDto = jwtTokenProvider.createToken(
                    String.valueOf((userResponse.getUserSn())),
                    Collections.singletonList("ROLE_USER")
            );

            return ResponseEntity.ok(new LoginResponse(
                    200,
                    "로그인이 완료되었습니다.",
                    userResponse,
                    tokenDto
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(400, e.getMessage(), null, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse(500, "서버 오류가 발생했습니다.", null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 로그아웃 처리에 대한 로직 없음 (클라이언트에서만 토큰 삭제)
        return ResponseEntity.ok("로그아웃 성공");
    }


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(jwtTokenProvider.refreshToken(refreshToken));
    }

    @ApiOperation(value = "이메일 중복 체크")
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckRequest request) {
        boolean exists = userService.checkEmailExists(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

//    @ApiOperation(value = "비밀번호 찾기 - 인증코드 발송")
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> sendVerificationCode(@RequestBody EmailRequest request) {
//        try {
//            String code = userService.sendVerificationCode(request.getEmail());
//            return ResponseEntity.ok(new UserException(200, "인증코드가 발송되었습니다."));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new UserException(400, e.getMessage()));
//        }
//    }
//
//    @ApiOperation(value = "비밀번호 재설정")
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
//        try {
//            String tempPassword = userService.verifyCodeAndResetPassword(request.getEmail(), request.getCode());
//            return ResponseEntity.ok(new UserException(200, "임시 비밀번호: " + tempPassword));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new UserException(400, e.getMessage()));
//        }
//    }

}
