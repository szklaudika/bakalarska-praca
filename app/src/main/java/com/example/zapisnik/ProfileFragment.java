package com.example.zapisnik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CertificateDatabase database;
    private ListView listViewCertificates;
    private ArrayAdapter<String> adapter;
    private List<String> certificateList = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        listViewCertificates = view.findViewById(R.id.list_view_certificates);

        database = CertificateDatabase.getInstance(getActivity());

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, certificateList);
        listViewCertificates.setAdapter(adapter);

        // Load certificates only from the local database on start
        loadCertificatesFromDatabase();

        // Set up item click listener for deleting certificates
        listViewCertificates.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCertificate = certificateList.get(position);

            // Confirm deletion before proceeding
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle("Delete Certificate")
                    .setMessage("Are you sure you want to delete " + selectedCertificate + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the certificate
                        deleteCertificate(selectedCertificate);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

    private void deleteCertificate(String certificateName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Find the certificate from the list using the name
            Certificate certificateToDelete = database.certificateDao().getCertificateByName(certificateName);

            if (certificateToDelete != null) {
                // Delete from the database
                database.certificateDao().deleteCertificate(certificateToDelete);

                // Update the certificate list and UI on the main thread
                getActivity().runOnUiThread(() -> {
                    // Remove the certificate from the displayed list
                    certificateList.remove(certificateName);
                    adapter.notifyDataSetChanged();  // Refresh the ListView

                    // Show a confirmation toast
                    Toast.makeText(getActivity(), "Certificate deleted: " + certificateName, Toast.LENGTH_SHORT).show();
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Certificate not found: " + certificateName, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if the device is connected to the internet when the app is resumed.
        if (isNetworkAvailable()) {
            // Sync any local unsynced certificates with the server
            syncLocalCertificatesToServer();

            // Optionally, you can fetch data from the server if you want to update the list
            loadCertificatesFromServer();  // Uncomment this line if you want to sync after internet is available
        }
    }

    // Check network availability (Wi-Fi or Mobile Data)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }
        return false;
    }

    // Load certificates from the database in a separate thread
    public void loadCertificatesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> certificates = database.certificateDao().getAllCertificates();
            List<String> tempList = new ArrayList<>();

            for (Certificate cert : certificates) {
                Log.d("DEBUG", "Loaded from DB: " + cert.getName() + ", Expiry Date: " + cert.getExpiryDate());
                tempList.add(cert.getName() + " - Expires: " + (cert.getExpiryDate() != null ? cert.getExpiryDate() : "NULL"));
            }

            getActivity().runOnUiThread(() -> {
                certificateList.clear();
                certificateList.addAll(tempList);
                adapter.notifyDataSetChanged();
            });
        });
    }


    private void loadCertificatesFromServer() {
        CertificateApi api = RetrofitClient.getApi();
        Call<List<Certificate>> call = api.getAllCertificates();

        call.enqueue(new Callback<List<Certificate>>() {
            @Override
            public void onResponse(Call<List<Certificate>> call, Response<List<Certificate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Certificate> serverCertificates = response.body();

                    Executors.newSingleThreadExecutor().execute(() -> {
                        for (Certificate cert : serverCertificates) {
                            Certificate existingCert = database.certificateDao().getCertificateById(cert.getId());

                            if (existingCert == null) {
                                // Insert only if the certificate does not exist
                                database.certificateDao().insertCertificate(cert);
                            } else {
                                // Update if the certificate already exists
                                database.certificateDao().updateCertificate(cert);
                            }
                        }

                        getActivity().runOnUiThread(() -> {
                            // Refresh UI after inserting data
                            loadCertificatesFromDatabase();
                        });
                    });
                } else {
                    Log.e("DEBUG", "Server returned failure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Certificate>> call, Throwable t) {
                Log.e("DEBUG", "Failed to fetch from server: " + t.getMessage());
            }
        });
    }





    // Send all local certificates to the server if internet is available
    public void syncLocalCertificatesToServer() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> unsyncedCertificates = database.certificateDao().getUnsyncedCertificates();

            if (!unsyncedCertificates.isEmpty()) {
                CertificateApi api = RetrofitClient.getApi();

                for (Certificate cert : unsyncedCertificates) {
                    // Check if certificate exists on the server by matching ID or other unique identifier
                    api.getCertificateById(cert.getId()).enqueue(new Callback<Certificate>() {
                        @Override
                        public void onResponse(Call<Certificate> call, Response<Certificate> response) {
                            if (response.isSuccessful()) {
                                // If certificate exists, log the response and skip syncing
                                Log.d("Sync", "Certificate exists on server: " + response.body().toString());
                            } else {
                                // Log the response code and message when the certificate is not found or there's an issue
                                Log.e("Sync", "Failed to find certificate with ID " + cert.getId() + ". Status Code: " + response.code() + ", Message: " + response.message());

                                if (response.code() == 404) {
                                    // If 404 error is returned, the certificate doesn't exist, proceed to add it
                                    api.addCertificate(cert).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                // Mark the certificate as synced in local database
                                                Executors.newSingleThreadExecutor().execute(() -> {
                                                    database.certificateDao().markAsSynced(cert.getId());
                                                    getActivity().runOnUiThread(() -> {
                                                        Toast.makeText(getActivity(), "Certificate synced: " + cert.getName(), Toast.LENGTH_SHORT).show();
                                                    });
                                                });
                                            } else {
                                                Log.e("Sync", "Error syncing certificate: " + cert.getName());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.e("Sync", "Error syncing certificate: " + cert.getName(), t);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Certificate> call, Throwable t) {
                            Log.e("Sync", "Error checking if certificate exists on server", t);
                        }
                    });
                }
            }
        });
    }

    // Parse the expiry date string and return a Date object
    public Date parseExpiryDate(String expiryDate) {
        // Correct format to match the format in the database: yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Log.d("parseExpiryDate", "Parsing date: " + expiryDate);
            return dateFormat.parse(expiryDate);
        } catch (ParseException e) {
            Log.e("parseExpiryDate", "Error parsing expiry date: " + expiryDate, e);
            return null; // Return null if parsing fails
        }
    }
}
