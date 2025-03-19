package com.example.zapisnik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException; // Používame java.text.ParseException
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CertificateDatabase database;
    private ListView listViewCertificates;
    private SectionedCertificateAdapter adapter;
    private List<ListItem> items = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private TextView tvTotalFlightTime;  // Nová premenna pre TextView s celkovým letovým časom

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        listViewCertificates = view.findViewById(R.id.list_view_certificates);

        // Inflatujeme header, ktorý obsahuje ImageView, tv_platforms a nový tv_total_flight_time
        View headerView = inflater.inflate(R.layout.list_header_profile, null);
        listViewCertificates.addHeaderView(headerView, null, false);

        // Získame referenciu na profilovú fotku a nastavíme listener pre výber obrázku
        profileImageView = headerView.findViewById(R.id.img_profile_pic);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Spustíme intent pre výber obrázku z galérie
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Získame referenciu na nový TextView, kde sa zobrazí celkový letový čas
        tvTotalFlightTime = headerView.findViewById(R.id.tv_total_flight_time);

        database = CertificateDatabase.getInstance(getActivity());

        // Načítame certifikáty z lokálnej databázy
        loadCertificatesFromDatabase();

        // Načítame a zobrazíme celkový letový čas
        loadTotalFlightTime();

        // Listener pre vymazávanie certifikátu po kliknutí (použijeme len položky, nie hlavičky)
        listViewCertificates.setOnItemClickListener((parent, view1, position, id) -> {
            // Vzhľadom na pridaný header upravíme index
            int adjustedPosition = position - listViewCertificates.getHeaderViewsCount();
            if (adjustedPosition >= 0 && adjustedPosition < items.size()) {
                ListItem selectedItem = items.get(adjustedPosition);
                if (selectedItem.getType() == ListItem.TYPE_ITEM) {
                    String selectedCertificate = selectedItem.getText();
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Delete Certificate")
                            .setMessage("Are you sure you want to delete " + selectedCertificate + "?")
                            .setPositiveButton("Yes", (dialog, which) -> deleteCertificate(selectedCertificate))
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });

        return view;
    }

    // Výsledok výberu obrázku z galérie
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    /**
     * Načítanie certifikátov zo všetkých platforiem.
     * Všetky certifikáty sa zoskupia podľa hodnoty getPlatform() a zobrazia sa so sekciami.
     */
    public void loadCertificatesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> certificates = database.certificateDao().getAllCertificates();
            List<ListItem> tempItems = new ArrayList<>();

            // Definované známe platformy
            List<String> knownPlatforms = Arrays.asList("Velká éra", "Vrtuľníky", "Ultralighty", "Vetrone");

            // Zoskupenie certifikátov podľa platformy
            Map<String, List<Certificate>> groupedByPlatform = new HashMap<>();
            for (Certificate cert : certificates) {
                String platform = cert.getPlatform();
                if (platform == null || platform.isEmpty() || !knownPlatforms.contains(platform)) {
                    platform = "Ostatné certifikáty";
                }
                if (!groupedByPlatform.containsKey(platform)) {
                    groupedByPlatform.put(platform, new ArrayList<>());
                }
                groupedByPlatform.get(platform).add(cert);
            }

            // Najprv pre známe platformy
            for (String platform : knownPlatforms) {
                List<Certificate> list = groupedByPlatform.get(platform);
                if (list != null && !list.isEmpty()) {
                    tempItems.add(new ListItem(ListItem.TYPE_HEADER, platform));
                    for (Certificate cert : list) {
                        String formattedExpiry = "N/A";
                        if (cert.getExpiryDate() != null && !cert.getExpiryDate().isEmpty()) {
                            try {
                                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                Date date = originalFormat.parse(cert.getExpiryDate());
                                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy\nMMM dd", Locale.US);
                                formattedExpiry = newFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        String detailPart = cert.getCertificateType() + "\n(" + cert.getSection() + ")";
                        tempItems.add(new ListItem(ListItem.TYPE_ITEM, formattedExpiry, detailPart));
                    }
                }
            }

            // Potom pre "Ostatné certifikáty"
            if (groupedByPlatform.containsKey("Ostatné certifikáty")) {
                List<Certificate> others = groupedByPlatform.get("Ostatné certifikáty");
                if (others != null && !others.isEmpty()) {
                    tempItems.add(new ListItem(ListItem.TYPE_HEADER, "Ostatné certifikáty"));
                    for (Certificate cert : others) {
                        String formattedExpiry = "N/A";
                        if (cert.getExpiryDate() != null && !cert.getExpiryDate().isEmpty()) {
                            try {
                                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                Date date = originalFormat.parse(cert.getExpiryDate());
                                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy\nMMM dd", Locale.US);
                                formattedExpiry = newFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        String detailPart = cert.getCertificateType() + "\n(" + cert.getSection() + ", " + cert.getPlatform() + ")";
                        tempItems.add(new ListItem(ListItem.TYPE_ITEM, formattedExpiry, detailPart));
                    }
                }
            }

            // Aktualizácia UI vo vlákně UI
            getActivity().runOnUiThread(() -> {
                items.clear();
                items.addAll(tempItems);
                adapter = new SectionedCertificateAdapter(getActivity(), items);
                listViewCertificates.setAdapter(adapter);
            });
        });
    }

    // Metóda na načítanie celkového letového času zo všetkých letov z FlightDatabase
    private void loadTotalFlightTime() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Načítame všetky lety z FlightDatabase
            List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();
            int totalMinutes = 0;
            for (Flight flight : flights) {
                totalMinutes += flight.getTotalFlightTime();
            }
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            String totalTimeStr = "Celkový letový čas: " + hours + " h " + minutes + " min";
            getActivity().runOnUiThread(() -> tvTotalFlightTime.setText(totalTimeStr));
        });
    }

    private void deleteCertificate(String certificateString) {
        // Predpokladáme formát: "CertificateType (section) - Expires: date"
        String[] parts = certificateString.split(" - Expires:");
        String certificateName = parts[0].trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            Certificate certificateToDelete = database.certificateDao().getCertificateByName(certificateName);
            if (certificateToDelete != null) {
                CertificateApi api = RetrofitClient.getApi();
                api.deleteCertificate(certificateToDelete.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                database.certificateDao().deleteCertificate(certificateToDelete);
                                getActivity().runOnUiThread(() -> {
                                    loadCertificatesFromDatabase();
                                    Toast.makeText(getActivity(), "Certificate deleted: " + certificateName, Toast.LENGTH_SHORT).show();
                                });
                            });
                        } else {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Server deletion failed for certificate: " + certificateName, Toast.LENGTH_SHORT).show());
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error deleting certificate from server: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Certificate not found: " + certificateName, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            syncLocalCertificatesToServer();
            loadCertificatesFromServer();
            // Aktualizujeme aj celkový letový čas
            loadTotalFlightTime();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }
        return false;
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
                                database.certificateDao().insertCertificate(cert);
                            } else {
                                database.certificateDao().updateCertificate(cert);
                            }
                        }
                        getActivity().runOnUiThread(() -> loadCertificatesFromDatabase());
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

    public void syncLocalCertificatesToServer() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> unsyncedCertificates = database.certificateDao().getUnsyncedCertificates();
            if (!unsyncedCertificates.isEmpty()) {
                CertificateApi api = RetrofitClient.getApi();
                for (Certificate cert : unsyncedCertificates) {
                    api.getCertificateById(cert.getId()).enqueue(new Callback<Certificate>() {
                        @Override
                        public void onResponse(Call<Certificate> call, Response<Certificate> response) {
                            if (response.isSuccessful()) {
                                Certificate serverCert = response.body();
                                if (serverCert == null || (serverCert.getError() != null && serverCert.getError().equals("Certificate not found"))) {
                                    addCertificateToServer(cert, api);
                                } else {
                                    Log.d("Sync", "Certificate exists on server: " + serverCert.toString());
                                }
                            } else {
                                addCertificateToServer(cert, api);
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

    private void addCertificateToServer(Certificate cert, CertificateApi api) {
        api.addCertificate(cert).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.certificateDao().markAsSynced(cert.getId());
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Certificate synced: " + cert.getCertificateType(), Toast.LENGTH_SHORT).show());
                    });
                } else {
                    Log.e("Sync", "Error syncing certificate: " + cert.getCertificateType());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Sync", "Error syncing certificate: " + cert.getCertificateType(), t);
            }
        });
    }

    // Metóda na parsovanie dátumu expirácie, ak je potrebná inde
    public Date parseExpiryDate(String expiryDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Log.d("parseExpiryDate", "Parsing date: " + expiryDate);
            return dateFormat.parse(expiryDate);
        } catch (ParseException e) {
            Log.e("parseExpiryDate", "Error parsing expiry date: " + expiryDate, e);
            return null;
        }
    }
}
