package com.example.zapisnik;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificatesFragment extends Fragment {

    private EditText etCertificateName, etExpiryDate;
    private Button btnAddCertificate;
    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificates, container, false);

        etCertificateName = view.findViewById(R.id.et_certificate_name);
        etExpiryDate = view.findViewById(R.id.et_expiry_date);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        database = CertificateDatabase.getInstance(getActivity());

        btnAddCertificate.setOnClickListener(v -> addCertificate());

        return view;
    }

    private void addCertificate() {
        String certificateName = etCertificateName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (certificateName.isEmpty() || expiryDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Certificate newCertificate = new Certificate(certificateName, expiryDate);

        Executors.newSingleThreadExecutor().execute(() -> {
            database.certificateDao().insertCertificate(newCertificate);
        });

        Toast.makeText(getActivity(), "Certificate Added!", Toast.LENGTH_SHORT).show();

        etCertificateName.setText("");
        etExpiryDate.setText("");

        if (isWiFiConnected()) {
            sendCertificateToServer(newCertificate);
        }
    }

    private boolean isWiFiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void sendCertificateToServer(Certificate certificate) {
        RetrofitClient.getApi().addCertificate(certificate).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.certificateDao().markAsSynced(certificate.getId());
                    });
                    Toast.makeText(getActivity(), "Certificate synchronized", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Retrofit", "Failed response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Retrofit", "Error: " + t.getMessage());
            }
        });
    }
}
