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
     * 관심주택정보 가져오기
     *
     * @param userSn
     * @return List<FavoriteResponse>
     */
    @Transactional(readOnly = true)
    public FavoriteListResponse getuserFavorite(int userSn) {

        log.info("Fetching favorites for user: {}", userSn);
        try {
            // 사용자번호가 있는 관심 주택리스트 받기
            List<FavoriteResponse> favoriteList = myPageMapper.getUserFavorite(userSn);
            FavoriteListResponse response = new FavoriteListResponse();
            response.setFavoriteResponseList(favoriteList);
            return response;
        } catch (Exception e) {
            log.error("Error fetching favorites for user: " + userSn, e);
            throw new RuntimeException("Failed to fetch favorites: " + e.getMessage(), e);

        }
    }

    public boolean deleteFavorite(int favoriteSn) {
        int rowsAffected = myPageMapper.deleteFavorite(favoriteSn);
        return rowsAffected > 0; // 삭제 성공 여부 반환
    }

    public void deleteUser(int userSn) {
        myPageMapper.deleteUser(userSn);
    }


}
