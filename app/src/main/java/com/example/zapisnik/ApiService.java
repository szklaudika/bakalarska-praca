package com.example.zapisnik;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.List;

public interface ApiService {
    @POST("/uploadCertificates")
    Call<Void> uploadCertificates(@Body List<Certificate> certificates);
}
