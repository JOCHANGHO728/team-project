팀원분이 깃허브(GitHub)에서 바로 보고 복사하기 좋도록, **Markdown(마크다운) 형식**으로 깔끔하게 정리해 드립니다.

아래 내용을 복사해서 깃허브의 `README.md` 파일이나 팀 노션(Notion), 위키(Wiki) 등에 그대로 붙여넣기 하시면 됩니다!

---

# 📱 Android (Kotlin) 관리자 API 연동 가이드

이 문서는 스마트 쇼핑 카트 앱의 **관리자 기능(로그인, 매장 상품 관리)**을 서버와 연동하기 위한 안드로이드(Kotlin) 구현 가이드입니다. `Retrofit2` 라이브러리를 기준으로 작성되었습니다.

## 1. 초기 세팅 (Setup)

### 1-1. 라이브러리 추가 (`build.gradle.kts (Module :app)`)

```kotlin
dependencies {
    // Retrofit2 & Gson Converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}

```

### 1-2. 인터넷 권한 허용 (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.INTERNET" />

```

---

## 2. 데이터 클래스 (DTO)

서버와 통신할 때 주고받을 JSON 데이터의 규격입니다. 서버의 변수명과 정확히 매칭되어야 합니다.

```kotlin
import com.google.gson.annotations.SerializedName

// [요청] 관리자 로그인
data class ManagerLoginRequest(
    @SerializedName("managerId") val managerId: String,
    @SerializedName("mPassword") val mPassword: String
)

// [응답] 상품 정보 (조회 시)
data class ProductResponse(
    @SerializedName("pid") val pId: Long,
    @SerializedName("pname") val pName: String,
    @SerializedName("pprice") val pPrice: Int,
    @SerializedName("pquantity") val pQuantity: Int,
    @SerializedName("bkey") val bKey: String,
    @SerializedName("category") val category: String
)

// [요청] 상품 등록
data class ProductCreateRequest(
    @SerializedName("pName") val pName: String,
    @SerializedName("pPrice") val pPrice: Int,
    @SerializedName("pQuantity") val pQuantity: Int,
    @SerializedName("bKey") val bKey: String,
    @SerializedName("category") val category: String
)

// [요청] 상품 수정
data class ProductUpdateRequest(
    @SerializedName("pPrice") val pPrice: Int,
    @SerializedName("pQuantity") val pQuantity: Int
)

```

---

## 3. API 인터페이스 명세서 (API Interface)

서버의 어떤 URL로 어떤 데이터를 보낼지 정의합니다.

```kotlin
import retrofit2.Call
import retrofit2.http.*

interface ManagerApi {
    // 1. 관리자 로그인
    @POST("/api/v1/managers/login")
    fun login(@Body request: ManagerLoginRequest): Call<String>

    // 2. 상품 전체 조회
    @GET("/api/v1/managers/products")
    fun getAllProducts(): Call<List<ProductResponse>>

    // 3. 상품 추가
    @POST("/api/v1/managers/products")
    fun addProduct(@Body request: ProductCreateRequest): Call<String>

    // 4. 상품 수정 (pId: 수정할 상품의 ID)
    @PUT("/api/v1/managers/products/{pId}")
    fun updateProduct(
        @Path("pId") productId: Long,
        @Body request: ProductUpdateRequest
    ): Call<String>

    // 5. 상품 삭제 (pId: 삭제할 상품의 ID)
    @DELETE("/api/v1/managers/products/{pId}")
    fun deleteProduct(@Path("pId") productId: Long): Call<String>
}

```

---

## 4. 실제 API 호출 예시 (Activity / ViewModel)

화면(UI)에서 버튼 클릭 시 Retrofit을 이용해 서버에 요청을 보내는 예제입니다.

```kotlin
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ManagerActivity : AppCompatActivity() {

    // ⚠️ TODO: [본인의_Render_주소] 부분을 실제 배포된 서버 URL로 변경하세요!
    private val BASE_URL = "https://[본인의_Render_주소].onrender.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val managerApi = retrofit.create(ManagerApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- 1. 로그인 요청 예시 ---
        val loginData = ManagerLoginRequest("admin_master", "admin9981")
        managerApi.login(loginData).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // 로그인 성공 처리
                    Log.d("API_SUCCESS", "응답 메시지: ${response.body()}")
                } else {
                    // 비밀번호 틀림 등 서버 에러 처리
                    Log.e("API_ERROR", "에러 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("API_FAIL", "서버 통신 실패: ${t.message}")
            }
        })

        // --- 2. 상품 전체 조회 예시 ---
        managerApi.getAllProducts().enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_SUCCESS", "상품 개수: ${productList?.size}")
                    // 여기서 RecyclerView의 Adapter에 데이터를 넘겨주면 됩니다.
                }
            }
            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                Log.e("API_FAIL", "상품 조회 실패: ${t.message}")
            }
        })
    }
}

```

### 💡 주의사항 (Checklist)

1. **Base URL 뒤에 반드시 슬래시(`/`)** 가 포함되어야 합니다. (예: `...onrender.com/`)
2. Render 무료 서버를 사용하는 경우, **첫 요청 시 서버가 깨어나는 데 최대 1분 정도 소요**될 수 있습니다. (통신 타임아웃 발생 시 다시 시도해 주세요)

---

이 내용을 그대로 복사하셔서 깃허브 마크다운 파일에 넣으시면, 깔끔한 코드 블록과 함께 바로 읽기 좋은 형태가 될 거예요. 추가로 유저/가계부 관련 API 명세서도 필요하시면 언제든 말씀해 주세요!
