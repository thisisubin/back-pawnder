# OpenJDK 22 이미지 기반
FROM eclipse-temurin:21-jdk


# JAR 파일을 컨테이너 내 /app 폴더에 복사
COPY target/pawnder-0.0.1-SNAPSHOT.jar /app/pawnder-0.0.1-SNAPSHOT.jar

# 컨테이너 내 작업 디렉터리 설정
WORKDIR /app

# 애플리케이션 포트 노출
EXPOSE 8080

# Spring Boot 실행 명령
ENTRYPOINT ["java", "-jar", "pawnder-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
