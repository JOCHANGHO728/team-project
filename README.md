📋 [졸업작품] 서버 배포 완료 및 안드로이드 연동 가이드
서버 배포가 완료되어 이제 로컬 환경(localhost)이 아닌 실제 클라우드 서버와 통신할 수 있습니다. 아래 내용을 확인해서 안드로이드 프로젝트 설정을 변경해 주세요.

1️⃣ 서버 접속 정보 (Base URL)
기존에 사용하던 IP 주소(예: 192.168.x.x 또는 localhost)를 아래 주소로 변경해야 합니다.

서버 Base URL: https://server-jc54.onrender.com

(주의: 끝에 / 슬래시는 빼고 입력해 주세요)

*(주의: 반드시 http가 아닌 **https*여야 합니다)

2️⃣ ⚠️ 중요: 서버 "수면 모드" 주의사항 (필독)
우리가 사용하는 서버는 무료 플랜이라서 **15분 동안 요청이 없으면 절전 모드(Sleep)**로 들어갑니다.

증상: 앱을 처음 켰을 때 데이터를 불러오는 데 약 50초~1분 정도 걸릴 수 있습니다.

해결: 고장 난 것이 아니니, 첫 요청 후 조금만 기다려 주세요. 한번 깨어나면 그 뒤로는 빠릅니다.

팁: 시연이나 테스트 전에는 미리 한 번 접속해서 서버를 깨워두는 것이 좋습니다.

3️⃣ 안드로이드 개발팀 수정 사항 (Checklist)
① Retrofit / OkHttp 설정 변경 RetrofitClient나 NetworkModule 파일에서 baseUrl을 위 주소로 변경해 주세요.

Java
// 예시 코드
.baseUrl("https://server-jc54.onrender.com")
② AndroidManifest.xml 권한 확인 인터넷 권한이 있는지 다시 한 번 확인해 주세요.

XML
<uses-permission android:name="android.permission.INTERNET" />
③ API 엔드포인트 기존에 로컬에서 테스트하던 API 주소(Controller 매핑 주소)는 그대로 사용하면 됩니다.

예시: GET /api/users, POST /api/login 등

4️⃣ 자주 발생하는 에러 해결
SSLHandshakeException: URL이 https://로 시작하는지 확인해 주세요. (보안 인증서 문제)

SocketTimeoutException: 서버가 깨어나는 중이라 응답이 늦어서 그렇습니다. 타임아웃 시간을 늘리거나, 잠시 후 다시 시도해 주세요.

404 Not Found: 요청한 주소(URI)가 맞는지 확인해 주세요. (Controller에 그 주소가 있는지)

