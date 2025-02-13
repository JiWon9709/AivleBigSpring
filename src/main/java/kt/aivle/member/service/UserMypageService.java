package kt.aivle.member.service;

import kt.aivle.member.mapper.UserMypageMapper;
import kt.aivle.member.model.*;
import kt.aivle.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserMypageService {

    @Autowired
    private final UserMypageMapper userMypageMapper;
    private static final String PROFILE_UPLOAD_DIR = "/uploads/profile/";
    private static final String FTP_URL_PREFIX = "http://4.217.186.166:8081/uploads/";

    @Value("${file.path}")
    private String dir;

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

            log.info("회원 탈퇴 처리 완료 - userSn: {}", userSn);
        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 중 오류 발생", e);
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.");
        }
    }

    /**
     * 프로필 사진 업로드 및 저장
     */
    @Transactional
    public UserProfileImageResponse uploadProfileImage(UserProfileImageUpdateRequest request, MultipartFile file) {
        try {
            log.info("프로필 이미지 업로드 시작 - userSn: {}", request.getUserSn());

            // 1️⃣ 파일 검증
            String contentType = file.getContentType();
            log.info("업로드된 파일의 contentType: {}", contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                log.error("허용되지 않는 파일 형식: {}", contentType);
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            // 2️⃣ 확장자 검증
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            log.info("📌 원본 파일명: {}, 확장자: {}", originalFilename, ext);

            Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif");
            if (!allowedExtensions.contains(ext)) {
                log.error("❌ 허용되지 않는 파일 확장자: {}", ext);
                throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다.");
            }

            // 3️⃣ 기존 프로필 사진 삭제
            String existingProfileImage = userMypageMapper.getUserProfileImage(request.getUserSn());
            log.info("📌 기존 프로필 이미지: {}", existingProfileImage);

            if (existingProfileImage != null) {
                String existingFileName = existingProfileImage.substring(existingProfileImage.lastIndexOf("/") + 1);
                File oldFile = new File(dir + "profile/" + existingFileName);
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    log.info("✅ 기존 프로필 이미지 파일 삭제 여부: {}", deleted);
                }
            }

            // 4️⃣ 새로운 파일 저장 (폴더 생성 보완)
            String uniqueFileName = UUID.randomUUID().toString() + "." + ext;
            String uploadFolder = dir + "profile/";

            log.info("📌 파일 저장 경로: {}", uploadFolder);
            File directory = new File(uploadFolder);
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                log.info("📌 프로필 폴더 생성 여부: {}", dirCreated);

                // 디렉토리 생성에 실패한 경우 예외 발생
                if (!dirCreated) {
                    throw new IOException("프로필 저장 폴더를 생성할 수 없습니다: " + uploadFolder);
                }
            }

            File saveFile = new File(uploadFolder, uniqueFileName);
            file.transferTo(saveFile);
            log.info("✅ 새 프로필 이미지 저장 완료: {}", uniqueFileName);

            // 5️⃣ DB 업데이트
            String imageUrl = FTP_URL_PREFIX + "profile/" + uniqueFileName;
            log.info("📌 DB에 저장할 프로필 이미지 URL: {}", imageUrl);

            int updatedRows = userMypageMapper.updateUserProfileImage(request.getUserSn(), imageUrl);
            log.info("✅ 프로필 이미지 DB 업데이트 결과: {} rows", updatedRows);

            if (updatedRows == 0) {
                log.error("❌ DB 업데이트 실패: 업데이트된 행이 없음");
                throw new RuntimeException("프로필 이미지 업데이트 실패");
            }

            return new UserProfileImageResponse(imageUrl);
        } catch (IOException e) {
            log.error("❌ 프로필 이미지 저장 중 오류 발생", e);
            throw new RuntimeException("프로필 이미지 저장 중 오류 발생");
        } catch (Exception e) {
            log.error("❌ 프로필 이미지 업로드 중 알 수 없는 오류 발생", e);
            throw new RuntimeException("프로필 이미지 업로드 중 오류 발생");
        }
    }


    /**
     * 프로필 사진 삭제
     */
    @Transactional
    public void deleteProfileImage(Long userSn) {
        try {
            // 1️⃣ 기존 프로필 사진 URL 가져오기
            String existingProfileImage = userMypageMapper.getUserProfileImage(userSn);

            if (existingProfileImage != null) {
                // 2️⃣ 서버에 저장된 이미지 삭제
                String fileName = existingProfileImage.substring(existingProfileImage.lastIndexOf("/") + 1);
                File file = new File(dir + "profile/" + fileName);

                if (file.exists()) {
                    if (!file.delete()) {
                        throw new RuntimeException("프로필 이미지 삭제 실패: " + fileName);
                    }
                }
            }

            // 3️⃣ DB에서 프로필 이미지 정보 제거 (NULL로 업데이트)
            int updatedRows = userMypageMapper.deleteUserProfileImage(userSn);
            if (updatedRows == 0) {
                throw new RuntimeException("프로필 이미지 삭제 실패 (DB 업데이트 오류)");
            }

            log.info("프로필 이미지 삭제 완료 - userSn: {}", userSn);

        } catch (Exception e) {
            log.error("프로필 이미지 삭제 중 오류 발생", e);
            throw new RuntimeException("프로필 이미지 삭제 중 오류가 발생했습니다.");
        }
    }

}
