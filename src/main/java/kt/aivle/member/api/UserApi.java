package kt.aivle.member.api;

import io.swagger.annotations.ApiOperation;
import kt.aivle.member.model.*;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserApi {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo(Authentication authentication) {
        try{
            // Authentication 객체에서 userSn 추출
            Long userSn = Long.valueOf(authentication.getName());
            UserResponse userInfo = userService.getNameByUserSn(userSn);

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new LoginResponse(404, "사용자 정보를 찾을 수 없습니다.", null, null));
            }

            return ResponseEntity.ok(new LoginResponse(
                    200,
                    "사용자 정보 조회 성공",
                    userInfo,
                    null  // 토큰 정보는 불필요
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(400, "잘못된 사용자 식별자입니다.", null, null));
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(500, "서버 오류가 발생했습니다.", null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 로그아웃 처리에 대한 로직 없음 (클라이언트에서만 토큰 삭제)
        return ResponseEntity.ok("로그아웃 성공");
    }


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String authHeader) {
        try {
            String refreshToken = authHeader.substring(7);
            TokenDto newTokenDto = jwtTokenProvider.refreshToken(refreshToken);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newTokenDto.getAccessToken())
                    .body(new TokenResponse(true, "토큰이 성공적으로 재발급되었습니다.", newTokenDto));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse(false, "토큰 재발급 실패: " + e.getMessage(), null));
        }
    }

    @ApiOperation(value = "이메일 중복 체크")
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckRequest request) {
        boolean exists = userService.checkEmailExists(request.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("success", !exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "이메일 통한 비밀번호 찾기 요청")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            String email = request.getEmail();
            String code = userService.sendVerificationCode(email);
            log.info("인증코드 생성 완료: {}", code);

            userService.sendEmail(email, code);
            log.info("이메일 발송 완료");

            return ResponseEntity.ok().body(Map.of("message", "인증코드가 전송되었습니다."));
        } catch (Exception e) {
            log.error("비밀번호 찾기 처리 중 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 인증코드 확인 및 비밀번호 재설정
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String tempPassword = userService.verifyCodeAndResetPassword(email, code);
        return ResponseEntity.ok().body(Map.of("message", "임시 비밀번호가 생성되었습니다.", "tempPassword", tempPassword));
    }

}
