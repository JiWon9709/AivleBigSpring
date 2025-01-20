package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMapper;
import kt.aivle.member.model.dto.LoginRequestDTO;
import kt.aivle.member.model.dto.SignupRequestDTO;
import kt.aivle.member.model.entity.User;
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
    public void signup(SignupRequestDTO signupRequest) { // 회원가입
        //이메일 중복 체크
        if (userMapper.findByEmail(signupRequest.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        try {
            User user = new User();
            user.setUserName(signupRequest.getUserName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(Sha256Util.encrypt(signupRequest.getPassword())); // 비밀번호 암호화
            user.setGender(signupRequest.getGender());
            user.setAddress(signupRequest.getAddress());
            user.setZipCode(signupRequest.getZipCode());
            user.setTelno(signupRequest.getTelno());
            user.setUseYn("Y");

            userMapper.insertUser(user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

    public User login(LoginRequestDTO loginRequest) {
        try {
            // 이메일로 사용자 조회
            User user = userMapper.findByEmail(loginRequest.getEmail());
            if (user == null) {
                throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
            }

            // 비밀번호 검증
            String encryptedPassword = Sha256Util.encrypt(loginRequest.getPassword());
            if (!user.getPassword().equals(encryptedPassword)) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }

            return user;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.", e);
        }
    }

    public boolean checkEmailExists(String email) {
        User user = userMapper.findByEmail(email);
        return user != null && "Y".equals(user.getUseYn()); // 활성 사용자인 경우만 체크
    }
}
