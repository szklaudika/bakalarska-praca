package com.example.zapisnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
            // Wifi pripojenie je aktívne, spusti synchronizáciu
            synchronizeData(context);
        }
    }

    private void synchronizeData(Context context) {
        // Získaj všetky certifikáty z lokálnej databázy
        CertificateDatabase db = CertificateDatabase.getInstance(context);
        List<Certificate> certificates = db.certificateDao().getAllCertificates();

        for (Certificate cert : certificates) {
            sendCertificateToServer(cert, context);
        }
    }

    private void sendCertificateToServer(Certificate certificate, Context context) {
        // Zavolaj API na poslanie certifikátu na server
        RetrofitClient.getApi().addCertificate(certificate).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Úspešné odoslanie certifikátu
                    Toast.makeText(context, "Certificate synchronized", Toast.LENGTH_SHORT).show();
                } else {
                    // Chyba pri odosielaní
                    Toast.makeText(context, "Failed to synchronize certificate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Chyba pri spojení
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
