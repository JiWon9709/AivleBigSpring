package kt.aivle.member.model;

import lombok.Data;

@Data
public class VerificationRequest {
    private String email;
    private String verificationCode;
}
