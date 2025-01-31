package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserBasicInfoResponse {
    private Integer userSn;
    private String userName;
    private String email;
}
