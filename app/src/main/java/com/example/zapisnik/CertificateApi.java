package com.example.zapisnik;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;  // Required to support dynamic path parameters
import retrofit2.http.Query;

public interface CertificateApi {

    // Endpoint to add a certificate to the server
    @POST("add_certificate.php")
    Call<Void> addCertificate(@Body Certificate certificate);

    // Endpoint to get all certificates from the server
    @GET("get_certificates.php")
    Call<List<Certificate>> getAllCertificates();

    // Endpoint to get a certificate by ID (using the correct GET request with dynamic parameter)
    @GET("get_certificates_by_id.php")
    Call<Certificate> getCertificateById(@Query("id") int id);
}
