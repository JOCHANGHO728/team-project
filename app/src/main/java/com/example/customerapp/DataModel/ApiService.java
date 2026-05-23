package com.example.customerapp.DataModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    // 회원가입: POST /api/v1/users/signup
    @POST("api/v1/users/signup")
    Call<ApiResponse<String>> signup(@Body SignUpRequest request);

    // 로그인: POST /api/v1/users/login
    @POST("api/v1/users/login")
    Call<ApiResponse<UserAuthResponse>> login(@Body LoginRequest request);

    // 상품 검색 (카테고리별): GET /api/v1/products/search?keyword=과자
    @GET("api/v1/products/search")
    Call<ApiResponse<List<Product>>> searchProducts(@Query("keyword") String keyword);

    // 주문 생성: POST /api/v1/orders
    @POST("api/v1/orders")
    Call<ApiResponse<Void>> createOrder(
            @Header("Authorization") String authorization,
            @Body OrderRequest request
    );

    // 바코드로 단건 상품 조회 (누락되었던 부분 추가!)
    @GET("api/v1/products/barcode")
    Call<ApiResponse<Product>> searchByBarcode(@Query("bKey") String bKey);

    // 전체 구매 내역 조회: GET /api/v1/purchase-history/{uId}
    @GET("api/v1/purchase-history/{uId}")
    Call<ApiResponse<List<PurchaseHistoryItem>>> getPurchaseHistory(
            @Header("Authorization") String authorization,
            @Path("uId") String uId
    );

    // 기간별 구매 내역 조회: GET /api/v1/purchase-history/{uId}/range
    @GET("api/v1/purchase-history/{uId}/range")
    Call<ApiResponse<List<PurchaseHistoryItem>>> getPurchaseHistoryByRange(
            @Header("Authorization") String authorization,
            @Path("uId") String uId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
}
