package kt.aivle.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDTO<T> {
    private int resultCode; // 상태 코드
    private String resultMsg; // 메세지
    private T data; // 데이터

    // data 없이 사용할 수 있는 생성자 추가
    public ResponseDTO(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = null;
    }
}
