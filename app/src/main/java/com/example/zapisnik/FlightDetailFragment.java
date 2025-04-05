package com.example.zapisnik;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightDetailFragment extends Fragment {

    private static final String TAG = "FlightDetailFragment";
    private FlightDatabase database;

    // Declare TextViews for flight details
    private TextView tvDate, tvDeparture, tvDepartureTime, tvArrival, tvArrivalTime, tvAircraftModel,
            tvRegistration, tvSinglePilotTime, tvMultiPilotTime, tvTotalFlightTime, tvPilotName,
            tvSinglePilot, tvLandingsDay, tvLandingsNight, tvNightTime, tvIfrTime, tvPicTime,
            tvCopilotTime, tvDualTime, tvInstructorTime, tvFstdDate, tvFstdType, tvFstdTotalTime, tvRemarks;

    public FlightDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        // Initialize TextViews
        tvDate = view.findViewById(R.id.tv_flight_date);
        tvDeparture = view.findViewById(R.id.tv_flight_departure);
        tvDepartureTime = view.findViewById(R.id.tv_flight_departure_time);
        tvArrival = view.findViewById(R.id.tv_flight_arrival);
        tvArrivalTime = view.findViewById(R.id.tv_flight_arrival_time);
        tvAircraftModel = view.findViewById(R.id.tv_flight_aircraft_model);
        tvRegistration = view.findViewById(R.id.tv_flight_registration);
        tvSinglePilotTime = view.findViewById(R.id.tv_flight_single_pilot_time);
        tvMultiPilotTime = view.findViewById(R.id.tv_flight_multi_pilot_time);
        tvTotalFlightTime = view.findViewById(R.id.tv_flight_total_flight_time);
        tvPilotName = view.findViewById(R.id.tv_flight_pilot_name);
        tvSinglePilot = view.findViewById(R.id.tv_flight_single_pilot);
        tvLandingsDay = view.findViewById(R.id.tv_flight_landings_day);
        tvLandingsNight = view.findViewById(R.id.tv_flight_landings_night);
        tvNightTime = view.findViewById(R.id.tv_flight_night_time);
        tvIfrTime = view.findViewById(R.id.tv_flight_ifr_time);
        tvPicTime = view.findViewById(R.id.tv_flight_pic_time);
        tvCopilotTime = view.findViewById(R.id.tv_flight_copilot_time);
        tvDualTime = view.findViewById(R.id.tv_flight_dual_time);
        tvInstructorTime = view.findViewById(R.id.tv_flight_instructor_time);
        tvFstdDate = view.findViewById(R.id.tv_flight_fstd_date);
        tvFstdType = view.findViewById(R.id.tv_flight_fstd_type);
        tvFstdTotalTime = view.findViewById(R.id.tv_flight_fstd_total_time);
        tvRemarks = view.findViewById(R.id.tv_flight_remarks);

        database = FlightDatabase.getInstance(getActivity());

        // Check if the Bundle contains a flight id. If so, load the flight from local DB.
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            int flightId = args.getInt("id");
            Executors.newSingleThreadExecutor().execute(() -> {
                Flight flight = database.flightDao().getFlightById(flightId);
                if (flight != null) {
                    getActivity().runOnUiThread(() -> setFlightDetailsFromFlight(flight));
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Flight not found", Toast.LENGTH_SHORT).show());
                }
            });
        } else if (args != null) {
            // Fallback: use data passed via Bundle if no flight id is provided
            setFlightDetails(args);
        } else {
            Toast.makeText(getActivity(), "No flight details available", Toast.LENGTH_SHORT).show();
        }

        // Calculate and display aggregated flight data from the local database
        calculateFlightAggregates();

        Button btnDelete = view.findViewById(R.id.btn_delete_flight);
        btnDelete.setOnClickListener(v -> {
            if (args != null && args.containsKey("id")) {
                int flightId = args.getInt("id");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Flight")
                        .setMessage("Are you sure you want to delete this flight?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteFlight(flightId))
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(getActivity(), "Cannot delete: Missing flight ID", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    /**
     * Sets the flight details in the UI using a Bundle.
     */
    private void setFlightDetails(Bundle args) {
        tvDate.setText(" " + getStringOrDash(args, "date"));
        tvDeparture.setText(" " + getStringOrDash(args, "departure"));
        tvDepartureTime.setText(" " + getStringOrDash(args, "departureTime"));
        tvArrival.setText(" " + getStringOrDash(args, "arrival"));
        tvArrivalTime.setText(" " + getStringOrDash(args, "arrivalTime"));
        tvAircraftModel.setText(" " + getStringOrDash(args, "aircraftModel"));
        tvRegistration.setText(" " + getStringOrDash(args, "registration"));
        tvSinglePilotTime.setText(" " + getIntOrDash(args, "singlePilotTime", false));
        tvMultiPilotTime.setText(" " + getIntOrDash(args, "multiPilotTime", true));
        tvTotalFlightTime.setText(" " + getIntOrDash(args, "totalFlightTime", true));
        tvPilotName.setText(" " + getStringOrDash(args, "pilotName"));
        tvSinglePilot.setText(" " + getBooleanOrDash(args, "singlePilot", "ME", "SE"));
        tvLandingsDay.setText(" " + getIntOrDash(args, "landingsDay", false));
        tvLandingsNight.setText(" " + getIntOrDash(args, "landingsNight", false));
        tvNightTime.setText(" " + getIntOrDash(args, "nightTime", true));
        tvIfrTime.setText(" " + getIntOrDash(args, "ifrTime", true));
        tvPicTime.setText(" " + getIntOrDash(args, "picTime", true));
        tvCopilotTime.setText(" " + getIntOrDash(args, "copilotTime", true));
        tvDualTime.setText(" " + getIntOrDash(args, "dualTime", true));
        tvInstructorTime.setText(" " + getIntOrDash(args, "instructorTime", true));
        tvFstdDate.setText(" " + getStringOrDash(args, "fstdDate"));
        tvFstdType.setText(" " + getStringOrDash(args, "fstdType"));
        tvFstdTotalTime.setText(" " + getIntOrDash(args, "fstdTotalTime", true));
        tvRemarks.setText(" " + getStringOrDash(args, "remarks"));
    }

    /**
     * Sets the flight details in the UI from a Flight object.
     */
    private void setFlightDetailsFromFlight(Flight flight) {
        tvDate.setText(" " + (flight.getDate() != null ? flight.getDate() : "-"));
        tvDeparture.setText(" " + (flight.getDeparturePlace() != null ? flight.getDeparturePlace() : "-"));
        tvDepartureTime.setText(" " + (flight.getDepartureTime() != null ? flight.getDepartureTime() : "-"));
        tvArrival.setText(" " + (flight.getArrivalPlace() != null ? flight.getArrivalPlace() : "-"));
        tvArrivalTime.setText(" " + (flight.getArrivalTime() != null ? flight.getArrivalTime() : "-"));
        tvAircraftModel.setText(" " + (flight.getAircraftModel() != null ? flight.getAircraftModel() : "-"));
        tvRegistration.setText(" " + (flight.getRegistration() != null ? flight.getRegistration() : "-"));
        tvSinglePilotTime.setText(" " + formatInt(flight.getSinglePilotTime(), false));
        tvMultiPilotTime.setText(" " + formatInt(flight.getMultiPilotTime(), true));
        tvTotalFlightTime.setText(" " + formatInt(flight.getTotalFlightTime(), true));
        tvPilotName.setText(" " + (flight.getPilotName() != null ? flight.getPilotName() : "-"));
        tvSinglePilot.setText(" " + (flight.isSinglePilot() ? "ME" : "SE"));
        tvLandingsDay.setText(" " + formatInt(flight.getLandingsDay(), false));
        tvLandingsNight.setText(" " + formatInt(flight.getLandingsNight(), false));
        tvNightTime.setText(" " + formatInt(flight.getNightTime(), true));
        tvIfrTime.setText(" " + formatInt(flight.getIfrTime(), true));
        tvPicTime.setText(" " + formatInt(flight.getPicTime(), true));
        tvCopilotTime.setText(" " + formatInt(flight.getCopilotTime(), true));
        tvDualTime.setText(" " + formatInt(flight.getDualTime(), true));
        tvInstructorTime.setText(" " + formatInt(flight.getInstructorTime(), true));
        tvFstdDate.setText(" " + (flight.getFstdDate() != null ? flight.getFstdDate() : "-"));
        tvFstdType.setText(" " + (flight.getFstdType() != null ? flight.getFstdType() : "-"));
        tvFstdTotalTime.setText(" " + formatInt(flight.getFstdTotalTime(), true));
        tvRemarks.setText(" " + (flight.getRemarks() != null ? flight.getRemarks() : "-"));
    }

    private String getStringOrDash(Bundle args, String key) {
        String value = args.getString(key);
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return "-";
        }
        return value;
    }

    private String getIntOrDash(Bundle args, String key, boolean format) {
        if (args.containsKey(key)) {
            int value = args.getInt(key);
            if (format && value == 0) {
                return "-";
            }
            return format ? formatTime(value) : String.valueOf(value);
        }
        return "-";
    }

    private String getBooleanOrDash(Bundle args, String key, String trueText, String falseText) {
        if (args.containsKey(key)) {
            boolean value = args.getBoolean(key);
            return value ? trueText : falseText;
        }
        return "-";
    }

    private String formatTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    private String formatInt(Integer value, boolean format) {
        if (value == null) {
            return "-";
        }
        if (format && value == 0) {
            return "-";
        }
        return format ? formatTime(value) : String.valueOf(value);
    }

    // Helper class to store aggregates for a single user
    private static class UserFlightAggregate {
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
    }

    /**
     * Calculates aggregated flight data from all flights stored in the local database.
     * Displays the results via log messages (or update UI as needed).
     */
    private void calculateFlightAggregates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch all flights from the local database
            List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();
            // Map to hold aggregate values keyed by user id
            Map<Integer, UserFlightAggregate> aggregatesByUser = new HashMap<>();

            for (Flight flight : flights) {
                int userId = flight.getUserId();
                UserFlightAggregate agg = aggregatesByUser.get(userId);
                if (agg == null) {
                    agg = new UserFlightAggregate();
                    aggregatesByUser.put(userId, agg);
                }

                // Update aggregates with null-safe operations
                agg.totalFlightTime += flight.getTotalFlightTime();
                agg.totalMultiPilotTime += (flight.getMultiPilotTime() == null ? 0 : flight.getMultiPilotTime());
                agg.totalLandingsDay += (flight.getLandingsDay() == null ? 0 : flight.getLandingsDay());
                agg.totalLandingsNight += (flight.getLandingsNight() == null ? 0 : flight.getLandingsNight());
                agg.totalNightTime += (flight.getNightTime() == null ? 0 : flight.getNightTime());
                agg.totalIfrTime += (flight.getIfrTime() == null ? 0 : flight.getIfrTime());
                agg.totalPicTime += (flight.getPicTime() == null ? 0 : flight.getPicTime());
                agg.totalCopilotTime += (flight.getCopilotTime() == null ? 0 : flight.getCopilotTime());
                agg.totalDualTime += (flight.getDualTime() == null ? 0 : flight.getDualTime());
                agg.totalInstructorTime += (flight.getInstructorTime() == null ? 0 : flight.getInstructorTime());

                String fstdType = flight.getFstdType();
                int fstdTotalTime = (flight.getFstdTotalTime() == null ? 0 : flight.getFstdTotalTime());
                if (fstdType != null && !fstdType.isEmpty() && fstdTotalTime > 0) {
                    int prev = agg.fstdSummary.getOrDefault(fstdType, 0);
                    agg.fstdSummary.put(fstdType, prev + fstdTotalTime);
                }
            }

            // Log aggregates per user (update UI if needed)
            for (Map.Entry<Integer, UserFlightAggregate> entry : aggregatesByUser.entrySet()) {
                int userId = entry.getKey();
                UserFlightAggregate agg = entry.getValue();
                Log.d("FlightAggregates", "User " + userId +
                        " - Total Flight Time: " + agg.totalFlightTime +
                        ", Total Multi-Pilot Time: " + agg.totalMultiPilotTime);
            }
        });
    }

    /**
     * Helper method to format minutes into a string showing hours and minutes.
     */
    private String formatMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return hours + " h " + minutes + " m";
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

    private void deleteFlight(int flightId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Flight flight = database.flightDao().getFlightById(flightId);
            if (flight != null) {
                String url = "https://zapisnik-2b2a59a43d05.herokuapp.com/delete_flight.php?id=" + flightId;
                retrofit2.Call<Void> call = RetrofitClient.getFlightApi().deleteFlight(flightId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            database.flightDao().delete(flight);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getActivity(), "Flight deleted successfully", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().popBackStack(); // Go back to list
                                });
                            }
                        } else {
                            showToast("Server error while deleting flight");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast("Error: " + t.getMessage());
                    }

                    private void showToast(String message) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Flight not found locally", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // Recalculate aggregates when the fragment resumes.
        calculateFlightAggregates();
    }

}
