package kt.aivle.member.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private int resultCode;
    private String resultMsg;
    private UserResponse user;
}
