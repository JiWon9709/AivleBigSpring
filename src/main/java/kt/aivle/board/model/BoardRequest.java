package kt.aivle.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BoardRequest {
    @ApiModelProperty(notes = "사용자번호")
    private int UserSn;
    @ApiModelProperty(notes="제목")
    private String title;
    @ApiModelProperty(notes="내용")
    private String content;
}
