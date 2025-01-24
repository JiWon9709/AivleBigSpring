package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMapper;
import kt.aivle.member.model.*;
import kt.aivle.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    // 인증 코드 저장소 (Redis 써야 한다는데 추후 참조)
    private Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();


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
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest) {
        try {
            log.info("로그인 시도 - 이메일: {}", loginRequest.getEmail());

            // 이메일로 사용자 조회
            UserAuth userAuth = userMapper.findByEmailForAuth(loginRequest.getEmail());
            if (userAuth == null) {
                log.warn("존재하지 않는 이메일: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
            }

            // 비밀번호 검증
            String encryptedPassword = Sha256Util.encrypt(loginRequest.getPassword());
            log.info("입력된 비밀번호 암호화 결과: {}", encryptedPassword);
            log.info("저장된 비밀번호: {}", userAuth.getPassword());

            if (!userAuth.getPassword().equals(encryptedPassword)) {
                log.warn("비밀번호 불일치 - 이메일: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }

            // 검증 성공 후 응답용 정보 조회 반환
            return userMapper.findByEmail(loginRequest.getEmail());
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 중 오류:", e);
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("로그인 중 예외 발생:", e);
            throw e;
        }
    }

    public boolean checkEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }

    @Transactional
    public String sendVerificationCode(String email) {
        log.info("이메일 인증 코드 발송 시도 - 이메일: {}", email);

        UserResponse user = userMapper.findByEmail(email);
        log.info("조회된 사용자 정보: {}", user);  // null인지 확인

        if (user == null || !"Y".equals(user.getUseYn())) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        String code = generateRandomCode();
        verificationCodes.put(email, new VerificationInfo(code, LocalDateTime.now()));
        log.info("생성된 인증코드: {}", code);

        return code;
    }

    @Async
    public void sendEmail(String email, String code) {
        log.info("이메일 발송 시작 - 수신자: {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 재설정 인증번호");
        message.setText("인증번호: " + code);
        message.setFrom("your-email@gmail.com"); // 발신자 이메일 추가

        try {
            mailSender.send(message);
            log.info("이메일 발송 성공 - 수신자: {}", email);
        } catch (MailException e) {
            log.error("이메일 발송 실패 - 상세 에러: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }


    @Transactional
    public String verifyCodeAndResetPassword(String email, String code) {
        log.info("비밀번호 재설정 시도 - 이메일: {}", email);

        VerificationInfo info = verificationCodes.get(email);
        if (info == null || !info.getVerificationCode().equals(code)) {
            log.warn("인증번호 불일치 - 이메일: {}, 입력된 코드: {}", email, code);
            throw new IllegalArgumentException("잘못된 인증번호입니다.");
        }

        // 만료 시간 체크 (5분)
        if (info.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            log.warn("인증번호 만료 - 이메일: {}, 생성시간: {}", email, info.getCreatedAt());
            verificationCodes.remove(email);
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }

        // 임시 비밀번호 생성 및 저장
        String tempPassword = generateTempPassword();
        log.info("임시 비밀번호 생성 완료 - 이메일: {}", email);

        try {
            String encryptedPassword = Sha256Util.encrypt(tempPassword);
            log.debug("암호화된 비밀번호 생성 완료 - 이메일: {}", email);

            int updatedRows = userMapper.updatePassword(email, encryptedPassword);
            log.info("비밀번호 업데이트 결과 - 이메일: {}, 업데이트된 행 수: {}", email, updatedRows);

            if (updatedRows == 0) {
                log.error("비밀번호 업데이트 실패 - 이메일: {}", email);
                throw new IllegalArgumentException("비밀번호 업데이트에 실패했습니다.");
            }

            // 인증 정보 삭제
            verificationCodes.remove(email);
            log.info("인증 정보 삭제 완료 - 이메일: {}", email);

            return tempPassword;
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 실패 - 이메일: {}", email, e);
            throw new RuntimeException("비밀번호 암호화 실패", e);
        } catch (Exception e) {
            log.error("비밀번호 재설정 중 예외 발생 - 이메일: {}", email, e);
            throw new RuntimeException("비밀번호 재설정 중 오류가 발생했습니다.", e);
        }
    }

    private String generateRandomCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }




}
