package kt.aivle.member.api;

import io.swagger.annotations.ApiOperation;
import kt.aivle.member.model.*;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

    // 비밀번호 찾기 - 인증코드 전송
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = userService.sendVerificationCode(email);
        return ResponseEntity.ok().body(Map.of("message", "인증코드가 전송되었습니다."));
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
