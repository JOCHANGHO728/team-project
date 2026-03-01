팀원분이 **Java(자바)** 환경에서 안드로이드 스튜디오를 사용하시는군요!

코틀린 버전을 자바 버전으로 완벽하게 번역하여, 깃허브나 노션에 바로 복사해서 붙여넣기 좋게 마크다운 형식으로 다시 정리해 드립니다.

---

# 📱 Android (Java) 관리자 API 연동 가이드

이 문서는 스마트 쇼핑 카트 앱의 **관리자 기능(로그인, 매장 상품 관리)**을 서버와 연동하기 위한 안드로이드(Java) 구현 가이드입니다. `Retrofit2` 라이브러리를 기준으로 작성되었습니다.

## 1. 초기 세팅 (Setup)

### 1-1. 라이브러리 추가 (`build.gradle (Module :app)`)

```groovy
dependencies {
    // Retrofit2 & Gson Converter
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}

```

### 1-2. 인터넷 권한 허용 (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.INTERNET" />

```

---

## 2. 데이터 클래스 (DTO)

서버와 통신할 때 주고받을 JSON 데이터 객체입니다. (Lombok이 없다면 Getter/Setter를 추가해서 사용하세요.)

**ManagerLoginRequest.java (로그인 요청)**

```java
import com.google.gson.annotations.SerializedName;

public class ManagerLoginRequest {
    @SerializedName("managerId")
    private String managerId;
    
    @SerializedName("mPassword")
    private String mPassword;

    public ManagerLoginRequest(String managerId, String mPassword) {
        this.managerId = managerId;
        this.mPassword = mPassword;
    }
}

```

**ProductResponse.java (상품 조회 응답)**

```java
import com.google.gson.annotations.SerializedName;

public class ProductResponse {
    @SerializedName("pid")
    private Long pId;
    
    @SerializedName("pname")
    private String pName;
    
    @SerializedName("pprice")
    private int pPrice;
    
    @SerializedName("pquantity")
    private int pQuantity;
    
    @SerializedName("bkey")
    private String bKey;
    
    @SerializedName("category")
    private String category;
    
    // 필요 시 Getter를 생성하세요. (예: getPName(), getPPrice() 등)
}

```

**ProductCreateRequest.java (상품 등록 요청)**

```java
import com.google.gson.annotations.SerializedName;

public class ProductCreateRequest {
    @SerializedName("pName")
    private String pName;
    @SerializedName("pPrice")
    private int pPrice;
    @SerializedName("pQuantity")
    private int pQuantity;
    @SerializedName("bKey")
    private String bKey;
    @SerializedName("category")
    private String category;

    public ProductCreateRequest(String pName, int pPrice, int pQuantity, String bKey, String category) {
        this.pName = pName;
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
        this.bKey = bKey;
        this.category = category;
    }
}

```

**ProductUpdateRequest.java (상품 수정 요청)**

```java
import com.google.gson.annotations.SerializedName;

public class ProductUpdateRequest {
    @SerializedName("pPrice")
    private int pPrice;
    @SerializedName("pQuantity")
    private int pQuantity;

    public ProductUpdateRequest(int pPrice, int pQuantity) {
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
    }
}

```

---

## 3. API 인터페이스 명세서 (API Interface)

서버의 어떤 URL로 어떤 데이터를 보낼지 정의하는 인터페이스입니다.

**ManagerApi.java**

```java
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ManagerApi {
    // 1. 관리자 로그인
    @POST("/api/v1/managers/login")
    Call<String> login(@Body ManagerLoginRequest request);

    // 2. 상품 전체 조회
    @GET("/api/v1/managers/products")
    Call<List<ProductResponse>> getAllProducts();

    // 3. 상품 추가
    @POST("/api/v1/managers/products")
    Call<String> addProduct(@Body ProductCreateRequest request);

    // 4. 상품 수정 (pId: 수정할 상품의 ID)
    @PUT("/api/v1/managers/products/{pId}")
    Call<String> updateProduct(
        @Path("pId") Long productId,
        @Body ProductUpdateRequest request
    );

    // 5. 상품 삭제 (pId: 삭제할 상품의 ID)
    @DELETE("/api/v1/managers/products/{pId}")
    Call<String> deleteProduct(@Path("pId") Long productId);
}

```

---

## 4. 실제 API 호출 예시 (Activity)

화면(UI)에서 Retrofit을 이용해 서버에 요청을 보내는 자바 코드 예제입니다.

**ManagerActivity.java**

```java
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManagerActivity extends AppCompatActivity {

    // ⚠️ TODO: [본인의_Render_주소] 부분을 실제 배포된 서버 URL로 변경하세요!
    private static final String BASE_URL = "https://[본인의_Render_주소].onrender.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retrofit 객체 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ManagerApi managerApi = retrofit.create(ManagerApi.class);

        // ==========================================
        // 1. 관리자 로그인 요청 예시
        // ==========================================
        ManagerLoginRequest loginData = new ManagerLoginRequest("admin_master", "admin9981");
        
        managerApi.login(loginData).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("API_SUCCESS", "응답 메시지: " + response.body());
                } else {
                    Log.e("API_ERROR", "에러 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_FAIL", "서버 통신 실패: " + t.getMessage());
            }
        });

        // ==========================================
        // 2. 상품 전체 조회 예시
        // ==========================================
        managerApi.getAllProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> productList = response.body();
                    Log.d("API_SUCCESS", "상품 개수: " + productList.size());
                    // TODO: RecyclerView Adapter에 productList 전달
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("API_FAIL", "상품 조회 실패: " + t.getMessage());
            }
        });
    }
}

```

### 💡 주의사항 (Checklist)

1. **Base URL 뒤에 반드시 슬래시(`/`)** 가 포함되어야 합니다. (예: `...onrender.com/`)
2. Render 무료 서버를 사용하는 경우, **첫 요청 시 서버가 깨어나는 데 최대 1분 정도 소요**될 수 있습니다. (통신 타임아웃 발생 시 다시 시도해 주세요)

---

### 2. 안드로이드 팀원 전달용 코드 (Java) ( 제품 검색하기 )

팀원분은 Retrofit 인터페이스에 검색용 API를 하나 더 추가하고 호출만 하면 됩니다.

**① `ManagerApi.java` (API 인터페이스 추가)**
URL 뒤에 `?name=키워드` 형태로 데이터를 붙여서 보내야 하므로 `@Query` 어노테이션을 사용합니다.

```java
    // ... (기존 코드들) ...

    // 6. 상품 이름 검색 API
    @GET("/api/v1/managers/products/search")
    Call<List<ProductResponse>> searchProducts(@Query("name") String keyword);
}

```

**② `ManagerActivity.java` (검색 호출 예시)**
앱에서 검색창(EditText)에 글자를 입력하고 돋보기 버튼을 눌렀을 때 실행될 코드입니다.

```java
        // ==========================================
        // 6. 상품 이름 검색 실행 예시 (예: "새우" 검색)
        // ==========================================
        String searchKeyword = "새우"; 
        
        managerApi.searchProducts(searchKeyword).enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> searchResult = response.body();
                    Log.d("API_SUCCESS", "검색된 상품 개수: " + searchResult.size());
                    
                    // 검색된 상품들의 이름 출력해보기
                    for (ProductResponse product : searchResult) {
                        Log.d("API_SUCCESS", "검색된 상품: " + product.getPName());
                    }
                    
                    // TODO: 이 검색 결과를 화면(RecyclerView)에 업데이트 해주세요!
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("API_FAIL", "상품 검색 통신 실패: " + t.getMessage());
            }
        });

```

---


