package kt.aivle.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isTokenValid(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
