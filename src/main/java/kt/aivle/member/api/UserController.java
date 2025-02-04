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
import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
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

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        try {
            long startTime = System.currentTimeMillis();

            // 1.유저 서비스 로그인 시간 체크
            log.info("로그인 프로세스 시작");
            UserResponse userResponse = userService.login(loginRequest);
            log.info("유저 서비스 로그인 소요시간: {}ms", System.currentTimeMillis() - startTime);


            // 2. 토큰 생성 시간 체크
            long tokenStartTime = System.currentTimeMillis();
            TokenDto tokenDto = jwtTokenProvider.createToken(
                    String.valueOf((userResponse.getUserSn())),
                    Collections.singletonList("ROLE_USER")
            );
            log.info("토큰 생성 소요시간: {}ms", System.currentTimeMillis() - tokenStartTime);

            // 3. 쿠키 설정 시작 시간 체크
            long cookieStartTime = System.currentTimeMillis();

            //Access Token을 HttpOnly 쿠키에 설정
            Cookie accessTokenCookie = new Cookie("access_token", tokenDto.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // 현재 배포된 게 http환경이라 false, 추후 https로 바꾸면 true로 변경 필요
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME / 1000)); // 30분
            //accessTokenCookie.setSameSite("Strict"); //  CSRF 방어, https 변경 후 CookieSerializer 사용해서 적용 가능


            // Refresh Token을 HttpOnly 쿠키에 설정
            Cookie refreshTokenCookie = new Cookie("refresh_token", tokenDto.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // 현재 배포된 게 http환경이라 false, 추후 https로 바꾸면 true로 변경 필요
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000)); // 24시간
            //refreshTokenCookie.setSameStie("Strict"); //  CSRF 방어, https 변경 후 CookieSerializer 사용해서 적용 가능


            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            log.info("쿠키 설정 소요시간: {}ms", System.currentTimeMillis() - cookieStartTime);

            // 4. 전체 프로세스 완료시간 체크
            log.info("전체 로그인 프로세스 완료 시간: {}ms", System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(new LoginResponse(
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

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenResponse(false, "Refresh Token이 없습니다.", null));
            }

            // 토큰 재발급
            TokenDto newTokenDto = jwtTokenProvider.refreshToken(refreshToken);

            // 새로운 Access Token을 쿠키에 설정
            Cookie accessTokenCookie = new Cookie("access_token", newTokenDto.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // 현재 배포된 게 http환경이라 false, 추후 https로 바꾸면 true로 변경 필요
            accessTokenCookie.setPath("/");

            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok()
                    .body(new TokenResponse(true, "토큰이 성공적으로 재발급되었스빈다.", null));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse(false, "토큰 재발급 실패: " + e.getMessage(), null));
        }
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

    @ApiOperation(value = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Access Token 쿠키 삭제
        Cookie accesTokenCookie = new Cookie("access_token", null);
        accesTokenCookie.setHttpOnly(true);
        accesTokenCookie.setSecure(false); // 현재 배포된 게 http환경이라 false, 추후 https로 바꾸면 true로 변경 필요
        accesTokenCookie.setMaxAge(0);
        accesTokenCookie.setPath("/");

        // Refresh Token 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // 현재 배포된 게 http환경이라 false, 추후 https로 바꾸면 true로 변경 필요
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");

        response.addCookie(accesTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
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
            log.info("인증코드 생성 완료: 이메일: {}", email);

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
