# 1단계: 빌드 스테이지 (Gradle 사용)
FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/src

# 소스 코드 복사
COPY --chown=gradle:gradle . .

# [추가] gradlew 파일에 실행 권한 부여
RUN chmod +x ./gradlew

# 실행 가능한 JAR 파일 생성 (테스트는 스킵하여 빌드 속도 향상)
RUN ./gradlew bootJar -x test --no-daemon

# 2단계: 실행 스테이지 (가벼운 JRE 사용)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 복사
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 컨테이너 실행 시 자바 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# API 기본 포트 오픈
EXPOSE 8080