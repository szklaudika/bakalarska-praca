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

    @ColumnInfo(name = "certificate_type")
    private String certificateType;

    @ColumnInfo(name = "acquired_date")
    private String acquiredDate;

    @SerializedName("expiry_date")
    @ColumnInfo(name = "expiry_date")
    private String expiryDate;

    @ColumnInfo(name = "days_remaining")
    private int daysRemaining;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced;

    @SerializedName("error")
    private String error;

    // Nový konštruktor so všetkými potrebnými poľami
    public Certificate(String section, String platform, String certificateType,
                       String acquiredDate, String expiryDate, int daysRemaining, String note) {
        this.section = section;
        this.platform = platform;
        this.certificateType = certificateType;
        this.acquiredDate = acquiredDate;
        this.expiryDate = expiryDate;
        this.daysRemaining = daysRemaining;
        this.note = note;
        this.isSynced = false;
        this.error = null;
    }

    // Gettery a settery
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
    public String getAcquiredDate() {
        return acquiredDate;
    }
    public void setAcquiredDate(String acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public int getDaysRemaining() {
        return daysRemaining;
    }
    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
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


}
