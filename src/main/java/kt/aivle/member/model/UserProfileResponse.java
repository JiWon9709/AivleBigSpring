package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Integer userSn;
    private String userName;
    private String email;
    private String telno;
    private String address;
    private String zipCode;
    private String gender;
}
