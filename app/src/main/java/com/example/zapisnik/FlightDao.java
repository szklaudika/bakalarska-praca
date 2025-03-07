package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FlightDao {
    @Insert
    void insert(Flight flight);

    @Query("SELECT * FROM flights")
    List<Flight> getAllFlights();

    @Query("SELECT * FROM flights WHERE is_synced = 0")
    List<Flight> getUnsyncedFlights();

    @Query("UPDATE flights SET is_synced = 1 WHERE id = :flightId")
    void markAsSynced(int flightId);
}
