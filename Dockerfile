# ---- build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY gradlew gradle/ ./
COPY settings.gradle* build.gradle* ./
COPY src ./src
# 필요시: COPY 도메인/리소스 등 추가
RUN chmod +x gradlew && ./gradlew clean bootJar -x test

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8080"]
