package kt.aivle.member.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kt.aivle.member.model.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;
    // 토큰 만료 시간 설정
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;   //30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;  //24시간

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes); // Key 객체 생성
    }

    // 신규 사용자에게 Access/Refresh 토큰 생성
    public TokenDto createToken(String userPk, List<String> roles) {
        log.debug("Creating Access and Refresh tokens for user: {}, roles: {}", userPk, roles);

        // 토큰에 userPk 사용자 정보 넣어주기
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        log.info("Access token expiration: {}", accessTokenExpiresIn);
        log.info("Refresh token expiration: {}", refreshTokenExpiresIn);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Access token successfully created.");
        log.info("Refresh token successfully created.");

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    // refresh 토큰 이용한 access 재발급
    public TokenDto createAccessTokenByRefresh(String userPk, List<String> roles, String existingRefreshToken) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(existingRefreshToken)  // 기존 리프레시 토큰 유지
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }


    // access 만료되어서 reissue 요청한 사용자에 대해 리프레시 토큰 이용한 access 토큰 재발급
    public TokenDto refreshToken(String refreshToken) {
        log.debug("Attempting to refresh token: {}", refreshToken);
        try {
            Claims claims = parseClaims(refreshToken);
            String userPk = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            // 액세스 토큰만 새로 발급
            return createAccessTokenByRefresh(userPk, roles, refreshToken);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            throw new RuntimeException("만료된 Refresh Token입니다.");
        }
    }

    // 인증 처리
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get("roles") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 토큰에서 직접 정보를 추출하여 Authentication 객체 생성
        List<String> roles = claims.get("roles", List.class);
        Collection<? extends GrantedAuthority> authorities =
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // UserDetails 객체를 DB 조회 없이 생성
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        log.debug("Validating token: {}", token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.info("Token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT token error: {}", e.getMessage());
            throw e;
        }
    }

    // Claims 파싱
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰도 Claims를 반환
        }
    }
}