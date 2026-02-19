# 1. 더 안정적인 Java 17 이미지를 사용합니다.
FROM eclipse-temurin:17-jdk-jammy

# 2. 작업할 폴더를 만듭니다.
WORKDIR /app

# 3. 내 컴퓨터에 있는 파일들을 다 복사해서 서버로 가져갑니다.
COPY . .

# 4. gradlew 실행 권한을 줍니다.
RUN chmod +x ./gradlew

# 5. 빌드합니다.
RUN ./gradlew bootJar

# 6. 서버가 실행될 때 이 명령어를 씁니다. (수정된 부분)
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]
