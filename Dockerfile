FROM eclipse-temurin:17-jre
WORKDIR /app
# Gradle: build/libs/app.jar  (Maven이면 target/app.jar 로 변경)
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=8080"]
