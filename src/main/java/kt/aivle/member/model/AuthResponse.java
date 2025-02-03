package kt.aivle.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private int statusCode;     // HTTP 상태 코드
    private String message;     // 응답 메시지
    private Object data;        // 추가 데이터가 필요한 경우

    // 성공 응답을 위한 정적 팩토리 메소드
    public static AuthResponse success(String message) {
        return AuthResponse.builder()
                .statusCode(200)
                .message(message)
                .build();
    }

    // 실패 응답을 위한 정적 팩토리 메소드
    public static AuthResponse error(int statusCode, String message) {
        return AuthResponse.builder()
                .statusCode(statusCode)
                .message(message)
                .build();
    }
}