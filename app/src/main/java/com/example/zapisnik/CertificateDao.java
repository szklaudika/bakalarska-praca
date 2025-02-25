package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CertificateDao {
    @Insert
    void insertCertificate(Certificate certificate);

    @Query("SELECT * FROM certificates")
    List<Certificate> getAllCertificates();

    @Query("SELECT * FROM certificates WHERE synced = 0")
    List<Certificate> getUnsyncedCertificates();

    @Query("UPDATE certificates SET synced = 1 WHERE id = :id")
    void markAsSynced(int id);
}
