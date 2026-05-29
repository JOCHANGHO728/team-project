package com.example.managementapp.model;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 회원가입(미사용 기능)
    @POST("register")
    Call<SignUpResponse> register(@Body SignUpRequest request);

    // 관리자 로그인
    @POST("/api/v1/managers/login")
    Call<ApiResponse<ManagerAuthResponse>> login(@Body LoginRequest request);

    // 상품 조회
    @GET("/api/v1/managers/products/search")
    Call<ApiResponse<List<ProductResponse>>> search(@Query("name") String keyword);

    // 상품 추가
    @POST("/api/v1/managers/products")
    Call<String> addProduct(@Body ProductCreateRequest request);

    // 상품 수정
    @PUT("/api/v1/managers/products/{pId}")
    Call<ApiResponse<Void>> updateProduct(
            @Path("pId") Long productId,
            @Body ProductUpdateRequest request
    );

    // 상품 삭제
    @DELETE("/api/v1/managers/products/{pId}")
    Call<String> deleteProduct(@Path("pId") Long productId);
}