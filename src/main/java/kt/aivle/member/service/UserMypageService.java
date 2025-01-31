package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMypageMapper;
import kt.aivle.member.model.UserBasicInfoResponse;
import kt.aivle.member.model.UserProfileResponse;
import kt.aivle.member.model.UserProfileUpdateRequest;
import kt.aivle.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserMypageService {

    private final UserMypageMapper userMypageMapper;

    @Transactional(readOnly = true)
    public UserBasicInfoResponse getUserInfo(long userSn) {
        try {
            return userMypageMapper.findBasicInfoByUserSn(userSn);
        } catch (Exception e) {
            log.error("Error in getUserInfo: " , e);
            throw e;

        }
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(long userSn) {
        try {
            return userMypageMapper.findProfileByUserSn(userSn);
        } catch (Exception e) {
            log.error("Error in getUserInfo: " , e);
            throw e;

        }
    }

    @Transactional
    public void updateUserProfile(Long userSn, UserProfileUpdateRequest request) {
        int updateRows = userMypageMapper.updateUserProfile(userSn, request);
        if (updateRows == 0) {
            throw new IllegalArgumentException("사용자 정보 수정에 실패했습니다.");
        }
    }

    public boolean checkPassword(Long userSn, String currentPassword) {
        try {
            String encryptedPassword = Sha256Util.encrypt(currentPassword);
            String storedPassword = userMypageMapper.getStoredPassword(userSn);
            return storedPassword.equals(encryptedPassword);
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 중 오류 발생", e);
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.");

        }
    }

    @Transactional
    public void changePassword(Long userSn, String newPassword) {
        try {
            String encryptedPassword = Sha256Util.encrypt(newPassword);
            int updatedRows = userMypageMapper.updatePassword(userSn, encryptedPassword);

            if (updatedRows == 0) {
                throw new IllegalArgumentException("비밀번호 변경에 실패했습니다.");

            }
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 중 오류 발생", e);
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.");
        }

    }

    @Transactional
    public void withdrawUser(Long userSn, String currentPassword) {
        try {
            // 비밀번호 확인
            String encryptedPassword = Sha256Util.encrypt(currentPassword);
            String storedPassword = userMypageMapper.getStoredPassword(userSn);

            if (!storedPassword.equals(encryptedPassword)) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

            // 회원 탈퇴 처리 (user_yn = 'N'으로 변경)
            int updatedRows = userMypageMapper.updateUserWithdrawal(userSn);
            if (updatedRows == 0) {
                throw new RuntimeException("회원 탈퇴 처리에 실패했습니다.");
            }

            log.info("회원 탈퇴 처리 완료 - userS: {}", userSn);
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 중 오류 발생", e);
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.");
        }
    }


}
