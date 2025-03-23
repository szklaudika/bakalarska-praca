package com.example.zapisnik;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FlightDao {
    @Insert
    void insert(Flight flight);

    @Query("SELECT * FROM flights")
    List<Flight> getAllFlights();

    // Only get flights that were added offline and not yet synced.
    @Query("SELECT * FROM flights WHERE is_synced = 0 AND added_offline = 1")
    List<Flight> getUnsyncedOfflineFlights();

    @Query("UPDATE flights SET is_synced = 1 WHERE id = :flightId")
    void markAsSynced(int flightId);

    @Delete
    void delete(Flight flight);

    // New method to check for an existing flight by unique fields.
    @Query("SELECT * FROM flights WHERE date = :date AND departure_place = :departurePlace AND arrival_place = :arrivalPlace AND pilot_name = :pilotName")
    List<Flight> getFlightByUnique(String date, String departurePlace, String arrivalPlace, String pilotName);
}
