package com.example.zapisnik;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "certificates")
public class Certificate {

    @PrimaryKey(autoGenerate = true)  // Teraz sa ID generuje automaticky
    private int id;

    private String name;

    @SerializedName("expiry_date")
    @ColumnInfo(name = "expiry_date")
    private String expiryDate;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced; // Sledovanie stavu synchronizácie

    // Nové pole pre chybovú správu (napr. z PHP skriptu, keď certifikát nie je nájdený)
    @SerializedName("error")
    private String error;

    // Konštruktor pre lokálne ukladanie
    public Certificate(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.isSynced = false;
    }

    // Gettery a settery
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
        isSynced = synced;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
