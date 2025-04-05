package com.example.zapisnik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    // Date filter views and current filter dates
    private EditText etFromDate, etToDate;
    private Button btnApplyFilter;
    private Date currentFromDate = null;
    private Date currentToDate = null;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
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

        // --- Initialize Date Filter Views ---
        etFromDate = view.findViewById(R.id.et_from_date);
        etToDate = view.findViewById(R.id.et_to_date);
        btnApplyFilter = view.findViewById(R.id.btn_apply_filter);
        // Apply dynamic date formatting to the filter EditTexts.
        setExpiryTextWatcher(etFromDate);
        setExpiryTextWatcher(etToDate);

        btnApplyFilter.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                String fromText = etFromDate.getText().toString().trim();
                String toText = etToDate.getText().toString().trim();
                // If either field is empty, clear the filter.
                if (fromText.isEmpty() || toText.isEmpty()) {
                    currentFromDate = null;
                    currentToDate = null;
                    Toast.makeText(getActivity(), "Filter cleared.", Toast.LENGTH_SHORT).show();
                } else {
                    currentFromDate = sdf.parse(fromText);
                    currentToDate = sdf.parse(toText);
                    Toast.makeText(getActivity(), "Filter applied.", Toast.LENGTH_SHORT).show();
                }
                calculateFlightAggregatesFromLocal(currentFromDate, currentToDate);
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Please enter dates in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Initialize certificate database and load data ---
        database = CertificateDatabase.getInstance(getActivity());
        loadCertificatesFromDatabase();
        loadCertificatesFromServer();
        calculateFlightAggregatesFromLocal(null, null);

        // --- Setup Certificates ListView ---
        listViewCertificates = view.findViewById(R.id.list_view_certificates);
        listViewCertificates.setOnItemClickListener((parent, itemView, position, id) -> {
            try {
                if (position >= 0 && position < items.size()) {
                    ListItem selectedItem = items.get(position);
                    if (selectedItem.getType() == ListItem.TYPE_ITEM) {
                        int certificateId = selectedItem.getId();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete Certificate")
                                .setMessage("Are you sure you want to delete this certificate?")
                                .setPositiveButton("Yes", (dialog, which) -> deleteCertificate(certificateId))
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
            Context context = getContext();
            if (context == null) return;

            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);
            Log.d(TAG, "loadCertificatesFromDatabase() - userId: " + userId);

            List<Certificate> certificates = database.certificateDao().getCertificatesByUserId(userId);
            Log.d(TAG, "Certificates fetched from DB: " + certificates.size());

            // Create a translation mapping from Slovak to English.
            Map<String, String> platformTranslation = new HashMap<>();
            platformTranslation.put("Velka era", "Airplanes");
            platformTranslation.put("Vrtuľníky", "Helicopters");
            platformTranslation.put("Ultralighty", "Ultralights");
            platformTranslation.put("Vetrone", "Gliders");

            List<ListItem> tempItems = new ArrayList<>();
            // Group certificates using the translated platform name.
            Map<String, List<Certificate>> groupedByPlatform = new HashMap<>();
            for (Certificate cert : certificates) {
                String dbPlatform = cert.getPlatform();
                String displayPlatform = platformTranslation.get(dbPlatform);
                if (displayPlatform == null) {
                    displayPlatform = "Other certificates";
                }
                groupedByPlatform.computeIfAbsent(displayPlatform, k -> new ArrayList<>()).add(cert);
            }

            // For a consistent order, use the translated names.
            List<String> displayPlatforms = new ArrayList<>(platformTranslation.values());
            displayPlatforms.add("Other certificates");

            for (String platform : displayPlatforms) {
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
                        if (cert.getNote() != null && !cert.getNote().trim().isEmpty()) {
                            detailPart += "\nNote: " + cert.getNote();
                        }
                        tempItems.add(new ListItem(ListItem.TYPE_ITEM, cert.getId(), formattedExpiry, detailPart));
                    }
                }
            }

            // Update UI on main thread.
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
                        // First, delete all certificates for this user from the local database.
                        database.certificateDao().deleteAllCertificatesForUser(userId);
                        // Then, insert the server certificates.
                        for (Certificate cert : serverCertificates) {
                            database.certificateDao().insertCertificate(cert);
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
     * Deletes a certificate by its ID. First deletes from server, then from local DB.
     */
    private void deleteCertificate(final int certificateId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Certificate certificateToDelete = database.certificateDao().getCertificateById(certificateId);
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
                                        Toast.makeText(getActivity(), "Certificate deleted successfully", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "Server deletion failed for certificate ID: " + certificateId, Toast.LENGTH_SHORT).show());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Error deleting certificate from server: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Certificate not found with ID: " + certificateId, Toast.LENGTH_SHORT).show());
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
     * Only flights within the given date range (if provided) are included.
     */
    private void calculateFlightAggregatesFromLocal(Date fromDate, Date toDate) {
        Executors.newSingleThreadExecutor().execute(() -> {
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            for (Flight flight : flights) {
                if (fromDate != null && toDate != null) {
                    try {
                        Date flightDate = sdf.parse(flight.getDate());
                        if (flightDate == null || flightDate.before(fromDate) || flightDate.after(toDate)) {
                            continue; // Skip flights outside the filter range.
                        }
                    } catch (ParseException e) {
                        continue;
                    }
                }
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
            final String landingsStr = totalLandingsDay + " (day), " + totalLandingsNight + " (night)";
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
        calculateFlightAggregatesFromLocal(currentFromDate, currentToDate);
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
                        Log.d("ProfileFragment", "Certificate synced: " + certificate.getId());
                    });
                } else {
                    Log.e("ProfileFragment", "Server responded with error: " + response.code() + " - " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ProfileFragment", "Failed to sync certificate: " + t.getMessage());
            }
        });
    }

    /**
     * Dynamically formats input text into the "yyyy-MM-dd" date format.
     */
    private void setExpiryTextWatcher(final EditText etExpiry) {
        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String formatted = formatDateInput(s.toString());
                if (!formatted.equals(s.toString())) {
                    etExpiry.setText(formatted);
                    etExpiry.setSelection(formatted.length());
                }
            }
        });
    }

    /**
     * Formats a string of digits as "yyyy-MM-dd".
     */
    private String formatDateInput(String input) {
        input = input.replaceAll("[^0-9]", "");
        StringBuilder sb = new StringBuilder();
        if (input.length() >= 1)
            sb.append(input.substring(0, Math.min(4, input.length())));
        if (input.length() >= 5)
            sb.append("-").append(input.substring(4, Math.min(6, input.length())));
        if (input.length() >= 7)
            sb.append("-").append(input.substring(6, Math.min(8, input.length())));
        if (sb.length() >= 7) {
            String month = sb.substring(5, 7);
            if (!month.isEmpty() && Integer.parseInt(month) > 12) {
                sb.replace(5, 7, "12");
            }
        }
        if (sb.length() >= 10) {
            String day = sb.substring(8, 10);
            if (!day.isEmpty() && Integer.parseInt(day) > 31) {
                sb.replace(8, 10, "31");
            }
        }
        return sb.toString();
    }
}
