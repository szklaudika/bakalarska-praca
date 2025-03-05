package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FlightDao {

    @Insert
    void insertFlight(Flight flight);

    @Query("SELECT * FROM flights ORDER BY date DESC")
    List<Flight> getAllFlights();
}
