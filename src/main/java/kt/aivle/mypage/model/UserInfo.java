package kt.aivle.mypage.model;

import io.swagger.annotations.ApiModelProperty;
import kt.aivle.base.BaseMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserInfo {
    private static final long serialVersionUID = 1L;

    /**
     * 사용자번호
     */
    @ApiModelProperty(notes = "사용자번호", position = 1)
    private Integer userSn;
    /**
     * 사용자명
     */
    @ApiModelProperty(notes = "사용자명", position = 2)
    private String userName;

    /**
     * 이메일
     */
    @ApiModelProperty(notes = "이메일", position = 3)
    private String email;

    /**
     * 패스워드
     */
    @ApiModelProperty(notes = "패스워드", position = 4)
    private String password;

    /**
     * 성별
     */
    @ApiModelProperty(notes = "성별", position = 5)
    private String gender;

    /**
     * 주소
     */
    @ApiModelProperty(notes = "주소", position = 6)
    private String address;

    /**
     * 집우편번호
     */
    @ApiModelProperty(notes = "집우편번호", position = 7)
    private String zipCode;

    /**
     * 전화번호
     */
    @ApiModelProperty(notes = "전화번호", position = 8)
    private String telno;

    /**
     * 회원탈퇴여부
     */
    @ApiModelProperty(notes = "회원탈퇴여부", position = 9)
    private String useYn;
}
