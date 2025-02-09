package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserProfileImageResponse {
    private String profileImage;

    public UserProfileImageResponse(String profileImage) {
        this.profileImage = profileImage;
    }
}
