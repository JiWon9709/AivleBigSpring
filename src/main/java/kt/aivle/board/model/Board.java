package kt.aivle.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Board {
    @ApiModelProperty(notes="게시판번호")
    private int boardSn;
    @ApiModelProperty(notes="사용자이름")
    private String userName;
    @ApiModelProperty(notes="사용자번호")
    private int userSn;
    @ApiModelProperty(notes="제목")
    private String title;
    @ApiModelProperty(notes="내용")
    private String content;
    @ApiModelProperty(notes = "생성일")
    private String createdDt;
//    private int views;
}
