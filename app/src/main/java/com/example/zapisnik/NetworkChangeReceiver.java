package com.example.zapisnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
            synchronizeData(context);
        }
    }

    private void synchronizeData(Context context) {
        FlightDatabase db = FlightDatabase.getInstance(context); // Use your FlightDatabase

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> unsyncedFlights = db.flightDao().getUnsyncedFlights();

            for (Flight flight : unsyncedFlights) {
                sendFlightToServer(flight, db);
            }
        });
    }

    private void sendFlightToServer(Flight flight, FlightDatabase db) {
        FlightApi api = RetrofitClient.getFlightApi(); // Get the correct API instance

        api.addFlight(flight).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // If the flight is successfully synced, mark it as synced in the database
                    Executors.newSingleThreadExecutor().execute(() -> db.flightDao().markAsSynced(flight.getId()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Retrofit", "Error: " + t.getMessage());
            }
        });
    }
}

