package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMapper;
import kt.aivle.member.model.*;
import kt.aivle.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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

        UserResponse existingUser = userMapper.findByEmail(signupRequest.getEmail());

        //이메일 중복 체크
        if (existingUser != null) {
            if (existingUser.getUseYn().equals("Y")) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            } else {
                // 탈퇴한 회원인 경우 30일 제한 체크
                LocalDateTime withdrawalDate = existingUser.getWithdrawalDt();
                if (withdrawalDate != null) {
                    long daysSinceWithdrawal = ChronoUnit.DAYS.between(
                            withdrawalDate,
                            LocalDateTime.now()
                    );
                    if (daysSinceWithdrawal < 30) {
                        throw new IllegalArgumentException(
                                String.format(" 탈퇴 후 30일이 경과하지 않았습니다. %d일 후에 다시 시도해주세요.",
                                        30 - daysSinceWithdrawal)
                        );
                    }
                }
            }
        }
        try {
            signupRequest.setPassword(Sha256Util.encrypt(signupRequest.getPassword())); // 비밀번호 암호화
            signupRequest.setUseYn("Y");
            userMapper.insertUser(signupRequest);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

    public Map<String, Object> checkEmail(String email) {
        Map<String, Object> response = new HashMap<>();
        UserResponse user = userMapper.findByEmail(email);

        if (user == null) {
            response.put("success", true);
            response.put("exists", false);
            response.put("message", "사용 가능한 이메일입니다.");
        } else if (user.getUseYn().equals("N")) {
            response.put("success", false);
            response.put("exists", true);
            response.put("message", "탈퇴 후 30일이 경과하지 않아 재가입이 제한됩니다.");
        } else {
            response.put("success", false);
            response.put("exists", true);
            response.put("message", "이미 사용 중인 이메일입니다.");
        }

        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest) {
        try {
//            log.info("로그인 시도 - 이메일: {}", loginRequest.getEmail());

            // 이메일로 사용자 조회
            UserAuth userAuth = userMapper.findByEmailForAuth(loginRequest.getEmail());
            if (userAuth == null) {
                log.warn("존재하지 않는 이메일: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
            }

            // 비밀번호 검증
            String encryptedPassword = Sha256Util.encrypt(loginRequest.getPassword().trim());

            if (!userAuth.getPassword().equals(encryptedPassword)) {
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

    @Transactional(readOnly = true)
    public UserResponse getNameByUserSn(Long userSn) {
        return userMapper.findNameByUserSn(userSn);
    }

    public boolean checkEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }

    @Transactional
    public String sendVerificationCode(String email) {
        log.info("이메일 인증 코드 발송 시도 - 이메일: {}", email);

        UserResponse user = userMapper.findByEmail(email);


        if (user == null || !"Y".equals(user.getUseYn())) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        String code = generateRandomCode();
        verificationCodes.put(email, new VerificationInfo(code, LocalDateTime.now()));


        return code;
    }

    @Async
    public void sendEmail(String email, String code) {
        log.info("이메일 발송 시작 - 수신자: {}", email);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Zipline - 비밀번호 재설정 인증번호");
            helper.setFrom("ziplineaivle@gmail.com");

            String htmlContent = String.format("""
            <div style="margin:100px;">
                <div style="margin-bottom: 50px;">
                    <h1 style="font-size: 24px; color: #303030;">Zipline</h1>
                </div>
                
                <div style="margin-bottom: 30px;">
                    <h2 style="font-size: 20px; color: #404040;">비밀번호 재설정 인증번호 안내</h2>
                </div>
                
                <div style="background-color: #f7f7f7; padding: 30px; border-radius: 10px; margin-bottom: 30px;">
                    <p style="font-size: 16px; color: #606060; margin-bottom: 20px;">
                        안녕하세요.<br>
                        Zipline 서비스 비밀번호 재설정을 위한 인증번호를 안내해 드립니다.
                    </p>
                    
                    <div style="background-color: #ffffff; padding: 20px; border-radius: 5px; margin-bottom: 20px; text-align: center;">
                        <span style="font-size: 30px; color: #000000; font-weight: bold; letter-spacing: 5px;">%s</span>
                    </div>
                    
                    <p style="font-size: 14px; color: #808080;">
                        * 본 인증번호는 5분간 유효합니다.<br>
                        * 비밀번호 재설정을 요청하지 않으셨다면 이 메일을 무시하셔도 됩니다.
                    </p>
                </div>
                
                <div style="border-top: 1px solid #e9e9e9; padding-top: 20px;">
                    <p style="font-size: 12px; color: #b0b0b0;">
                        본 메일은 발신전용 메일이므로 회신을 받지 않습니다.<br>
                        Copyright © Zipline All rights reserved.
                    </p>
                </div>
            </div>
        """, code);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("이메일 발송 성공 - 수신자: {}", email);

        } catch (MessagingException | MailException e) {
            log.error("이메일 발송 실패 - 상세 에러: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }


    @Transactional
    public String verifyCodeAndResetPassword(String email, String code) {
        log.info("비밀번호 재설정 시도 - 이메일: {}", email);

        VerificationInfo info = verificationCodes.get(email);
        if (info == null || !info.getVerificationCode().equals(code)) {

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
