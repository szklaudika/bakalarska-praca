package com.example.zapisnik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    // Aggregated flight data TextViews
    private TextView tvTotalFlightTime;
    private TextView tvMultiPilotTime;
    private TextView tvLandings;
    private TextView tvNocnyLet;
    private TextView tvIfr;
    private TextView tvPic;
    private TextView tvKopilot;
    private TextView tvDvojpilot;
    private TextView tvInstructor;
    private TextView tvFstdSummary;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enables the menu in this fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout with the redesigned summarizations container
        View view = inflater.inflate(R.layout.activity_profile_coordinator, container, false);

        // --- Profile Header Setup ---
        ImageView profileImageView = view.findViewById(R.id.img_profile_pic);
        ImageView settingsIcon = view.findViewById(R.id.img_settings);
        settingsIcon.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // --- Retrieve Flight Data Views from the layout ---
        tvTotalFlightTime = view.findViewById(R.id.tv_total_flight_time_value);
        tvMultiPilotTime = view.findViewById(R.id.tv_multi_pilot_time_value);
        tvLandings = view.findViewById(R.id.tv_landings_value);
        tvNocnyLet = view.findViewById(R.id.tv_nocny_let_value);
        tvIfr = view.findViewById(R.id.tv_ifr_value);
        tvPic = view.findViewById(R.id.tv_pic_value);
        tvKopilot = view.findViewById(R.id.tv_kopilot_value);
        tvDvojpilot = view.findViewById(R.id.tv_dvojpilot_value);
        tvInstructor = view.findViewById(R.id.tv_instructor_value);
        tvFstdSummary = view.findViewById(R.id.tv_fstd_summary_value);

        // --- Toggle Bar Setup ---
        TextView tvToggleSummarizations = view.findViewById(R.id.tv_toggle_summarizations);
        TextView tvToggleCertificates = view.findViewById(R.id.tv_toggle_certificates);
        final LinearLayout llSummarizations = view.findViewById(R.id.ll_summarizations);
        final LinearLayout llCertificates = view.findViewById(R.id.ll_certificates);

        // Retrieve dark mode preference
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isLightMode = prefs.getBoolean("dark_mode", false);
        int selectedToggleDrawable = isLightMode ? R.drawable.toggle_segment_selected_light : R.drawable.toggle_segment_selected;

        // By default, show summarizations and hide certificates.
        tvToggleSummarizations.setBackgroundResource(selectedToggleDrawable);
        tvToggleCertificates.setBackgroundResource(android.R.color.transparent);
        llSummarizations.setVisibility(View.VISIBLE);
        llCertificates.setVisibility(View.GONE);

        tvToggleSummarizations.setOnClickListener(v -> {
            tvToggleSummarizations.setBackgroundResource(selectedToggleDrawable);
            tvToggleCertificates.setBackgroundResource(android.R.color.transparent);
            llSummarizations.setVisibility(View.VISIBLE);
            llCertificates.setVisibility(View.GONE);
        });
        tvToggleCertificates.setOnClickListener(v -> {
            tvToggleCertificates.setBackgroundResource(selectedToggleDrawable);
            tvToggleSummarizations.setBackgroundResource(android.R.color.transparent);
            llSummarizations.setVisibility(View.GONE);
            llCertificates.setVisibility(View.VISIBLE);
        });

        // --- Initialize certificate database and load data ---
        database = CertificateDatabase.getInstance(getActivity());
        loadCertificatesFromDatabase();
        loadCertificatesFromServer();
        calculateFlightAggregatesFromLocal();

        // --- Setup Certificates ListView ---
        listViewCertificates = view.findViewById(R.id.list_view_certificates);
        listViewCertificates.setOnItemClickListener((parent, itemView, position, id) -> {
            try {
                if (position >= 0 && position < items.size()) {
                    ListItem selectedItem = items.get(position);
                    if (selectedItem.getType() == ListItem.TYPE_ITEM) {
                        String selectedCertificate = selectedItem.getText();
                        String[] parts = selectedCertificate.split(" - Expires:");
                        if (parts.length < 1) {
                            Toast.makeText(getActivity(), "Certificate format error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String certificateName = parts[0].trim();
                        new AlertDialog.Builder(getActivity())
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

    /**
     * Call this method after setting the ListView adapter to calculate its height.
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView.getAdapter() == null) {
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            View listItem = listView.getAdapter().getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listView.getAdapter().getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads certificates from the local database and groups them by platform.
     */
    public void loadCertificatesFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Ensure fragment is still attached before using getActivity()
            Context context = getContext();
            if (context == null) return;  // safely exit if context is unavailable

            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);

            Log.d(TAG, "loadCertificatesFromDatabase() - userId: " + userId);

            List<Certificate> certificates = database.certificateDao().getCertificatesByUserId(userId);
            Log.d(TAG, "Certificates fetched from DB: " + certificates.size());

            List<ListItem> tempItems = new ArrayList<>();
            List<String> knownPlatforms = Arrays.asList("Velká éra", "Vrtuľníky", "Ultralighty", "Vetrone");

            Map<String, List<Certificate>> groupedByPlatform = new HashMap<>();
            for (Certificate cert : certificates) {
                String platform = cert.getPlatform();
                if (platform == null || platform.isEmpty() || !knownPlatforms.contains(platform)) {
                    platform = "Ostatné certifikáty";
                }
                groupedByPlatform.computeIfAbsent(platform, k -> new ArrayList<>()).add(cert);
            }

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
                        String certificateDisplay = cert.getCertificateType() + " - Expires: " + formattedExpiry;
                        tempItems.add(new ListItem(ListItem.TYPE_ITEM, certificateDisplay, detailPart));
                    }
                }
            }

            // Check again before updating UI on the main thread
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    items.clear();
                    items.addAll(tempItems);
                    if (getContext() != null) {
                        adapter = new SectionedCertificateAdapter(getContext(), items);
                        listViewCertificates.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(listViewCertificates);
                        Log.d(TAG, "Certificates ListView updated. Total items: " + items.size());
                    }
                });
            }
        });
    }


    /**
     * Fetches certificate data from the server, updates the local database,
     * and removes local certificates not present on the server.
     */
    private void loadCertificatesFromServer() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        Log.d(TAG, "loadCertificatesFromServer() - userId: " + userId);

        CertificateApi api = RetrofitClient.getApi();
        Call<List<Certificate>> call = api.getAllCertificates(userId);
        call.enqueue(new Callback<List<Certificate>>() {
            @Override
            public void onResponse(Call<List<Certificate>> call, Response<List<Certificate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Certificate> serverCertificates = response.body();
                    Log.d(TAG, "Certificates fetched from server: " + serverCertificates.size());

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
     * Deletes a certificate by its name. First deletes from server, then from local DB.
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
     * Aggregates flight data from the local database for the current user and updates the UI.
     */
    private void calculateFlightAggregatesFromLocal() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Retrieve the current user's id and fetch only their flights.
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);
            List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getFlightsByUserId(userId);
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

            final String totalFlightTimeStr = formatMinutes(totalFlightTime);
            final String totalMultiPilotTimeStr = formatMinutes(totalMultiPilotTime);
            final String landingsStr = totalLandingsDay + " (deň), " + totalLandingsNight + " (noc)";
            final String nocnyLetStr = formatMinutes(totalNightTime);
            final String ifrStr = formatMinutes(totalIfrTime);
            final String picStr = formatMinutes(totalPicTime);
            final String kopilotStr = formatMinutes(totalCopilotTime);
            final String dvojpilotStr = formatMinutes(totalDualTime);
            final String instructorStr = formatMinutes(totalInstructorTime);
            final String fstdSummaryStr = getFstdSummaryString(fstdSummary);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvTotalFlightTime.setText(totalFlightTimeStr);
                    tvMultiPilotTime.setText(totalMultiPilotTimeStr);
                    tvLandings.setText(landingsStr);
                    tvNocnyLet.setText(nocnyLetStr);
                    tvIfr.setText(ifrStr);
                    tvPic.setText(picStr);
                    tvKopilot.setText(kopilotStr);
                    tvDvojpilot.setText(dvojpilotStr);
                    tvInstructor.setText(instructorStr);
                    tvFstdSummary.setText(fstdSummaryStr);
                });
            }
        });
    }

    private String formatMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return hours + " h " + minutes + " m";
    }

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
        calculateFlightAggregatesFromLocal();
        if (isNetworkAvailable()) {
            syncOfflineCertificates();
        }
    }

    private void syncOfflineCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> unsyncedCertificates = database.certificateDao().getUnsyncedCertificates();
            for (Certificate cert : unsyncedCertificates) {
                sendCertificateToServer(cert);
            }
        });
    }

    private void sendCertificateToServer(Certificate certificate) {
        RetrofitClient.getApi().addCertificate(certificate).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.certificateDao().markAsSynced(certificate.getId());
                    });
                } else {
                    Log.d("ProfileFragment", "Failed to sync certificate: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("ProfileFragment", "Failed to sync certificate: " + t.getMessage());
            }
        });
    }
}
