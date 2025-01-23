package kt.aivle.member.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VerificationInfo {
    private String verificationCode;
    private LocalDateTime createdAt;
}
