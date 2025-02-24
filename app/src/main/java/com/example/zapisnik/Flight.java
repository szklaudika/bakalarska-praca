package com.example.zapisnik;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flights")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String departureLocation;
    private String arrivalLocation;
    private String flightDate;
    private String flightTime;
    private String flightDuration;
    private String flightType;

    // Konštruktor
    public Flight(String departureLocation, String arrivalLocation, String flightDate,
                  String flightTime, String flightDuration, String flightType) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.flightDate = flightDate;
        this.flightTime = flightTime;
        this.flightDuration = flightDuration;
        this.flightType = flightType;
    }

    // Gettery a Settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepartureLocation() { return departureLocation; }
    public String getArrivalLocation() { return arrivalLocation; }
    public String getFlightDate() { return flightDate; }
    public String getFlightTime() { return flightTime; }
    public String getFlightDuration() { return flightDuration; }
    public String getFlightType() { return flightType; }
}
