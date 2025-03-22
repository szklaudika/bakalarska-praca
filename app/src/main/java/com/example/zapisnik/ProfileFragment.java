package com.example.zapisnik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private CertificateDatabase database;
    private ListView listViewCertificates;
    private SectionedCertificateAdapter adapter;
    private List<ListItem> items = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;

    // Aggregated flight data TextViews
    private TextView tvTotalFlightTime;
    private TextView tvMultiPilotTime;
    private TextView tvLandings;
    private TextView tvOperationTime;
    private TextView tvPilotFunctionTime;
    private TextView tvFstdSummary;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        listViewCertificates = view.findViewById(R.id.list_view_certificates);

        // Inflate header which contains the profile image and flight data TextViews
        View headerView = inflater.inflate(R.layout.list_header_profile, null);
        listViewCertificates.addHeaderView(headerView, null, false);

        // Set up profile image and listener for selecting an image from the gallery
        profileImageView = headerView.findViewById(R.id.img_profile_pic);
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Find TextViews for aggregated flight data (using IDs defined in your XML)
        tvTotalFlightTime = headerView.findViewById(R.id.tv_total_flight_time);
        tvMultiPilotTime = headerView.findViewById(R.id.tv_multi_pilot_time);
        tvLandings = headerView.findViewById(R.id.tv_landings);
        tvOperationTime = headerView.findViewById(R.id.tv_operation_time);
        tvPilotFunctionTime = headerView.findViewById(R.id.tv_pilot_function_time);
        tvFstdSummary = headerView.findViewById(R.id.tv_fstd_summary);

        // Initialize local certificate database
        database = CertificateDatabase.getInstance(getActivity());

        // Load certificates from local database and from the server
        loadCertificatesFromDatabase();
        loadCertificatesFromServer();

        // Calculate flight aggregates from local flight data
        calculateFlightAggregatesFromLocal();

        // Listener for deleting a certificate when a list item (not header) is clicked
        listViewCertificates.setOnItemClickListener((parent, view1, position, id) -> {
            try {
                int adjustedPosition = position - listViewCertificates.getHeaderViewsCount();
                if (adjustedPosition >= 0 && adjustedPosition < items.size()) {
                    ListItem selectedItem = items.get(adjustedPosition);
                    if (selectedItem.getType() == ListItem.TYPE_ITEM) {
                        String selectedCertificate = selectedItem.getText();
                        String[] parts = selectedCertificate.split(" - Expires:");
                        if (parts.length < 1) {
                            Toast.makeText(getActivity(), "Certificate format error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String certificateName = parts[0].trim();
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("Delete Certificate")
                                .setMessage("Are you sure you want to delete " + certificateName + "?")
                                .setPositiveButton("Yes", (dialog, which) -> deleteCertificate(certificateName))
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing click", e);
                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    /**
     * Loads certificates from the local database and groups them by platform.
     */
    public void loadCertificatesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> certificates = database.certificateDao().getAllCertificates();
            List<ListItem> tempItems = new ArrayList<>();
            List<String> knownPlatforms = Arrays.asList("Velká éra", "Vrtuľníky", "Ultralighty", "Vetrone");

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

            // Add certificates grouped by known platforms
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

            // Add certificates from unknown platforms under "Ostatné certifikáty"
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

            getActivity().runOnUiThread(() -> {
                items.clear();
                items.addAll(tempItems);
                adapter = new SectionedCertificateAdapter(getActivity(), items);
                listViewCertificates.setAdapter(adapter);
            });
        });
    }

    /**
     * Fetches certificate data from the server, updates the local database,
     * and removes local certificates not present on the server.
     */
    private void loadCertificatesFromServer() {
        CertificateApi api = RetrofitClient.getApi();
        Call<List<Certificate>> call = api.getAllCertificates();
        call.enqueue(new Callback<List<Certificate>>() {
            @Override
            public void onResponse(Call<List<Certificate>> call, Response<List<Certificate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Certificate> serverCertificates = response.body();

                    // Remove junk local certificate data (not present on the server)
                    removeLocalJunkCertificates(serverCertificates);

                    // Update local database with certificates from the server
                    Executors.newSingleThreadExecutor().execute(() -> {
                        for (Certificate cert : serverCertificates) {
                            Certificate existingCert = database.certificateDao().getCertificateById(cert.getId());
                            if (existingCert == null) {
                                database.certificateDao().insertCertificate(cert);
                            } else {
                                database.certificateDao().updateCertificate(cert);
                            }
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> loadCertificatesFromDatabase());
                        }
                    });
                } else {
                    Log.e(TAG, "Server returned failure: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Certificate>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch certificates from server: " + t.getMessage());
            }
        });
    }

    /**
     * Removes local certificate records that are not present in the server data.
     */
    private void removeLocalJunkCertificates(List<Certificate> serverCertificates) {
        // Create a set of certificate IDs from the server
        Set<Integer> serverIds = new HashSet<>();
        for (Certificate cert : serverCertificates) {
            serverIds.add(cert.getId());
        }
        // Run deletion on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> localCertificates = database.certificateDao().getAllCertificates();
            for (Certificate localCert : localCertificates) {
                if (!serverIds.contains(localCert.getId())) {
                    // This local certificate is considered junk and should be deleted
                    database.certificateDao().deleteCertificate(localCert);
                    Log.d(TAG, "Deleted junk local certificate with id: " + localCert.getId());
                }
            }
        });
    }

    /**
     * Deletes a certificate by its name. First deletes from server, then from local database.
     */
    private void deleteCertificate(String certificateString) {
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
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        loadCertificatesFromDatabase();
                                        Toast.makeText(getActivity(), "Certificate deleted: " + certificateName, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Server deletion failed for certificate: " + certificateName, Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error deleting certificate from server: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Certificate not found: " + certificateName, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Checks whether network connectivity is available.
     */
    private boolean isNetworkAvailable() {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    return connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }
        return false;
    }

    /**
     * Aggregates flight data from the local database and updates the UI.
     */
    private void calculateFlightAggregatesFromLocal() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();
            int totalFlightTime = 0;
            int totalMultiPilotTime = 0;
            int totalLandingsDay = 0;
            int totalLandingsNight = 0;
            int totalNightTime = 0;
            int totalIfrTime = 0;
            int totalPicTime = 0;
            int totalCopilotTime = 0;
            int totalDualTime = 0;
            int totalInstructorTime = 0;
            Map<String, Integer> fstdSummary = new HashMap<>();

            for (Flight flight : flights) {
                totalFlightTime += flight.getTotalFlightTime();
                totalMultiPilotTime += (flight.getMultiPilotTime() == null ? 0 : flight.getMultiPilotTime());
                totalLandingsDay += (flight.getLandingsDay() == null ? 0 : flight.getLandingsDay());
                totalLandingsNight += (flight.getLandingsNight() == null ? 0 : flight.getLandingsNight());
                totalNightTime += (flight.getNightTime() == null ? 0 : flight.getNightTime());
                totalIfrTime += (flight.getIfrTime() == null ? 0 : flight.getIfrTime());
                totalPicTime += (flight.getPicTime() == null ? 0 : flight.getPicTime());
                totalCopilotTime += (flight.getCopilotTime() == null ? 0 : flight.getCopilotTime());
                totalDualTime += (flight.getDualTime() == null ? 0 : flight.getDualTime());
                totalInstructorTime += (flight.getInstructorTime() == null ? 0 : flight.getInstructorTime());

                String fstdType = flight.getFstdType();
                int fstdTotalTime = (flight.getFstdTotalTime() == null ? 0 : flight.getFstdTotalTime());
                if (fstdType != null && !fstdType.isEmpty() && fstdTotalTime > 0) {
                    int prev = fstdSummary.containsKey(fstdType) ? fstdSummary.get(fstdType) : 0;
                    fstdSummary.put(fstdType, prev + fstdTotalTime);
                }
            }

            final String totalFlightTimeStr = formatMinutes(totalFlightTime, "Celkový letový čas");
            final String totalMultiPilotTimeStr = formatMinutes(totalMultiPilotTime, "Celkový multi pilot čas");
            final String landingsStr = "Súčet pristátí: " + totalLandingsDay + " (deň), " + totalLandingsNight + " (noc)";
            final String operationTimeStr = formatMinutes(totalNightTime, "Nočný let") + ", " + formatMinutes(totalIfrTime, "IFR let");
            final String pilotFunctionTimeStr = formatMinutes(totalPicTime, "PIC") + ", "
                    + formatMinutes(totalCopilotTime, "Kopilot") + ", "
                    + formatMinutes(totalDualTime, "Dvojpilot") + ", "
                    + formatMinutes(totalInstructorTime, "Inštruktor");
            final String fstdSummaryStr = getFstdSummaryString(fstdSummary);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvTotalFlightTime.setText(totalFlightTimeStr);
                    tvMultiPilotTime.setText(totalMultiPilotTimeStr);
                    tvLandings.setText(landingsStr);
                    tvOperationTime.setText(operationTimeStr);
                    tvPilotFunctionTime.setText(pilotFunctionTimeStr);
                    tvFstdSummary.setText("FSTD Sumarizácia:\n" + fstdSummaryStr);
                });
            }
        });
    }

    /**
     * Helper method to format minutes into a string with hours and minutes.
     */
    private String formatMinutes(int totalMinutes, String label) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return label + ": " + hours + " h " + minutes + " m";
    }

    /**
     * Helper method to build a summary string for FSTD sessions.
     */
    private String getFstdSummaryString(Map<String, Integer> fstdSummary) {
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Integer> entry : fstdSummary.entrySet()) {
            int totalMinutes = entry.getValue();
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            summary.append(entry.getKey())
                    .append(": ")
                    .append(hours).append(" h ")
                    .append(minutes).append(" m\n");
        }
        return summary.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recalculate flight aggregates when the fragment resumes
        calculateFlightAggregatesFromLocal();
    }
}
