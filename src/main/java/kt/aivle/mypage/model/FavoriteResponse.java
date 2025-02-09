package kt.aivle.mypage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FavoriteResponse {
    private String jutaekName;
    private String jutaekSize;
    private String jutaekAddress;
    private String gongoType;
    private Integer favoriteSn;
    private Integer jutaekDtlSn;
    private Double structureScore;
    private Double infraScore;
    private String longitude;
    private String latitude;
    private Long guarantee;
    private Long monthly;
    private String jutaekImg;

}
