package kt.aivle.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Gongo {
    @ApiModelProperty(notes="공고번호")
    private int gongoSn;
    @ApiModelProperty(notes="공고명")
    private String gongoName;
    @ApiModelProperty(notes="공고타입")
    private int gongoType;

    @ApiModelProperty(notes = "시작일")
    private String scheduleStartDt;
    @ApiModelProperty(notes = "종료일")
    private String scheduleEndDt;
    @ApiModelProperty(notes="서류시작일")
    private String documentStartDt;
    @ApiModelProperty(notes="서류종료일")
    private String documentEndDt;
    @ApiModelProperty(notes = "생성일자")
    private String createdDt;
    @ApiModelProperty(notes = "수정일자")
    private String updatedDt;
}
