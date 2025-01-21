package kt.aivle.member.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer userSn;
    private String userName;
    private String email;
    private String password;
    private String gender;
    private String address;
    private String zipCode;
    private String telno;
    private String useYn;
    private LocalDateTime createdDt;
    private LocalDateTime updatedDt;

}
