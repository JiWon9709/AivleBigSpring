package kt.aivle.base.exception;

import java.util.HashMap;
import java.util.Map;
import kt.aivle.base.BaseModel;
import kt.aivle.base.BaseResModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public BaseResModel<BaseModel> baseExceptionHandler(BaseException e, final WebRequest request) {
    BaseResModel<BaseModel> result = new BaseResModel<BaseModel>();

    printLog(BaseErrorCode.ERROR_ETC.getCode(), BaseErrorCode.ERROR_ETC.getValue(), e);

    result.setResultCode(BaseErrorCode.ERROR_ETC.getCode());
    result.setResultMsg(e.getMessage());
    return result;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public BaseResModel<Map<String, Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException e) {

    BaseResModel<Map<String, Map<String, String>>> result = new BaseResModel<Map<String, Map<String, String>>>();

    //컬럼 Validation 에러 메시지
    Map<String, Map<String, String>> errors = new HashMap<>();
    Map<String, String> map = new HashMap<>();

    e.getBindingResult().getAllErrors()
        .forEach(c -> map.put(((FieldError) c).getField(), c.getDefaultMessage()));

    errors.put("errors", map);

    printLog(BaseErrorCode.ERROR_ETC.getCode(), BaseErrorCode.ERROR_ETC.getValue(), e);
    result.setResultCode(BaseErrorCode.ERROR_PARAMETER_VALID.getCode());
    result.setResultMsg(BaseErrorCode.ERROR_PARAMETER_VALID.getValue());
    result.setData(errors);

    return result;
  }

  @ExceptionHandler(Exception.class)
  public BaseResModel<BaseModel> exceptionHandler(Exception e) {
    BaseResModel<BaseModel> result = new BaseResModel<>();

    result.setResultMsg(e.getMessage());
    result.setResultCode(412);

    return result;
  }

  /**
   * 로그 출력
   */
  public void printLog(int errorCode, String errorMsg, Exception e) {
    log.error("\n\n에러[ERROR] resultCode:{} / resultMsg:{} / message \n[[[\n{}\n]]]\n",
        errorCode, errorMsg, e.getMessage());
  }
}
