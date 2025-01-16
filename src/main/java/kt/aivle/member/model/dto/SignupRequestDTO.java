package kt.aivle.member.model.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String userName;
    private String email;
    private String password;
    private String gender;
    private String address;
    private String zipCode;
    private String telno;

}
