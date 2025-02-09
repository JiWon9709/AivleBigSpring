package kt.aivle.member.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.member.model.UserBasicInfoResponse;
import kt.aivle.member.model.UserProfileResponse;
import kt.aivle.member.model.UserProfileUpdateRequest;
import kt.aivle.mypage.model.UserInfo;
import kt.aivle.mypage.model.UserPwRequest;
import org.apache.ibatis.annotations.Param;

@DATA_SOURCE
public interface UserMypageMapper {

    UserBasicInfoResponse findBasicInfoByUserSn(Long userSn);

    UserProfileResponse findProfileByUserSn(Long userSn);

    int updateUserProfile(@Param("userSn") Long userSn, @Param("request")UserProfileUpdateRequest request);

    String getStoredPassword(@Param("userSn") Long userSn);

    int updatePassword(@Param("userSn") Long userSn, @Param("password") String password);

    int updateUserWithdrawal(@Param("userSn") Long userSn);

    public UserInfo getUserInfo(long usersn);
    public void updateUserInfo(UserInfo userInfo); // 사용자 정보 업데이트
    public int verifyPassword(UserPwRequest userPwRequest);
    public int updatePw(UserPwRequest userPwRequest); // 비밀번호 변경
    public void deleteUser(int usersn);

    // 추가: 프로필 이미지 저장
    int updateUserProfileImage(@Param("userSn") Long userSn, @Param("profileImage") String profileImage);

    // 추가: 프로필 이미지 조회
    String getUserProfileImage(@Param("userSn") Long userSn);

    // 🔹 프로필 이미지 삭제 (NULL로 업데이트)
    int deleteUserProfileImage(@Param("userSn") Long userSn);


}
