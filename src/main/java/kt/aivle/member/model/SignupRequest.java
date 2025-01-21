package kt.aivle.member.model;

import lombok.Data;

@Data
public class SignupRequest {
    private String userName;
    private String email;
    private String password;
    private String gender;
    private String address;
    private String zipCode;
    private String telno;
    private String useYn;

}
