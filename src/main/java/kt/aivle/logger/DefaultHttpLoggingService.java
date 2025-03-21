package kt.aivle.logger;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultHttpLoggingService implements HttpLogging {

  @Override
  public void logRequest(HttpServletRequest request, Object body) {
    StringBuilder stringBuilder = new StringBuilder();
    Map<String, String> parameters = buildParametersMap(request);

    stringBuilder.append("REQUEST ");
    stringBuilder.append("method=[").append(request.getMethod()).append("] ");
    stringBuilder.append("path=[").append(request.getRequestURI()).append("] ");
    stringBuilder.append("headers=[").append(buildHeadersMap(request)).append("] ");

    if (!parameters.isEmpty()) {
      stringBuilder.append("parameters=[").append(parameters).append("] ");
    }
    if (body != null) {
      stringBuilder.append("body=[").append(body).append("]");
    }

    log.debug(stringBuilder.toString());
  }

  @Override
  public void logResponse(HttpServletRequest request, HttpServletResponse response, Object body) {

    String responseInfo = "RESPONSE " +
        "method=[" + request.getMethod() + "] " +
        "path=[" + request.getRequestURI() + "] " +
        "responseHeaders=[" + buildHeadersMap(response) + "] " +
        "responseBody=[" + body + "] ";
    log.debug(responseInfo);
  }

  private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
    Map<String, String> resultMap = new HashMap<>();
    Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

    while (parameterNames.hasMoreElements()) {
      String key = parameterNames.nextElement();
      String value = httpServletRequest.getParameter(key);
      resultMap.put(key, value);
    }

    return resultMap;
  }

  private Map<String, String> buildHeadersMap(HttpServletRequest request) {
    Map<String, String> map = new HashMap<>();

    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }

    return map;
  }

  private Map<String, String> buildHeadersMap(HttpServletResponse response) {
    Map<String, String> map = new HashMap<>();

    Collection<String> headerNames = response.getHeaderNames();
    for (String header : headerNames) {
      map.put(header, response.getHeader(header));
    }

    return map;
  }
}
