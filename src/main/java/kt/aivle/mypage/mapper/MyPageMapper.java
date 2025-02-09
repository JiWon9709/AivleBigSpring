package kt.aivle.mypage.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.mypage.model.*;

import java.util.List;

@DATA_SOURCE
public interface MyPageMapper {

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
