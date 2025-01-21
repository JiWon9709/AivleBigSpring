package kt.aivle.mypage.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FavoriteListResponse {
    @ApiModelProperty(required = true, notes = "관심주택리스트")
    private List<FavoriteResponse> favoriteResponseList;
}
