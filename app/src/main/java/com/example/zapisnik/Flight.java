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
    @ColumnInfo(name = "date")
    private String date;

    @SerializedName("departurePlace")
    @ColumnInfo(name = "departure_place")
    private String departurePlace;

    @SerializedName("departureTime")
    @ColumnInfo(name = "departure_time")
    private String departureTime;

    @SerializedName("arrivalPlace")
    @ColumnInfo(name = "arrival_place")
    private String arrivalPlace;

    @SerializedName("arrivalTime")
    @ColumnInfo(name = "arrival_time")
    private String arrivalTime;

    @SerializedName("aircraftModel")
    @ColumnInfo(name = "aircraft_model")
    private String aircraftModel;

    @SerializedName("registration")
    @ColumnInfo(name = "registration")
    private String registration;

    @SerializedName("singlePilotTime")
    @ColumnInfo(name = "singlePilotTime")
    private int singlePilotTime; // If your DB column is named "singlePilotTime", otherwise change to snake_case

    @SerializedName("multiPilotTime")
    @ColumnInfo(name = "multiPilotTime")
    private Integer multiPilotTime;

    @SerializedName("totalFlightTime")
    @ColumnInfo(name = "totalFlightTime")
    private int totalFlightTime;

    @SerializedName("pilotName")
    @ColumnInfo(name = "pilot_name")
    private String pilotName;

    @SerializedName("singlePilot")
    @ColumnInfo(name = "singlePilot")
    private boolean singlePilot;

    @SerializedName("landingsDay")
    @ColumnInfo(name = "landings_day")
    private Integer landingsDay;

    @SerializedName("landingsNight")
    @ColumnInfo(name = "landings_night")
    private Integer landingsNight;

    @SerializedName("nightTime")
    @ColumnInfo(name = "nightTime")
    private Integer nightTime;

    @SerializedName("ifrTime")
    @ColumnInfo(name = "ifr_time")
    private Integer ifrTime;

    @SerializedName("picTime")
    @ColumnInfo(name = "pic_time")
    private Integer picTime;

    @SerializedName("copilotTime")
    @ColumnInfo(name = "copilot_time")
    private Integer copilotTime;

    @SerializedName("dualTime")
    @ColumnInfo(name = "dual_time")
    private Integer dualTime;

    @SerializedName("instructorTime")
    @ColumnInfo(name = "instructor_time")
    private Integer instructorTime;

    @SerializedName("fstdDate")
    @ColumnInfo(name = "fstd_date")
    private String fstdDate;

    @SerializedName("fstdType")
    @ColumnInfo(name = "fstd_type")
    private String fstdType;

    @SerializedName("fstdTotalTime")
    @ColumnInfo(name = "fstd_total_time")
    private Integer fstdTotalTime;

    @SerializedName("remarks")
    @ColumnInfo(name = "remarks")
    private String remarks;

    @SerializedName("userId")
    @ColumnInfo(name = "user_id")
    private int userId;

    @SerializedName("addedOffline")
    @ColumnInfo(name = "added_offline")
    private boolean addedOffline;

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

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean isAddedOffline() { return addedOffline; }
    public void setAddedOffline(boolean addedOffline) { this.addedOffline = addedOffline; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }
}
