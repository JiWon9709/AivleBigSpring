package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserProfileImageUpdateRequest {
    private Long userSn;
    private String profileImage;
}
