package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMapper;
import kt.aivle.member.model.dto.LoginRequestDTO;
import kt.aivle.member.model.dto.SignupRequestDTO;
import kt.aivle.member.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    //private final BCryptPasswordEncoder passwordEncoder;

    public void signup(SignupRequestDTO signupRequest) {
        //이메일 중복 체크
        if (userMapper.findByEmail(signupRequest.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존쟇나느 이메일입니다.");
        }

        User user = new User();
        user.setUserName(signupRequest.getUserName());
        user.setEmail(signupRequest.getEmail());
        // 비밀번호 암호화
        user.setPassword(signupRequest.getPassword());
        user.setGender(signupRequest.getGender());
        user.setAddress(signupRequest.getAddress());
        user.setZipCode(signupRequest.getZipCode());
        user.setTelno(signupRequest.getTelno());
        user.setUseYn("Y");

        userMapper.insertUser(user);
    }

    public User login(LoginRequestDTO loginRequest) {
        User user = userMapper.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        //패스워드 검사

        return user;
    }
}
