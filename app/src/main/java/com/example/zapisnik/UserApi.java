package com.example.zapisnik;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> loginUser(
            @Field("usernameOrEmail") String usernameOrEmail,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );
}
