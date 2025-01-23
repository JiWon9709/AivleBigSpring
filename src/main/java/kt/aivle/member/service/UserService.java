package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMapper;
import kt.aivle.member.model.LoginRequest;
import kt.aivle.member.model.SignupRequest;
import kt.aivle.member.model.UserAuth;
import kt.aivle.member.model.UserResponse;
import kt.aivle.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    @Transactional
    public void signup(SignupRequest signupRequest) { // 회원가입
        //이메일 중복 체크
        if (userMapper.findByEmail(signupRequest.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        try {
            signupRequest.setPassword(Sha256Util.encrypt(signupRequest.getPassword())); // 비밀번호 암호화
            signupRequest.setUseYn("Y");
            userMapper.insertUser(signupRequest);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

    public UserResponse login(LoginRequest loginRequest) {
        try {
            // 이메일로 사용자 조회
            UserAuth userAuth = userMapper.findByEmailForAuth(loginRequest.getEmail());
            if (userAuth == null) {
                throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
            }

            // 비밀번호 검증
            String encryptedPassword = Sha256Util.encrypt(loginRequest.getPassword());
            if (!userAuth.getPassword().equals(encryptedPassword)) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }
            // 검증 성공 후 응답용 정보 조회 반환
            return userMapper.findByEmail(loginRequest.getEmail());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.", e);
        }
    }

    public boolean checkEmailExists(String email) {
        UserResponse userResponse = userMapper.findByEmail(email);
        return userResponse != null && "Y".equals(userResponse.getUseYn()); // 활성 사용자인 경우만 체크
    }
}
