package com.example.managementapp.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("register")
    Call<SignUpResponse> register(@Body SignUpRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
}