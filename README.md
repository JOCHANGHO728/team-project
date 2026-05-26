# CustomerApp

마트/편의점 고객용 쇼핑 Android 앱입니다. 상품 조회, 장바구니, 바코드 스캔, 간편결제 테스트, 구매내역 조회, 생체로그인을 지원합니다.

## 주요 기능

- 회원가입 / 로그인
- 생체로그인
  - Android BiometricPrompt 사용
  - EncryptedSharedPreferences 기반 로그인 정보 저장
- 상품 조회
  - 카테고리별 상품 목록 조회
  - 상품 검색
  - Cloudinary 상품 이미지 표시
  - 일반상품 / 1박스 / 대용량 / 행사상품 등 옵션 선택
  - 1박스 상품 할인율 표시
- 장바구니
  - 상품 추가 / 수량 변경 / 삭제
  - 상품 이미지 표시
  - 총 결제 금액 계산
- 바코드 스캔
  - CameraX + ML Kit Barcode Scanning 사용
  - 스캔한 상품을 장바구니에 추가
- 결제
  - PortOne 테스트 결제 연동
  - 토스페이 / 카카오페이 / 페이코 선택 가능
  - 현장결제 처리
  - 결제 성공 시 영수증 화면 표시
  - 결제 성공 후 구매내역 DB 저장 및 장바구니 비우기
- 구매내역 / 가계부
  - 전체 구매내역 조회
  - 기간별 구매내역 조회
  - 날짜 미입력 시 오늘 날짜 기준 조회
- 내정보
  - 사용자 정보 표시
  - 구매내역 이동
  - 로그아웃

## 기술 스택

- Android Java
- Gradle Kotlin DSL
- ViewBinding
- Retrofit2
- Gson Converter
- CameraX
- ML Kit Barcode Scanning
- PortOne WebView 결제
- Android Biometric
- Android Security Crypto
- Glide
- MPAndroidChart
- Material Components

## 프로젝트 구조

```text
app/src/main/java/com/example/customerapp
├── MainActivity.java
├── Login.java
├── SignUp.java
├── DataModel
│   ├── ApiService.java
│   ├── RetrofitClient.java
│   ├── CartManager.java
│   ├── Product.java
│   ├── OrderRequest.java
│   ├── OrderItem.java
│   ├── PurchaseHistoryItem.java
│   └── ...
└── Customer
    ├── Customer.java
    ├── MyInfo.java
    ├── BarcodeScan
    │   └── BarcodeScan.java
    ├── Shopping
    │   └── ProductAdapter.java
    ├── Shoppingbasket
    │   ├── ShoppingBasket.java
    │   ├── Payment.java
    │   ├── PortOnePaymentWebViewActivity.java
    │   ├── PaymentSuccessActivity.java
    │   ├── CartAdapter.java
    │   └── CartItem.java
    └── Household_Ledger
        ├── household_Ledger.java
        ├── LedgerResultActivity.java
        ├── LedgerReceiptAdapter.java
        └── DatePickerFragment.java
```

## 서버 API

기본 서버 주소:

```text
https://server-jc54.onrender.com/
```

사용 API:

| 기능 | Method | Endpoint |
|---|---|---|
| 회원가입 | POST | `/api/v1/users/signup` |
| 로그인 | POST | `/api/v1/users/login` |
| 상품 검색 | GET | `/api/v1/products/search?keyword=` |
| 카테고리별 상품 조회 | GET | `/api/v1/products?category=` |
| 바코드 상품 조회 | GET | `/api/v1/products/barcode?bKey=` |
| 주문 생성 | POST | `/api/v1/orders` |
| 전체 구매내역 조회 | GET | `/api/v1/purchase-history/{uId}` |
| 기간별 구매내역 조회 | GET | `/api/v1/purchase-history/{uId}/range` |

## 상품 이미지 표시

상품 조회 API 응답의 `imageUrl` 값을 Glide로 불러와 상품 목록과 장바구니에 표시합니다.
이미지 URL이 없거나 로드에 실패하면 `ic_placeholder` 기본 이미지가 표시됩니다.

서버 응답 예시:

