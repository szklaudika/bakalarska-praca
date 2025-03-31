package com.example.zapisnik;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CertificateApi {

    // Endpoint to add a certificate to the server
    @POST("add_certificate.php")
    Call<Void> addCertificate(@Body Certificate certificate);

    // Endpoint to get all certificates from the server
    @GET("get_certificates.php")
    Call<List<Certificate>> getAllCertificates(@Query("user_id") int userId);


    // Endpoint to check if certificate exists (by name and expiry date)
    @GET("check_certificate.php")
    Call<Certificate> getCertificateByNameAndExpiry(
            @Query("name") String name,
            @Query("expiry_date") String expiryDate
    );

    // Endpoint to delete a certificate from the server
    @POST("delete_certificate.php")
    Call<Void> deleteCertificate(@Query("id") int id);
}
