package com.example.zapisnik;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.86/zapisnik_db/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create a logging interceptor to log HTTP request and response details
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response bodies

            // Create an OkHttpClient and add the interceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Build Retrofit with the OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)  // Use the OkHttpClient with logging
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static CertificateApi getApi() {
        return getRetrofitInstance().create(CertificateApi.class);
    }
}
