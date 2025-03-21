package kt.aivle.member.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer userSn;
    private String password;
    private String userName;
    private String role; // 권한 추가
    private String email;
    private String gender;
    private String zipCode;
    private String address;
    private String telno;
    private String useYn;
    private LocalDateTime withdrawalDt; // 탈퇴 날짜
}
