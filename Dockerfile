# 베이스 이미지 설정 (OpenJDK 17 사용)
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행 포트 설정
EXPOSE 7773

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]