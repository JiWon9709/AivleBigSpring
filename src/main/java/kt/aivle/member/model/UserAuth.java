package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserAuth {
    private Integer userSn;
    private String password;
    private String role; // 권한 추가
    private String email;
    private String useYn;
}
