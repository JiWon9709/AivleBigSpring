package kt.aivle.mypage.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.mypage.model.*;

import java.util.List;

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
     * 회원정보 수정하기
     *
     * @param userInfo
     * @return
     */
    public void updateUserInfo(UserInfo userInfo); // 사용자 정보 업데이트

    /**
     * 비밀번호 검증
     *
     * @param userPwRequest
     * @return
     */
    public int verifyPassword(UserPwRequest userPwRequest);

    /**
     * 비밀번호 수정하기
     *
     * @param userPwRequest
     * @return
     */
    public int updatePw(UserPwRequest userPwRequest); // 비밀번호 변경

    /**
     * 관심주택 불러오기
     *
     * @param usersn
     * @return
     */
    public List<FavoriteResponse> getUserFavorite(int usersn);

    public int deleteFavorite(int favoriteSn);

    public void deleteUser(int usersn);
}
