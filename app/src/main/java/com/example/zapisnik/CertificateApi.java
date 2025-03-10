package com.example.zapisnik;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CertificateApi {
    @POST("add_certificate.php")
    Call<Void> addCertificate(@Body Certificate certificate);

    @GET("get_certificates.php")  // Nové API na získanie certifikátov zo servera
    Call<List<Certificate>> getAllCertificates();
}
