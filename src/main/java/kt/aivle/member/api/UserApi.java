package kt.aivle.member.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import kt.aivle.member.model.dto.EmailCheckRequestDTO;
import kt.aivle.member.model.dto.LoginRequestDTO;
import kt.aivle.member.model.dto.SignupRequestDTO;
import kt.aivle.member.model.dto.ResponseDTO;
import kt.aivle.member.model.entity.User;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO signupRequest) {
        try {
            userService.signup(signupRequest);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(200, "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(400, e.getMessage()));
        }

    }

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            User user = userService.login(loginRequest);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(200, "로그인이 완료되었습니다.", user));
        } catch (IllegalArgumentException e) {
            // 잘못된 이메일이나 비밀번호의 경우
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(400, e.getMessage(), null));
        } catch (Exception e) {
            // 기타 서버 오류의 경우
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(500, "서버 오류가 발생했습니다.", null));
        }
    }

    @ApiOperation(value = "이메일 중복 체크")
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckRequestDTO request) {
        boolean exists = userService.checkEmailExists(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(){
//
//    }

}
