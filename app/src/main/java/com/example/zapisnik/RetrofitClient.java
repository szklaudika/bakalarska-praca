package com.example.zapisnik;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static volatile Retrofit retrofit = null;
    private static final String BASE_URL = "https://zapisnik-2b2a59a43d05.herokuapp.com/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .addInterceptor(chain -> {
                                okhttp3.Response response = chain.proceed(chain.request());
                                if (!response.isSuccessful()) {
                                    throw new IOException("Unexpected HTTP response: " + response);
                                }
                                return response;
                            })
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static CertificateApi getApi() {
        return getRetrofitInstance().create(CertificateApi.class);
    }
    public static FlightApi getFlightApi() {
        return getRetrofitInstance().create(FlightApi.class);
    }

    public static UserApi getUserApi() {
        return getRetrofitInstance().create(UserApi.class);
    }


}