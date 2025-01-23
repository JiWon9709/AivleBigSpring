package kt.aivle.configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.AuthorizationScope;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2  // Swagger2를 사용하겠다는 어노테이션
@SuppressWarnings("unchecked")	// warning밑줄 제거를 위한 태그
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {


  // API마다 구분짓기 위한 설정.
  @Bean
  public Docket commonApi() {
    return getDocket("전체", Predicates.or(
        PathSelectors.regex("/test.*")));

  }

  @Bean
  public Docket allApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("kt.aivle"))
        .paths(Predicates.not(PathSelectors.regex("/error.*")))
        .paths(PathSelectors.any())
        .build()
        .useDefaultResponseMessages(false)
        // JWT 인증 설정 추가
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  //swagger 설정.
  public Docket getDocket(String groupName, Predicate<String> predicate) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName(groupName)
        .select()
        .apis(RequestHandlerSelectors.basePackage("kt.aivle"))
        .paths(predicate)
        .build();
  }

  //swagger ui 설정.
  @Bean
  public UiConfiguration uiConfig() {
    return UiConfigurationBuilder.builder()
        .displayRequestDuration(true)
        .validatorUrl("")
        .build();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
  // JWT 인증을 위한 ApiKey 설정
  private ApiKey apiKey() {
    return new ApiKey("JWT", "Authorization", "header");
  }

  // 보안 컨텍스트 설정
  private SecurityContext securityContext() {
    return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .build();
  }

  // Security Reference 설정
  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
  }
}

