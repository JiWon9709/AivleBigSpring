package kt.aivle.member.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class TokenInfo {
    private Long userSn;
    private String email;
    private List<String> roles;
}