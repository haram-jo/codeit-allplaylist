# 1️⃣ Build stage
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

# 소스 복사 (gradle 유저 권한 유지)
COPY --chown=gradle:gradle . .

# 실행 권한 부여 및 gradlew로 빌드 (이게 더 에러가 적습니다)
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test --no-daemon

# 2️⃣ Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

#application.docker-properties 실행하도록 > application.properties 보다 우선순위임
ENV SPRING_PROFILES_ACTIVE=docker

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
