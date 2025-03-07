package com.example.zapisnik;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FlightApi {
    @POST("add_flight.php")
    Call<Void> addFlight(@Body Flight flight);

    @GET("get_flights.php")
    Call<List<Flight>> getAllFlights();
}
