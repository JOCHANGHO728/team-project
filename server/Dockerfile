FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m"

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]