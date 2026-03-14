FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m"

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]