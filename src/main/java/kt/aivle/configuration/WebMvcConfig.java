package kt.aivle.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kt.aivle.convert.CustomObjectMapper;
import kt.aivle.interceptor.RequestLoggingInterceptor;
import kt.aivle.logger.HttpLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"kt.aivle"})
public class WebMvcConfig implements WebMvcConfigurer, WebMvcRegistrations {

//  @Bean
//  public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(false);
//    config.addAllowedOrigin("*");
//    config.addAllowedHeader("*");
//    config.addAllowedMethod("*");
//    config.setMaxAge(6000L);
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", config);
//    FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(new CorsFilter(source));
//    filterBean.setOrder(0);
//    return filterBean;
//  }

  @Autowired
  private final HttpLogging loggingService;

  public WebMvcConfig(HttpLogging loggingService) {
    this.loggingService = loggingService;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RequestLoggingInterceptor(this.loggingService));
  }

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//            .allowedOrigins("http://localhost:3000/api") //React 도메인만 허용, 추후 서버배포시 변경 필요
//            .allowedMethods("GET", "POST", "DELETE", "OPTIONS") // 허용 메서드
//            .allowedHeaders("*")
//            .allowCredentials(true); // 인증 정보 허용
//
//  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter(customObjectMapper()));
  }

  @Bean
  public ObjectMapper customObjectMapper() {
    return new CustomObjectMapper();
  }

}
