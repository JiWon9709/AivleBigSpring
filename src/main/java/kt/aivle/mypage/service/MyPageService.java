package kt.aivle.mypage.service;

import kt.aivle.mypage.mapper.MyPageMapper;
import kt.aivle.mypage.model.UserInfo;
import kt.aivle.mypage.model.UserInfoRequest;
import kt.aivle.mypage.model.UserInfoResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userInfoRequest
     * @return
     */
    @Transactional
    public void updateUser(Integer userSn, UserInfoRequest userInfoRequest) {
        UserInfo existingUser = myPageMapper.getUserInfo(userSn);

        try {
            // 사용자 정보 업데이트
            existingUser.setUserName(userInfoRequest.getUserName());
            existingUser.setEmail(userInfoRequest.getEmail());
            existingUser.setPassword(userInfoRequest.getPassword()); // 주의: 비밀번호는 해싱해야 함
            existingUser.setGender(userInfoRequest.getGender());
            existingUser.setAddress(userInfoRequest.getAddress());
            existingUser.setZipCode(userInfoRequest.getZipCode());
            existingUser.setTelno(userInfoRequest.getTelno());

            // 업데이트된 사용자 정보 저장
            myPageMapper.updateUserInfo(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("리스트를 불러오던 중 에러가 발생했습니다." + e);
        }
    }
}
