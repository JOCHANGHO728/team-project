FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

COPY src src
RUN ./gradlew bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m"

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]