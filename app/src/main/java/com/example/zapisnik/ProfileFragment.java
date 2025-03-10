package com.example.zapisnik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
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

        // Načítanie certifikátov z lokálnej databázy + servera
        loadCertificatesFromDatabase();
        loadCertificatesFromServer();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if the device is connected to the internet when the app is resumed.
        if (isNetworkAvailable()) {
            // Sync any local unsynced certificates with the server
            syncLocalCertificatesToServer();
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

    // Opravené: Načítanie dát na inom vlákne
    public void loadCertificatesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> certificates = database.certificateDao().getAllCertificates();
            List<String> tempList = new ArrayList<>();

            for (Certificate cert : certificates) {
                tempList.add(cert.getName() + " - Expires: " + cert.getExpiryDate());
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

                    for (Certificate cert : serverCertificates) {
                        String certInfo = cert.getName() + " - Expires: " + cert.getExpiryDate();
                        if (!certificateList.contains(certInfo)) { // Zabráni duplicite
                            certificateList.add(certInfo);
                        }
                    }

                    adapter.notifyDataSetChanged(); // Aktualizácia UI
                }
            }

            @Override
            public void onFailure(Call<List<Certificate>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load certificates from server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    api.addCertificate(cert).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Mark as synced in local database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    database.certificateDao().markAsSynced(cert.getId());

                                    // Show toast on the main thread
                                    getActivity().runOnUiThread(() ->
                                            Toast.makeText(getActivity(), "Certificate synced: " + cert.getName(), Toast.LENGTH_SHORT).show()
                                    );
                                });
                            } else {
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "Error syncing certificate: " + cert.getName(), Toast.LENGTH_SHORT).show()
                                );
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Failed to sync certificate: " + cert.getName() + ", " + t.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                }
            }
        });
    }
}

