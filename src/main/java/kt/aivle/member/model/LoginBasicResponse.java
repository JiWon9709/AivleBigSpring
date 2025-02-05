package kt.aivle.member.model;

import lombok.Data;

@Data
public class LoginBasicResponse {
    private Integer userSn;
    private String userName;
    private String role;
    private String email;
}
