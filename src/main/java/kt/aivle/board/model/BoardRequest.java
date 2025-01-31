package kt.aivle.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoardRequest {
    @ApiModelProperty(notes = "사용자번호")
    private int userSn;
    @ApiModelProperty(notes="제목")
    private String title;
    @ApiModelProperty(notes="내용")
    private String content;
}
