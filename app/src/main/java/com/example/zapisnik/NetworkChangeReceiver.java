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

        CertificateDatabase db = CertificateDatabase.getInstance(context);


        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> unsyncedCertificates = db.certificateDao().getUnsyncedCertificates();

            for (Certificate cert : unsyncedCertificates) {
                sendCertificateToServer(cert, db);
            }
        });
    }


        private void sendCertificateToServer(Certificate certificate, CertificateDatabase db) {
            RetrofitClient.getApi().addCertificate(certificate).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {

                        Executors.newSingleThreadExecutor().execute(() -> db.certificateDao().markAsSynced(certificate.getId()));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {


                    Log.d("Retrofit", "Error: " + t.getMessage());
                }
            });
        }
    private void synchronizeFlights(Context context) {
        FlightDatabase flightDb = FlightDatabase.getInstance(context);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> unsyncedFlights = flightDb.flightDao().getUnsyncedFlights();
            for (Flight flight : unsyncedFlights) {
                sendFlightToServer(flight, flightDb);
            }
        });
    }

    private void sendFlightToServer(Flight flight, FlightDatabase db) {
        RetrofitClient.getFlightApi().addFlight(flight).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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