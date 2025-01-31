package kt.aivle.member.model;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String address;
    private String zipCode;
    private String telno;
}
