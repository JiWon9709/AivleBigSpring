package kt.aivle.member.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
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
        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtTokenProvider.parseClaims(token).getSubject();
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenProvider.validateToken(token)) {
                    var authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new RuntimeException("Invalid JWT token");
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰이 만료되었습니다.\"}");
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"유효하지 않은 토큰입니다.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Server Error\", \"message\": \"서버 오류가 발생했습니다.\"}");
        }


    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals(request.getContextPath() + "/users/login") ||
                path.equals(request.getContextPath() + "/users/signup") ||
                path.equals(request.getContextPath() + "/users/reissue") ||
                path.equals(request.getContextPath() + "/users/logout") ||
                path.equals(request.getContextPath() + "/users/check-email") ||
                path.equals(request.getContextPath() + "/users/forgot-password") ||
                path.equals(request.getContextPath() + "/gongo/active") ||
                // Swagger URLs 추가
                path.startsWith(request.getContextPath() + "/swagger-ui") ||
                path.startsWith(request.getContextPath() + "/v2/api-docs") ||
                path.startsWith(request.getContextPath() + "/swagger-resources") ||
                path.startsWith(request.getContextPath() + "/webjars");
                ;

        System.out.println("Request URI: " + path);
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Should Skip: " + shouldSkip);
        return shouldSkip;
    }
}
