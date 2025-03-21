# Base image
FROM openjdk:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# Fat JAR 파일 복사 (올바른 JAR 이름 확인)
COPY build/libs/*.jar app.jar
RUN chmod +x app.jar

# 포트 노출 (7773 포트)
EXPOSE 7773

# 실행 명령어 (Spring Boot 애플리케이션 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]