package com.example.zapisnik;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // Add TextWatcher for expiry date input
        etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Automatically format the input as the user types
                String formattedText = formatDateInput(charSequence.toString());
                if (!formattedText.equals(charSequence.toString())) {
                    etExpiryDate.setText(formattedText);
                    etExpiryDate.setSelection(formattedText.length());  // Keep the cursor at the end
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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

        Log.d("DEBUG", "Saving Certificate: Name = " + certificateName + ", Expiry Date = " + expiryDate);

        Executors.newSingleThreadExecutor().execute(() -> {
            database.certificateDao().insertCertificate(newCertificate);

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Certificate Added!", Toast.LENGTH_SHORT).show();
                etCertificateName.setText("");
                etExpiryDate.setText("");

                if (isWiFiConnected()) {
                    sendCertificateToServer(newCertificate);
                }
            });
        });
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
    private String formatDateInput(String input) {
        // Remove any non-numeric characters
        input = input.replaceAll("[^0-9]", "");

        StringBuilder formattedDate = new StringBuilder();

        // Format the date as YYYY-MM-DD dynamically
        if (input.length() >= 1) formattedDate.append(input.substring(0, Math.min(4, input.length()))); // Year
        if (input.length() >= 5) formattedDate.append("-").append(input.substring(4, Math.min(6, input.length()))); // Month
        if (input.length() >= 7) formattedDate.append("-").append(input.substring(6, Math.min(8, input.length()))); // Day

        // Validate the month
        String month = formattedDate.length() >= 7 ? formattedDate.substring(5, 7) : "";
        if (!month.isEmpty() && Integer.parseInt(month) > 12) {
            formattedDate.replace(5, 7, "12");  // Set month to 12 if it's greater than 12
        }

        // Validate the day
        String day = formattedDate.length() >= 10 ? formattedDate.substring(8, 10) : "";
        if (!day.isEmpty()) {
            int intDay = Integer.parseInt(day);
            if (intDay > 31) {
                formattedDate.replace(8, 10, "31");  // Set day to 31 if it's greater than 31
            } else {
                int monthInt = Integer.parseInt(month);
                if (monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) {
                    // Set day to 30 if the month has 30 days
                    if (intDay > 30) {
                        formattedDate.replace(8, 10, "30");
                    }
                } else if (monthInt == 2) {
                    // Check for leap year February
                    int maxDay = (isLeapYear(Integer.parseInt(formattedDate.substring(0, 4)))) ? 29 : 28;
                    if (intDay > maxDay) {
                        formattedDate.replace(8, 10, String.valueOf(maxDay));
                    }
                }
            }
        }

        return formattedDate.toString();
    }

    // Helper method to check if a year is a leap year
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}