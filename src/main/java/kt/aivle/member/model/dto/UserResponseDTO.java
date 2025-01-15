package kt.aivle.member.model.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer userSn;
    private String userName;
    private String email;
    private String gender;
    private String address;
    private String telno;
}
