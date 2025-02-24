package com.example.zapisnik;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CertificateApi {
    @POST("add_certificate.php")
    Call<Void> addCertificate(@Body Certificate certificate);
}
