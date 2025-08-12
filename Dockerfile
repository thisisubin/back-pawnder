# OpenJDK 22 이미지 기반
FROM eclipse-temurin:22-jdk-jammy

# JAR 파일을 컨테이너 내 /app 폴더에 복사
COPY target/myapp.jar /app/myapp.jar

# 컨테이너 내 작업 디렉터리 설정
WORKDIR /app

# 애플리케이션 포트 노출
EXPOSE 8080

# Spring Boot 실행 명령
ENTRYPOINT ["java", "-jar", "myapp.jar", "--spring.profiles.active=prod"]
