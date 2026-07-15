# 🛒 CustomerApp (스마트 오프라인 마트 보조 앱)

마트나 편의점을 이용하는 고객이 **방문 전 장바구니 구성**부터 **오프라인 매장에서의 바코드 스캔 및 셀프 결제**, 그리고 **스마트한 가계부 관리**까지 한 번에 처리할 수 있도록 돕는 Android 애플리케이션입니다.

---

## 🎯 주요 기능 (Key Features)

### 1. 사용자 인증 및 보안 (Auth & Security)
- **일반 로그인 및 회원가입**: REST API 기반 인증
- **생체 로그인 (Biometric Authentication)**: `Android BiometricPrompt`와 `EncryptedSharedPreferences`를 활용한 안전하고 빠른 지문/안면 인식 로그인

### 2. 스마트 쇼핑 (Shopping)
- **카테고리별 상품 탐색**: 마트의 전체 상품을 카테고리별로 조회
- **상품 검색**: 키워드 기반 빠른 상품 찾기
- **옵션 선택**: 일반 상품, 박스 상품, 대용량, 행사 상품 등 다채로운 옵션 선택 제공
- **이미지 최적화**: Cloudinary CDN을 통한 빠르고 효율적인 상품 이미지 로딩

### 3. 장바구니 (Cart)
- **온/오프라인 통합 장바구니**: 마트 방문 전에 미리 상품을 담아두고 관리
- **수량 조절 및 삭제**: 자유로운 아이템 관리 및 실시간 결제 예상 금액 계산
- **상태 분리**: 바코드로 '스캔된 상품'과 '아직 스캔되지 않은 상품'을 명확히 시각적(대각선 띠)으로 분리 표시

### 4. 바코드 스캔 (Barcode Scan)
- **카메라 스캔**: `CameraX`와 `ML Kit Barcode Scanning`을 결합한 빠르고 정확한 바코드 인식
- **실시간 반영**: 매장에서 스캔한 상품이 즉시 장바구니 '결제 대기' 상태로 전환
- **스마트 수량 계산**: 
  - 이미 장바구니에 담아둔 상품을 스캔하면 수량 유지 (확인 완료 처리)
  - 장바구니에 없던 상품을 스캔하면 기본 수량 1개로 신규 추가

### 5. 셀프 결제 (Self Checkout)
- **스캔 상품 전용 결제**: 사용자가 직접 바코드를 스캔한 상품들만 최종 결제 대상으로 지정
- **PG 연동 (PortOne)**: 토스페이, 카카오페이, 페이코 등 다양한 테스트 결제 지원
- **안전한 결제 처리**: 결제 성공 시 서버에 주문을 자동 생성하고, 장바구니에서 결제된 아이템만 깔끔하게 비움

### 6. 가계부 및 구매 내역 (Household Ledger)
- **월별 지출 요약**: MPAndroidChart를 활용한 카테고리별 원그래프(PieChart) 시각화 제공
- **상세 내역 조회**: 월 이동 버튼 및 특정 날짜(기간)를 지정하여 구매 영수증(내역) 상세 확인 가능

---

## 🛠 기술 스택 (Tech Stack)

| 구분 | 기술 / 라이브러리 |
|---|---|
| **언어 (Language)** | Java 17 |
| **빌드 도구 (Build)** | Gradle Kotlin DSL (`build.gradle.kts`) |
| **UI 및 레이아웃** | XML, ViewBinding, Material Components |
| **네트워크 (Network)** | Retrofit2, Gson Converter, OkHttp (Interceptor) |
| **카메라 & 비전 (Vision)**| CameraX, Google ML Kit (Barcode Scanning) |
| **이미지 로딩 (Image)** | Glide |
| **보안 및 인증 (Security)**| Android Biometric, Android Security Crypto |
| **결제 연동 (Payment)** | PortOne WebView 결제 연동 |
| **차트 및 시각화 (Chart)**| MPAndroidChart |

---

## 📁 프로젝트 구조 (Project Structure)

```text
app/src/main/java/com/example/customerapp
├── MainActivity.java
├── Login.java
├── SignUp.java
├── DataModel (데이터 및 네트워크 계층)
│   ├── ApiService.java            // API 명세 인터페이스
│   ├── RetrofitClient.java        // Retrofit 인스턴스 및 Interceptor (Token 자동 주입)
│   ├── CartManager.java           // 장바구니 및 유저 세션 싱글톤 관리 (Thread-safe)
│   └── Product.java, OrderItem.java ... // DTO 모델
└── Customer (UI 및 뷰 컨트롤러 계층)
    ├── Customer.java              // 메인 쇼핑 탭 (카테고리/상품 조회)
    ├── MyInfo.java                // 내 정보 탭
    ├── BarcodeScan
    │   └── BarcodeScan.java       // 카메라 바코드 스캔 액티비티
    ├── Shopping
    │   └── ProductAdapter.java    // 상품 목록 어댑터
    ├── Shoppingbasket
    │   ├── ShoppingBasket.java    // 장바구니 탭
    │   ├── Payment.java, PaymentSuccessActivity.java // 결제 관련 로직
    │   └── PortOnePaymentWebViewActivity.java
    └── Household_Ledger
        ├── household_Ledger.java  // 가계부 메인 탭 (차트)
        ├── LedgerDetailSearchActivity.java // 가계부 세부 기간 검색
        ├── LedgerResultActivity.java       // 가계부 기간 검색 결과
        └── DatePickerFragment.java
```

