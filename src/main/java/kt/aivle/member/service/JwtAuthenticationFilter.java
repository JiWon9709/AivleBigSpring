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
import javax.servlet.http.Cookie;
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

        try {
            // 쿠키에서 토큰 추출
            String accessToken = null;
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        accessToken = cookie.getValue();
                    } else if (cookie.getName().equals("refresh_token")) {
                        refreshToken = cookie.getValue();
                    }
                }
            }

            try {
                if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                try {
                    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                        TokenDto newToken = jwtTokenProvider.refreshToken(refreshToken);

                        // 새로운 Access Token을 쿠키에 설정
                        Cookie newAccessTokenCookie = new Cookie("access_token", newToken.getAccessToken());
                        newAccessTokenCookie.setHttpOnly(true);
                        newAccessTokenCookie.setSecure(true);
                        newAccessTokenCookie.setPath("/");

                        response.addCookie(newAccessTokenCookie);

                        Authentication authentication = jwtTokenProvider.getAuthentication(newToken.getAccessToken());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (JwtException refreshException) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"다시 로그인해주세요.\"}");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (IOException e) {
            throw new ServletException("Failed to process the request", e);
        }
    }


//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI();
//        boolean shouldSkip = path.equals(request.getContextPath() + "/users/login") ||
//                path.equals(request.getContextPath() + "/users/signup") ||
//                path.equals(request.getContextPath() + "/users/check-email") ||
//                path.equals(request.getContextPath() + "/users/forgot-password") ||
//                path.equals(request.getContextPath() + "/users/verify-code") ||
//                path.equals(request.getContextPath() + "/gongo/active") ||
//                // Swagger URLs 추가
//                path.startsWith(request.getContextPath() + "/swagger-ui") ||
//                path.startsWith(request.getContextPath() + "/v2/api-docs") ||
//                path.startsWith(request.getContextPath() + "/swagger-resources") ||
//                path.startsWith(request.getContextPath() + "/webjars");
//
//
//        System.out.println("Request URI: " + path);
//        System.out.println("Context Path: " + request.getContextPath());
//        System.out.println("Should Skip: " + shouldSkip);
//        return shouldSkip;
//    }
}