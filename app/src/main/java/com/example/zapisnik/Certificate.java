package com.example.zapisnik;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "certificates")
public class Certificate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "section")
    private String section;

    @ColumnInfo(name = "platform")
    private String platform;

    @SerializedName("certificate_type")
    @ColumnInfo(name = "certificate_type")
    private String certificateType;

    @SerializedName("expiry_date")
    @ColumnInfo(name = "expiry_date")
    private String expiryDate;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced;

    @SerializedName("error")
    private String error;

    // Add userId field with appropriate column info if needed.
    @ColumnInfo(name = "user_id")
    private int userId;

    // Add the addedOffline field.
    @ColumnInfo(name = "added_offline")
    private boolean addedOffline;

    // Constructor with required fields
    public Certificate(String section, String platform, String certificateType, String expiryDate, String note) {
        this.section = section;
        this.platform = platform;
        this.certificateType = certificateType;
        this.expiryDate = expiryDate;
        this.note = note;
        this.isSynced = false;
        this.error = null;
        // Optionally, you can set defaults for userId and addedOffline here if needed.
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    public String getPlatform() {
        return platform;
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getCertificateType() {
        return certificateType;
    }
    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
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
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public boolean isAddedOffline() {
        return addedOffline;
    }
    public void setAddedOffline(boolean addedOffline) {
        this.addedOffline = addedOffline;
    }
}