```json
{
  "pId": 1,
  "pName": "농심 새우깡 90g",
  "imageUrl": "https://res.cloudinary.com/example/product.webp"
}
```

관련 파일:

- `app/src/main/java/com/example/customerapp/DataModel/Product.java`: `imageUrl` / `image_url` 응답 매핑
- `app/src/main/java/com/example/customerapp/Customer/Shopping/ProductAdapter.java`: 상품 목록 이미지 로딩
- `app/src/main/java/com/example/customerapp/Customer/Shoppingbasket/CartAdapter.java`: 장바구니 이미지 로딩

## 결제 설정

PortOne 테스트 결제를 사용합니다.

설정 위치:

```text
app/build.gradle.kts
```

현재 설정값:

```kotlin
buildConfigField("String", "PORTONE_IMP_CODE", "\"imp03532732\"")
buildConfigField("String", "PORTONE_PG_TOSS", "\"tosspay.tosstest\"")
buildConfigField("String", "PORTONE_PG_KAKAO", "\"kakaopay.TC0ONETIME\"")
buildConfigField("String", "PORTONE_PG_PAYCO", "\"payco.PARTNERTEST\"")
```

결제 콜백 딥링크:

```text
customerapp://payment-callback
```

AndroidManifest 등록 위치:

```text
app/src/main/AndroidManifest.xml
```

## 권한

앱에서 사용하는 권한:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

## 실행 방법

1. Android Studio에서 프로젝트 열기

```text
C:\Users\chang\intelliJ
```

2. Gradle Sync 실행

3. 앱 빌드

```powershell
.\gradlew.bat :app:compileDebugJavaWithJavac
```

4. 앱 실행

Android Studio에서 에뮬레이터 또는 실제 Android 기기를 선택한 뒤 Run 실행

## 검증 명령어

컴파일 확인:

```powershell
.\gradlew.bat :app:compileDebugJavaWithJavac
```

Lint 확인:

```powershell
.\gradlew.bat :app:lintDebug
```

## 테스트 시나리오

### 로그인

1. 회원가입
2. 로그인
3. 생체로그인 체크 후 로그인
4. 앱 재실행 후 지문 인증 로그인 확인

### 상품 / 장바구니

1. 상품 카테고리 선택
2. 상품 검색
3. 상품 클릭 후 옵션 선택
4. 장바구니 담기
5. 장바구니에서 수량 변경 및 삭제 확인
6. 상품 목록과 장바구니에서 상품 이미지 표시 확인
7. 이미지가 없는 상품은 기본 이미지가 표시되는지 확인

### 바코드 스캔

1. 장바구니 화면 진입
2. 바코드 스캔 버튼 클릭
3. 상품 바코드 스캔
4. 장바구니로 돌아온 뒤 상품 추가 여부 확인

### 결제

1. 장바구니에 상품 추가
2. 결제하기 클릭
3. 간편결제 또는 현장결제 선택
4. 간편결제 선택 시 토스페이 / 카카오페이 / 페이코 중 선택
5. 테스트 결제 진행
6. 결제 성공 화면에서 영수증 확인
7. 장바구니로 돌아가기 클릭
8. 장바구니 비어있는지 확인
9. 구매내역에 결제 상품 저장됐는지 확인

### 구매내역

1. 가계부 탭 이동
2. 날짜 미입력 상태로 조회
3. 오늘 구매내역 조회 확인
4. 시작일 / 종료일 입력 후 기간 조회 확인

## 주의사항

- 현재 결제는 PortOne 테스트 결제 기준입니다.
- 실제 운영 결제에서는 서버에서 `imp_uid` 기반 결제 검증을 추가해야 합니다.
- 장바구니 데이터는 앱 메모리의 `CartManager`에서 관리합니다.
- 서버가 Render 무료 인스턴스인 경우 첫 요청 시 응답이 느릴 수 있습니다.

## 빌드 환경

- Android Gradle Plugin: 8.9.1
- Compile SDK: 36
- Target SDK: 35
- Min SDK: 24
- Java: 11
```

**Verification**
```powershell
Get-Content README.md
```

원하면 다음에 바로 `README.md` 파일로 실제 생성까지 해줄게요.
