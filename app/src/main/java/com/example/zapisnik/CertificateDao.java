package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CertificateDao {
    @Insert
    void insertCertificate(Certificate certificate);

    @Query("SELECT * FROM certificates")
    List<Certificate> getAllCertificates();
}
