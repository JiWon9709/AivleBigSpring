package kt.aivle.member.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import kt.aivle.member.model.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } catch (ExpiredJwtException e) {
            try {
                // Refresh Token 확인 및 새로운 Access Token 발급
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    TokenDto newToken = jwtTokenProvider.refreshToken(refreshToken);

                    // 새로운 토큰을 응답 헤더에 추가
                    response.setHeader("Authorization", "Bearer " + newToken.getAccessToken());
                    response.setHeader("Refresh", newToken.getRefreshToken());

                    // 새로운 토큰으로 인증 처리
                    Authentication authentication = jwtTokenProvider.getAuthentication(newToken.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (JwtException refreshException) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.\"}");
            }

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰이 만료되었습니다.\"}");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals(request.getContextPath() + "/users/login") ||
                path.equals(request.getContextPath() + "/users/signup") ||
                path.equals(request.getContextPath() + "/users/logout") ||
                path.equals(request.getContextPath() + "/users/check-email") ||
                path.equals(request.getContextPath() + "/users/forgot-password") ||
                path.equals(request.getContextPath() + "/users/verify-code") ||
                path.equals(request.getContextPath() + "/gongo/active") ||
                // Swagger URLs 추가
                path.startsWith(request.getContextPath() + "/swagger-ui") ||
                path.startsWith(request.getContextPath() + "/v2/api-docs") ||
                path.startsWith(request.getContextPath() + "/swagger-resources") ||
                path.startsWith(request.getContextPath() + "/webjars");


        System.out.println("Request URI: " + path);
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Should Skip: " + shouldSkip);
        return shouldSkip;
    }
}