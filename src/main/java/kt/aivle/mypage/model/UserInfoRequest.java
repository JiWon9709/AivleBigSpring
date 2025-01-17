package kt.aivle.mypage.model;

import lombok.Data;

@Data
public class UserInfoRequest {
    private String userName;
    private String email;
    private String password;
    private String gender;
    private String address;
    private String zipCode;
    private String telno;
}
