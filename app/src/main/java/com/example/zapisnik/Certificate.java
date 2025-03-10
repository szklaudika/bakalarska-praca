package com.example.zapisnik;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "certificates")
public class Certificate {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String expiryDate;
    private boolean synced; // Pridáme, aby sme vedeli, či je synchronizovaný

    public Certificate(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.synced = false; // Defaultne nie je synchronizovaný
    }

    // Gettery a Settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }
}