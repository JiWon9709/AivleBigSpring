package kt.aivle.interceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kt.aivle.logger.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RequestLoggingInterceptor implements HandlerInterceptor {

  private final HttpLogging httpLoggingService;

  public RequestLoggingInterceptor(HttpLogging httpLoggingService) {
    this.httpLoggingService = httpLoggingService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
        && request.getMethod().equals(HttpMethod.GET.name())) {
      if (!request.getRequestURI().endsWith(".css")
          && !request.getRequestURI().endsWith(".js")
          && !request.getRequestURI().contains("/images/")) {
        httpLoggingService.logRequest(request, null);
      }
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

  }
}

