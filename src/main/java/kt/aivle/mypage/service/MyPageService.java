package kt.aivle.mypage.service;

import kt.aivle.mypage.mapper.MyPageMapper;
import kt.aivle.mypage.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyPageService {
    private static final Logger log = LogManager.getLogger(MyPageService.class);
    @Autowired
    private MyPageMapper myPageMapper;

    /**
     * 사용자정보 가져오기
     *
     * @param userSn
     * @return
     */
    @Transactional
    public UserInfoResponse getuserInfo(int userSn) {
        UserInfoResponse result = new UserInfoResponse();

        try {
            UserInfo userinfo = myPageMapper.getUserInfo(userSn);
            result.setUserName(userinfo.getUserName());
            result.setEmail(userinfo.getEmail());
            result.setPassword(userinfo.getPassword());
            result.setGender(userinfo.getGender());
            result.setAddress(userinfo.getAddress());
            result.setZipCode(userinfo.getZipCode());
            result.setTelno(userinfo.getTelno());
        } catch (Exception e) {
            throw new RuntimeException("리스트를 불러오던 중 에러가 발생했습니다." + e);
        }
        return result;
    }

    /**
     * 사용자정보 수정하기
     *
     * @param userinfo
     * @return
     */
    @Transactional
    public void updateUser(UserInfo userinfo) {
        UserInfo existingUser = myPageMapper.getUserInfo(userinfo.getUserSn());

        try {
            // 사용자 정보 업데이트
            existingUser.setUserSn(userinfo.getUserSn());
            existingUser.setUserName(userinfo.getUserName());
            existingUser.setEmail(userinfo.getEmail());
            existingUser.setPassword(userinfo.getPassword()); // 주의: 비밀번호는 해싱해야 함
            existingUser.setGender(userinfo.getGender());
            existingUser.setAddress(userinfo.getAddress());
            existingUser.setZipCode(userinfo.getZipCode());
            existingUser.setTelno(userinfo.getTelno());

            // 업데이트된 사용자 정보 저장
            myPageMapper.updateUserInfo(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("리스트를 불러오던 중 에러가 발생했습니다." + e);
        }
    }

    /**
     * 비밀번호변경
     *
     * @param userPwRequest
     * @return
     */
    @Transactional
    public boolean updatePw(UserPwRequest userPwRequest) {
        // 1. 비밀번호 검증
        UserPwRequest pwRequest = new UserPwRequest();
        try {
            pwRequest.setUserSn(userPwRequest.getUserSn());
            pwRequest.setCurrentPassword(userPwRequest.getCurrentPassword()); // 주의: 비밀번호는 해싱해야 함
            int validPassword = myPageMapper.verifyPassword(pwRequest);

            if (validPassword == 0) {
                // 현재 비밀번호가 일치하지 않음
                return false;
            }
            // 2. 비밀번호 업데이트
            pwRequest.setNewPassword(userPwRequest.getNewPassword());
            int updatedRows = myPageMapper.updatePw(pwRequest);
            return updatedRows > 0; // 업데이트 성공 여부 반환
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 업데이트 중 에러가 발생했습니다." + e);
        }
    }

    /**
     * 관심주택정보 가져오기
     *
     * @param userSn
     * @return List<FavoriteResponse>
     */
    @Transactional
    public FavoriteListResponse getuserFavorite(int userSn) {
        FavoriteListResponse response = new FavoriteListResponse();

        try {
            // 사용자번호가 있는 관심 주택리스트 받기
            List<FavoriteResponse> favoriteList = myPageMapper.getUserFavorite(userSn);
            response.setFavoriteResponseList(favoriteList);
        } catch (Exception e) {
            throw new RuntimeException("리스트를 불러오던 중 에러가 발생했습니다." + e);
        }
        return response;
    }
}
