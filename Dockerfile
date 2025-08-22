# ---- build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 1) Gradle Wrapper와 설정 파일을 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* build.gradle* ./

# 2) 앱 소스 복사
COPY src ./src

# 3) 빌드
# BuildKit 사용 시 캐시 마운트: docker buildx 또는 compose가 자동 활성화
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && \
    ./gradlew clean bootJar -x test --no-daemon

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8080"]
