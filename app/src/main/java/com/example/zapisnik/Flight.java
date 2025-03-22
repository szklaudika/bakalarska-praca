package com.example.zapisnik;

import com.google.gson.annotations.SerializedName;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "flights")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("departurePlace")
    private String departurePlace;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("arrivalPlace")
    private String arrivalPlace;

    @SerializedName("arrivalTime")
    private String arrivalTime;

    @SerializedName("aircraftModel")
    private String aircraftModel;

    @SerializedName("registration")
    private String registration;

    @SerializedName("singlePilotTime")
    private int singlePilotTime; // Required

    @SerializedName("multiPilotTime")
    private Integer multiPilotTime; // Optional

    @SerializedName("totalFlightTime")
    private int totalFlightTime; // Required

    @SerializedName("pilotName")
    private String pilotName;

    @SerializedName("singlePilot")
    private boolean singlePilot; // Required (assumed)

    @SerializedName("landingsDay")
    private Integer landingsDay; // Optional

    @SerializedName("landingsNight")
    private Integer landingsNight; // Optional

    @SerializedName("nightTime")
    private Integer nightTime; // Optional

    @SerializedName("ifrTime")
    private Integer ifrTime; // Optional

    @SerializedName("picTime")
    private Integer picTime; // Optional

    @SerializedName("copilotTime")
    private Integer copilotTime; // Optional

    @SerializedName("dualTime")
    private Integer dualTime; // Optional

    @SerializedName("instructorTime")
    private Integer instructorTime; // Optional

    @SerializedName("fstdDate")
    private String fstdDate; // Optional

    @SerializedName("fstdType")
    private String fstdType; // Optional

    @SerializedName("fstdTotalTime")
    private Integer fstdTotalTime; // Optional

    @SerializedName("remarks")
    private String remarks;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced;

    public Flight(String date, String departurePlace, String departureTime, String arrivalPlace, String arrivalTime,
                  String aircraftModel, String registration, int singlePilotTime, Integer multiPilotTime, int totalFlightTime,
                  String pilotName, boolean singlePilot, Integer landingsDay, Integer landingsNight, Integer nightTime, Integer ifrTime,
                  Integer picTime, Integer copilotTime, Integer dualTime, Integer instructorTime, String fstdDate, String fstdType,
                  Integer fstdTotalTime, String remarks) {
        this.date = date;
        this.departurePlace = departurePlace;
        this.departureTime = departureTime;
        this.arrivalPlace = arrivalPlace;
        this.arrivalTime = arrivalTime;
        this.aircraftModel = aircraftModel;
        this.registration = registration;
        this.singlePilotTime = singlePilotTime;
        this.multiPilotTime = multiPilotTime;
        this.totalFlightTime = totalFlightTime;
        this.pilotName = pilotName;
        this.singlePilot = singlePilot;
        this.landingsDay = landingsDay;
        this.landingsNight = landingsNight;
        this.nightTime = nightTime;
        this.ifrTime = ifrTime;
        this.picTime = picTime;
        this.copilotTime = copilotTime;
        this.dualTime = dualTime;
        this.instructorTime = instructorTime;
        this.fstdDate = fstdDate;
        this.fstdType = fstdType;
        this.fstdTotalTime = fstdTotalTime;
        this.remarks = remarks;
        this.isSynced = false; // Default value when a flight is first created
    }

    // Getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDeparturePlace() { return departurePlace; }
    public void setDeparturePlace(String departurePlace) { this.departurePlace = departurePlace; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalPlace() { return arrivalPlace; }
    public void setArrivalPlace(String arrivalPlace) { this.arrivalPlace = arrivalPlace; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getAircraftModel() { return aircraftModel; }
    public void setAircraftModel(String aircraftModel) { this.aircraftModel = aircraftModel; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public int getSinglePilotTime() { return singlePilotTime; }
    public void setSinglePilotTime(int singlePilotTime) { this.singlePilotTime = singlePilotTime; }

    public Integer getMultiPilotTime() { return multiPilotTime; }
    public void setMultiPilotTime(Integer multiPilotTime) { this.multiPilotTime = multiPilotTime; }

    public int getTotalFlightTime() { return totalFlightTime; }
    public void setTotalFlightTime(int totalFlightTime) { this.totalFlightTime = totalFlightTime; }

    public String getPilotName() { return pilotName; }
    public void setPilotName(String pilotName) { this.pilotName = pilotName; }

    public boolean isSinglePilot() { return singlePilot; }
    public void setSinglePilot(boolean singlePilot) { this.singlePilot = singlePilot; }

    public Integer getLandingsDay() { return landingsDay; }
    public void setLandingsDay(Integer landingsDay) { this.landingsDay = landingsDay; }

    public Integer getLandingsNight() { return landingsNight; }
    public void setLandingsNight(Integer landingsNight) { this.landingsNight = landingsNight; }

    public Integer getNightTime() { return nightTime; }
    public void setNightTime(Integer nightTime) { this.nightTime = nightTime; }

    public Integer getIfrTime() { return ifrTime; }
    public void setIfrTime(Integer ifrTime) { this.ifrTime = ifrTime; }

    public Integer getPicTime() { return picTime; }
    public void setPicTime(Integer picTime) { this.picTime = picTime; }

    public Integer getCopilotTime() { return copilotTime; }
    public void setCopilotTime(Integer copilotTime) { this.copilotTime = copilotTime; }

    public Integer getDualTime() { return dualTime; }
    public void setDualTime(Integer dualTime) { this.dualTime = dualTime; }

    public Integer getInstructorTime() { return instructorTime; }
    public void setInstructorTime(Integer instructorTime) { this.instructorTime = instructorTime; }

    public String getFstdDate() { return fstdDate; }
    public void setFstdDate(String fstdDate) { this.fstdDate = fstdDate; }

    public String getFstdType() { return fstdType; }
    public void setFstdType(String fstdType) { this.fstdType = fstdType; }

    public Integer getFstdTotalTime() { return fstdTotalTime; }
    public void setFstdTotalTime(Integer fstdTotalTime) { this.fstdTotalTime = fstdTotalTime; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }
}
