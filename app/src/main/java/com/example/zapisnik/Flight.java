package com.example.zapisnik;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flights")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;
    private String departurePlace;
    private String departureTime;
    private String arrivalPlace;
    private String arrivalTime;
    private String aircraftModel;
    private String registration;
    private int singlePilotTime;
    private int multiPilotTime;
    private int totalFlightTime;
    private String pilotName;
    private int landings;
    private int nightTime;
    private int ifrTime;
    private int picTime;
    private int copilotTime;
    private int dualTime;
    private int instructorTime;
    private String fstdDate;
    private String fstdType;
    private int fstdTotalTime;
    private String remarks;

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
    }

    // Getters and setters
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
}