package com.example.zapisnik;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "certificates")
public class Certificate {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String expiryDate;

    public Certificate(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public String getExpiryDate() { return expiryDate; }
}
