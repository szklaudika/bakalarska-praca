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
    private int singlePilotTime;

    @SerializedName("multiPilotTime")
    private int multiPilotTime;

    @SerializedName("totalFlightTime")
    private int totalFlightTime;

    @SerializedName("pilotName")
    private String pilotName;

    @SerializedName("landings")
    private int landings;

    @SerializedName("nightTime")
    private int nightTime;

    @SerializedName("ifrTime")
    private int ifrTime;

    @SerializedName("picTime")
    private int picTime;

    @SerializedName("copilotTime")
    private int copilotTime;

    @SerializedName("dualTime")
    private int dualTime;

    @SerializedName("instructorTime")
    private int instructorTime;

    @SerializedName("fstdDate")
    private String fstdDate;

    @SerializedName("fstdType")
    private String fstdType;

    @SerializedName("fstdTotalTime")
    private int fstdTotalTime;

    @SerializedName("remarks")
    private String remarks;

    @ColumnInfo(name = "is_synced") // Mapping the column to the field
    private boolean isSynced;

    // Constructor
    public Flight(String date, String departurePlace, String departureTime, String arrivalPlace, String arrivalTime,
                  String aircraftModel, String registration, int singlePilotTime, int multiPilotTime, int totalFlightTime,
                  String pilotName, int landings, int nightTime, int ifrTime, int picTime, int copilotTime,
                  int dualTime, int instructorTime, String fstdDate, String fstdType, int fstdTotalTime, String remarks) {
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
        this.landings = landings;
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
        this.isSynced = false; // Default value is false when a flight is first created
    }

    // Getters and setters for all other fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDate() { return date; }
    public String getDeparturePlace() { return departurePlace; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalPlace() { return arrivalPlace; }
    public String getArrivalTime() { return arrivalTime; }
    public String getAircraftModel() { return aircraftModel; }
    public String getRegistration() { return registration; }
    public int getSinglePilotTime() { return singlePilotTime; }
    public int getMultiPilotTime() { return multiPilotTime; }
    public int getTotalFlightTime() { return totalFlightTime; }
    public String getPilotName() { return pilotName; }
    public int getLandings() { return landings; }
    public int getNightTime() { return nightTime; }
    public int getIfrTime() { return ifrTime; }
    public int getPicTime() { return picTime; }
    public int getCopilotTime() { return copilotTime; }
    public int getDualTime() { return dualTime; }
    public int getInstructorTime() { return instructorTime; }
    public String getFstdDate() { return fstdDate; }
    public String getFstdType() { return fstdType; }
    public int getFstdTotalTime() { return fstdTotalTime; }
    public String getRemarks() { return remarks; }

    // Getter and setter for isSynced (followed the JavaBean naming convention)
    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}

