package kt.aivle.member.api;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.ApiOperation;
import kt.aivle.member.model.*;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
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

    @ApiOperation(value = "이메일 중복 체크")
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckRequest request) {
        Map<String, Object> response = userService.checkEmail(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        try {

            UserResponse userResponse = userService.login(loginRequest);

            TokenDto tokenDto = jwtTokenProvider.createToken(
                    String.valueOf((userResponse.getUserSn())),
                    Collections.singletonList(userResponse.getRole())
            );


            // ✅ Access Token 쿠키 설정 (SameSite=None 적용 가능)
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokenDto.getAccessToken())
                    .httpOnly(true)
                    .secure(false) // HTTP 환경에서는 false, HTTPS에서는 true
                    .sameSite("Lax") //
                    .path("/")
                    .maxAge((int) (JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME / 1000)) // 30분
                    .build();

            // ✅ Refresh Token 쿠키 설정 (SameSite=None 적용 가능)
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenDto.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge((int) (JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000)) // 24시간
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(new LoginResponse(
                    200,
                    "로그인이 완료되었습니다.",
                    userResponse
            ));
        } catch (IllegalArgumentException e) {
            log.error("로그인 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(400, e.getMessage(), null));
        } catch (Exception e) {
            log.error("로그인 실패 - 서버 에러: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse(500, "서버 오류가 발생했습니다.", null));
        }
    }

    @ApiOperation(value = "토큰 재발급 요청")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 쿠키에서 refresh token 추출
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {

                    if (cookie.getName().equals("refresh_token")) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
            // refresh 토큰 없으면 즉시 로그아웃 유도
            if (refreshToken == null) {
                log.warn("🚨 Refresh Token이 없습니다.");
//                return deleteTokensAndLogout();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenResponse(false, "NO_REFRESH_TOKEN", null));
            }

            // 토큰 재발급
            // 🚀 Refresh Token 검증 및 새로운 Access Token 발급
            TokenDto newTokenDto;
            try {
                newTokenDto = jwtTokenProvider.refreshToken(refreshToken);
            } catch (ExpiredJwtException e) {
                log.warn("🚨 Refresh Token 만료됨.");


                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenResponse(false, "EXPIRED_REFRESH_TOKEN", null));
            } catch (JwtException e) {
                log.warn("🚨 Refresh Token 검증 실패.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenResponse(false, "유효하지 않은 Refresh Token입니다.", null));
            }

            log.info("✅ 새 Access Token 발급 완료");

            // ✅ 새로운 Access Token을 쿠키에 설정
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newTokenDto.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge((int) (JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME / 1000))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .body(new TokenResponse(true, "토큰이 성공적으로 재발급되었습니다.", null));

        } catch (RuntimeException e) {
            log.error("🚨 토큰 재발급 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse(false, "TOKEN_REISSUE_FAILED: ", null));
        }
    }


    @ApiOperation(value = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // ✅ Access Token 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();

        // ✅ Refresh Token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    private ResponseEntity<?> deleteTokensAndLogout() {
        // ✅ Access Token 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();

        // ✅ Refresh Token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new TokenResponse(false, "로그인이 필요합니다.", null));
    }



    @ApiOperation(value = "내 이름 받아오기(헤더)")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo(Authentication authentication) {
        try{
            // Authentication 객체에서 userSn 추출
            Long userSn = Long.valueOf(authentication.getName());
            UserResponse userInfo = userService.getNameByUserSn(userSn);

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new LoginResponse(404, "사용자 정보를 찾을 수 없습니다.", null));
            }

            return ResponseEntity.ok(new LoginResponse(
                    200,
                    "사용자 정보 조회 성공",
                    userInfo
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(400, "잘못된 사용자 식별자입니다.", null));
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(500, "서버 오류가 발생했습니다.", null));
        }
    }




    @ApiOperation(value = "이메일 통한 비밀번호 찾기 요청")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            String email = request.getEmail();
            String code = userService.sendVerificationCode(email);
            log.info("인증코드 생성 완료: 이메일");

            userService.sendEmail(email, code);
            log.info("이메일 발송 완료");

            return ResponseEntity.ok().body(Map.of("message", "인증코드가 전송되었습니다."));
        } catch (Exception e) {
            log.error("비밀번호 찾기 처리 중 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 인증코드 확인 및 비밀번호 재설정
    @ApiOperation(value = "인증코드 확인 및 임시 비밀번호 발급")
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        try {
            String tempPassword = userService.verifyCodeAndResetPassword(email, code);


            return ResponseEntity.ok(AuthResponse.builder()
                    .statusCode(200)
                    .message("임시 비밀번호가 생성되었습니다.")
                    .data(Map.of("tempPassword", tempPassword))
                    .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.PRECONDITION_FAILED)
                    .body(AuthResponse.builder()
                            .statusCode(412)
                            .message(e.getMessage())
                            .build());
        } catch (RuntimeException e) {
            // 비밀번호 암호화 실패 등 기타 오류
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .statusCode(500)
                            .message(e.getMessage())
                            .build());
        }
    }

}
