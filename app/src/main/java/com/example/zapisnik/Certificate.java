package com.example.zapisnik;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "certificates")
public class Certificate {

    @PrimaryKey(autoGenerate = false)
    private int id;

    private String name;

    @SerializedName("expiry_date") // Ensures correct mapping from JSON to Java
    @ColumnInfo(name = "expiry_date") // Ensures correct mapping in the Room database
    private String expiryDate;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced; // Tracks if the certificate is synced with the server

    // Constructor
    public Certificate(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.isSynced = false; // Default: not synced
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        this.isSynced = synced;
    }
}
