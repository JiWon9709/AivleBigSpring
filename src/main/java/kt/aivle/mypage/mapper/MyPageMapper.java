package kt.aivle.mypage.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.mypage.model.UserInfo;
import kt.aivle.mypage.model.UserInfoRequest;
import kt.aivle.mypage.model.UserInfoResponse;

@DATA_SOURCE
public interface MyPageMapper {
    /**
     * 사용자정보 불러오기
     *
     * @param usersn
     * @return
     */
    public UserInfo getUserInfo(int usersn);

    /**
     * 사용자정보 수정하기
     *
     * @param userInfo
     * @return
     */
    public void updateUserInfo(UserInfo userInfo); // 사용자 정보 업데이트
}
