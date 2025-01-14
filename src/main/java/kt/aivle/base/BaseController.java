package kt.aivle.base;

public class BaseController {
  protected BaseResModel response = new BaseResModel<>();
  public BaseResModel setView() {
    response.setResultCode(BaseMsg.SUCCESS.getCode());
    response.setResultMsg(BaseMsg.SUCCESS.getValue());
    return response;
  }
  public BaseResModel setView1() {
    response.setResultCode(BaseMsg.FAILED.getCode());
    response.setResultMsg(BaseMsg.FAILED.getValue());
    return response;
  }
}
