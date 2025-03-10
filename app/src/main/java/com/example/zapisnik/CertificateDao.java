package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CertificateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Fix: Replace existing records instead of failing
    void insertCertificate(Certificate certificate);

    @Update
    void updateCertificate(Certificate certificate);

    @Delete
    void deleteCertificate(Certificate certificate);

    @Query("SELECT * FROM certificates")
    List<Certificate> getAllCertificates();

    @Query("SELECT * FROM certificates WHERE is_synced = 0")
    List<Certificate> getUnsyncedCertificates();

    @Query("SELECT * FROM certificates WHERE is_synced = 1")
    List<Certificate> getSyncedCertificates();

    @Query("UPDATE certificates SET is_synced = 1 WHERE id = :id")
    void markAsSynced(int id);

    @Query("SELECT * FROM certificates WHERE name = :certificateName LIMIT 1")
    Certificate getCertificateByName(String certificateName);

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Fix: Bulk insert with conflict handling
    void insertCertificates(List<Certificate> certificates);

    @Query("SELECT * FROM certificates WHERE id = :id LIMIT 1") // Fix: Define the missing query
    Certificate getCertificateById(int id);
}
