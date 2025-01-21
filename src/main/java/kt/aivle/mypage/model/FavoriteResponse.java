package kt.aivle.mypage.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FavoriteResponse {
    /**
     * favorite_sn : 관심주택 삭제를 위해 추가
     */
    @ApiModelProperty(notes="favorite_sn", position=5)
    private int favoriteSn;

    /**
     * 주택 이름
     */
    @ApiModelProperty(notes = "주택 이름", position = 1)
    private String jutaekName;

    /**
     * 주택 크기
     */
    @ApiModelProperty(notes = "주택 크기", position = 2)
    private String jutaekSize;

    /**
     * 주택 주소
     */
    @ApiModelProperty(notes = "주택 주소", position = 3)
    private String jutaekAddress;

    /**
     * 공고 타입
     */
    @ApiModelProperty(notes = "공고 이름", position = 4)
    private String gongoType;

}
