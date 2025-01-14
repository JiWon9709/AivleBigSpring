package kt.aivle.api.util;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpReqRespUtils {
  private static final String[] IP_HEADER_CANDIDATES = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  };

  public static String getClientIpAddressIfServletRequestExist() {

    if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
      return "0.0.0.0";
    }

    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    for (String header: IP_HEADER_CANDIDATES) {
      String ipFromHeader = request.getHeader(header);
      if (Objects.nonNull(ipFromHeader) && ipFromHeader.length() != 0 && !"unknown".equalsIgnoreCase(ipFromHeader)) {
        String ip = ipFromHeader.split(",")[0];
        return ip;
      }
    }
    return request.getRemoteAddr();
  }

  public static String getRemoteIP(HttpServletRequest request){
    String ip = request.getHeader("X-FORWARDED-FOR");

    //proxy 환경일 경우
    if (ip == null || ip.length() == 0) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    //웹로직 서버일 경우
    if (ip == null || ip.length() == 0) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ip == null || ip.length() == 0) {
      ip = request.getRemoteAddr() ;
    }

    return ip;
  }
}
