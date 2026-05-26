
# Team Project - Shopping Mall API Server

Spring Boot 기반 쇼핑몰 REST API 서버입니다. Android 클라이언트와 통신하며, 사용자/관리자 인증, 상품 관리, 주문, 구매 내역 조회 기능을 제공합니다.

## 기술 스택

- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- BCrypt (`spring-security-crypto`)

## 실행 환경 변수

서버 실행 전 아래 환경 변수를 설정해야 합니다.

- `DB_HOST`
- `DB_PORT` (기본값 `4000`)
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `MANAGER_AUTH_SECRET`
- `USER_AUTH_SECRET`
- `MANAGER_AUTH_TOKEN_TTL_MILLIS` (기본값 `43200000`)
- `USER_AUTH_TOKEN_TTL_MILLIS` (기본값 `43200000`)

## 인증 방식

- 로그인 성공 시 서버가 HMAC-SHA256 기반 서명 토큰을 발급합니다.
- 사용자/관리자 토큰은 서로 다른 시크릿 키를 사용합니다.
- 토큰 payload에는 타입(`USER` / `MANAGER`)이 포함되어 권한 혼동을 방지합니다.
- 인증이 필요한 API는 `Authorization: Bearer {token}` 헤더가 필요합니다.

## API 구성

### 1) 사용자 API (`/api/v1/users`)
- `POST /login`: 사용자 로그인
- `POST /signup`: 사용자 회원가입

### 2) 관리자 API (`/api/v1/managers`)
- `POST /login`: 관리자 로그인
- `GET /products`: 전체 상품 조회
- `POST /products`: 상품 등록
- `PUT /products/{pId}`: 상품 수정
- `DELETE /products/{pId}`: 상품 삭제
- `GET /products/search?name=`: 상품 검색
- `GET /purchase-history`: 전체 회원 구매 내역 조회

### 3) 상품 API (`/api/v1/products`)
- `GET ?category=`: 카테고리별 상품 조회
- `GET /search?keyword=`: 키워드 상품 검색
- 상품 조회 응답에는 Cloudinary 상품 이미지 주소인 `imageUrl`이 포함됩니다.

상품 조회 응답 예시:

```json
{
  "pId": 1,
  "pName": "농심 새우깡 90g",
  "pPrice": 1500,
  "pQuantity": 10,
  "bKey": "880000000001",
  "category": "과자",
  "imageUrl": "https://res.cloudinary.com/example/product.webp"
}
```

DB의 `productdb.image_url` 컬럼에 저장된 URL이 API의 `imageUrl`로 반환됩니다.

### 4) 주문 API (`/api/v1/orders`)
- `POST /`: 주문 생성

### 5) 구매 내역 API (`/api/v1/purchase-history`)
- `GET /{uId}`: 사용자 전체 구매 내역
- `GET /{uId}/range?startDate=&endDate=`: 기간별 구매 내역

## 보안/무결성 처리

- 비밀번호는 BCrypt 해시로 저장/비교합니다.
- 주문 생성 시 `authenticatedUserId`를 강제 사용해 IDOR를 방지합니다.
- 구매 내역 조회 시 URL의 `uId`와 인증 사용자 ID를 비교해 본인 조회만 허용합니다.
- 재고 차감 시 비관적 락(Pessimistic Write)을 사용해 동시성 문제를 줄입니다.
- DTO 입력값은 Bean Validation으로 검증합니다.

## 예외 응답

- 공통 예외 포맷은 `ApiResponse.fail(...)`을 사용합니다.
- `GlobalExceptionHandler`에서 검증 오류/비즈니스 오류/기타 예외를 처리하며 로깅합니다.

## 빌드/테스트

```bash
cd server
./gradlew test
./gradlew bootRun
```

## 상품 이미지 응답 관련 파일

- `server/src/main/java/com/example/server/entity/Product.java`: DB `image_url` 컬럼 매핑
- `server/src/main/java/com/example/server/dto/ProductResponseDto.java`: 상품 API 응답에 `imageUrl` 포함
- `server/src/test/java/com/example/server/dto/ProductResponseDtoTest.java`: 이미지 URL 응답 매핑 검증