---

## 🌐 서버 API 명세 (API Endpoints)

**Base URL**: `https://server-jc54.onrender.com/`

| 기능 (Feature) | HTTP Method | Endpoint |
|---|---|---|
| **회원가입** | POST | `/api/v1/users/signup` |
| **로그인** | POST | `/api/v1/users/login` |
| **상품 검색** | GET | `/api/v1/products/search?keyword=` |
| **카테고리별 상품** | GET | `/api/v1/products?category=` |
| **바코드 단건 조회** | GET | `/api/v1/products/barcode?bKey=` |
| **주문 생성 (결제완료)** | POST | `/api/v1/orders` |
| **전체 구매내역** | GET | `/api/v1/purchase-history/{uId}` |
| **기간별 구매내역** | GET | `/api/v1/purchase-history/{uId}/range` |

> *참고: `RetrofitClient`에 적용된 Interceptor를 통해 `Authorization: Bearer <token>` 헤더가 모든 요청에 자동 포함됩니다.*

---

## 💳 결제 설정 (PortOne 연동)

결제는 PortOne의 웹뷰 브릿지 기반 테스트 환경을 사용합니다. (`app/build.gradle.kts` 내 Config 설정)

```kotlin
buildConfigField("String", "PORTONE_IMP_CODE", "\"imp03532732\"")
buildConfigField("String", "PORTONE_PG_TOSS", "\"tosspay.tosstest\"")
buildConfigField("String", "PORTONE_PG_KAKAO", "\"kakaopay.TC0ONETIME\"")
buildConfigField("String", "PORTONE_PG_PAYCO", "\"payco.PARTNERTEST\"")
```
- **결제 콜백 커스텀 스킴**: `customerapp://payment-callback`

---

## 🚀 빌드 및 실행 방법 (How to Run)

1. **Android Studio** 실행 후 `File > Open`을 통해 프로젝트 루트 폴더 열기
2. **Gradle Sync**를 실행하여 의존성 다운로드
3. (선택) 터미널을 통한 코드 검증:
   - 빌드 확인: `.\gradlew assembleDebug`
   - 정적 분석(Lint): `.\gradlew lintDebug`
4. Android 에뮬레이터 또는 물리적 디바이스(카메라 테스트 시 권장) 연결 후 `Run` 실행

---

## 📜 최근 주요 업데이트 및 리팩토링 내역

**v1.1.0 코드 안정화 및 사용성 개선**
- **스마트 스캔 및 결제 분리**: 장바구니에 미리 담은 상품과 실제 바코드를 스캔한 상품을 완벽히 분리하고, 스캔된 상품만 결제 서버로 넘어가도록 비즈니스 로직을 개편했습니다.
- **네트워크 레이어 최적화**: `RetrofitClient`에 `OkHttp Interceptor`를 도입해 모든 API에 인증 토큰을 자동으로 주입하도록 개선하여 코드 중복을 제거했습니다.
- **다국어 지원(Localization) 기초 구현**: 소스 코드 내부에 존재하던 텍스트(Toast, UI 텍스트)를 `strings.xml`로 완벽하게 추출/분리하여 향후 다국어 확장이 용이해졌습니다.
- **성능 및 스레드 안정성 향상**: `RecyclerView` 렌더링 시 전체 갱신(`notifyDataSetChanged`)을 지양하고 Diff 수준의 부분 갱신을 적용했습니다. 또한 싱글톤 `CartManager`를 `CopyOnWriteArrayList`로 전환해 동시성 문제(`ConcurrentModificationException`)를 해결했습니다.
- **UI 반응성 증대**: 바코드 스캔 후 상품 패널 등장 등 곳곳에 자연스러운 페이드/슬라이드 애니메이션을 추가했습니다.

---

> **주의사항 (Notice)**
> - 현재 결제 모듈은 PortOne의 **테스트 모드**로 동작하며 실제 과금되지 않습니다. 실 서비스 전환 시 서버사이드의 결제 검증 로직 추가가 필수적입니다.
> - 서버는 Render의 무료 플랜을 사용 중이므로, 장시간 미사용 후 최초 API 호출 시 서버가 켜지는 데 약간의 지연(Cold Start)이 발생할 수 있습니다.
