FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew


RUN ./gradlew bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m"

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

# 5. 빌드합니다. (🔥 메모리 제한 및 데몬 비활성화 추가)
RUN ./gradlew bootJar -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m"

# 6. 서버가 실행될 때 이 명령어를 씁니다.
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

