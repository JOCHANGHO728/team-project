# CustomerApp

마트/편의점 고객용 Android 쇼핑 앱입니다. 고객이 마트에 가기 전에 상품을 미리 장바구니에 담고, 매장에서 실제 구매할 상품의 바코드를 스캔한 뒤 스캔된 상품만 결제할 수 있도록 구성되어 있습니다.

## 주요 기능

- 회원가입 / 로그인
- 생체 로그인
  - Android BiometricPrompt 사용
  - EncryptedSharedPreferences 기반 로그인 정보 저장
- 상품 조회
  - 카테고리별 상품 목록 조회
  - 상품 검색
  - Cloudinary 상품 이미지 표시
  - 일반 상품 / 박스 상품 / 대용량 / 행사 상품 등 옵션 선택
- 장바구니
  - 마트 방문 전 상품 미리 담기
  - 상품 수량 변경 / 삭제
  - 장바구니 전체 상품 금액 계산
  - 바코드 스캔 상품과 미스캔 상품 구분 표시
- 바코드 스캔
  - CameraX + ML Kit Barcode Scanning 사용
  - 매장에서 스캔한 상품을 장바구니에 반영
  - 기존 장바구니에 있는 상품을 스캔하면 기존 수량을 유지
  - 장바구니에 없는 상품을 스캔하면 기본 수량 1개로 추가
- 결제
  - 바코드 스캔된 상품만 결제 대상
  - 토스페이 / 카카오페이 / 페이코 테스트 결제
  - 현장 결제 처리
  - 결제 성공 후 스캔된 상품만 주문 저장
  - 결제 성공 후 스캔된 상품만 장바구니에서 정리
- 구매내역 / 가계부
  - 월별 원그래프 소비 비율 표시
  - 월 이동 버튼으로 이전 달 / 다음 달 조회
  - 월별 총 금액 표시
  - 세부 날짜 조회 화면에서 기간별 구매내역 조회
- 내 정보
  - 사용자 정보 표시
  - 구매내역 이동
  - 로그아웃

## 최근 변경 사항

### 장바구니 / 바코드 스캔

- `activity_customer`에서 미리 담은 상품이 `activity_shopping_basket`의 총 상품 금액에 포함되도록 수정했습니다.
- 바코드 스캔으로 담은 상품만 현재 스캔 가격에 포함되도록 분리했습니다.
- 스캔된 상품은 장바구니 카드 왼쪽 상단에 대각선 `스캔됨` 띠가 표시됩니다.
- 스캔되지 않은 상품은 위쪽, 스캔된 상품은 아래쪽으로 정렬됩니다.
- 이미 장바구니에 있는 상품을 바코드로 스캔해도 수량이 자동으로 증가하지 않도록 수정했습니다.
- 장바구니에 없는 상품을 바코드로 스캔하면 기본 수량 1개로 추가됩니다.
- 스캔된 상품에서 `+` / `-`로 수량을 바꾸면 현재 스캔 가격도 함께 갱신됩니다.

### 결제

- 결제 대상이 장바구니 전체 상품에서 바코드 스캔된 상품으로 변경되었습니다.
- 스캔된 상품이 없으면 결제 화면으로 이동하지 않고 안내 메시지를 표시합니다.
- 결제 성공 화면의 영수증은 스캔된 상품만 표시합니다.
- 서버 주문 생성 요청도 스캔된 상품과 스캔 수량만 전송합니다.
- 결제 성공 후 장바구니 전체를 비우지 않고, 결제된 스캔 상품만 정리합니다.

### 가계부

- 가계부 메인 화면 상단에 월별 원그래프를 배치했습니다.
- 원그래프 위에 현재 월을 표시하고, 좌우 버튼으로 이전 달 / 다음 달을 이동할 수 있습니다.
- 원그래프 아래에 해당 월의 총 금액을 표시합니다.
- 기존 날짜 선택 조회 기능은 `세부 날짜 조회` 버튼을 통해 별도 화면에서 사용할 수 있도록 분리했습니다.

### 최적화 / 사용감 개선

- 상품 목록 검색과 카테고리 변경 시 어댑터를 매번 새로 만들지 않고 데이터만 갱신하도록 개선했습니다.
- 상품 목록, 장바구니 목록, 영수증 목록에 DiffUtil을 적용해 변경된 항목만 갱신하도록 했습니다.
- RecyclerView 고정 크기 설정으로 레이아웃 계산 부담을 줄였습니다.
- 바코드 스캔 화면과 장바구니 화면에서 RetrofitClient 공용 인스턴스를 재사용하도록 수정했습니다.
- 바코드 스캔 후 상품 패널이 부드럽게 표시되도록 짧은 페이드/슬라이드 애니메이션을 추가했습니다.
- 가계부 원그래프 애니메이션 시간을 줄여 화면 반응을 더 빠르게 했습니다.

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
│   └── PurchaseHistoryItem.java
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
        ├── LedgerDetailSearchActivity.java
        ├── LedgerResultActivity.java
        ├── LedgerReceiptAdapter.java
        └── DatePickerFragment.java
```

## 서버 API

기본 서버 주소:

```text
https://server-jc54.onrender.com/
```

주요 API:

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

## 결제 설정

PortOne 테스트 결제를 사용합니다.

설정 위치:

```text
app/build.gradle.kts
```

현재 설정:

```kotlin
buildConfigField("String", "PORTONE_IMP_CODE", "\"imp03532732\"")
buildConfigField("String", "PORTONE_PG_TOSS", "\"tosspay.tosstest\"")
buildConfigField("String", "PORTONE_PG_KAKAO", "\"kakaopay.TC0ONETIME\"")
buildConfigField("String", "PORTONE_PG_PAYCO", "\"payco.PARTNERTEST\"")
```

결제 콜백 스킴:

```text
customerapp://payment-callback
```

## 권한

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

## Android Studio 연결 방법

1. Android Studio 실행
2. `File > Open`
3. 아래 폴더 선택

```text
C:\Users\chang\source\repos\changho_BOX\team-project-CustomerApp-demo
```

4. Gradle Sync 실행
5. 에뮬레이터 또는 Android 기기 선택 후 Run 실행

## 빌드 / 검증

컴파일 확인:

```powershell
.\gradlew.bat :app:compileDebugJavaWithJavac
```

Lint 확인:

```powershell
.\gradlew.bat :app:lintDebug
```

## 주의사항

- 현재 결제는 PortOne 테스트 결제 기준입니다.
- 실제 운영 결제에서는 서버에서 `imp_uid` 기반 결제 검증을 추가해야 합니다.
- 장바구니 데이터는 앱 메모리의 `CartManager`에서 관리됩니다.
- Render 무료 인스턴스를 사용하는 경우 첫 요청 응답이 느릴 수 있습니다.
