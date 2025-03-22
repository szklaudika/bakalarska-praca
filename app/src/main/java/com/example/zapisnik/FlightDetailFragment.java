package com.example.zapisnik;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Set flight details from passed Bundle, if available.
        Bundle args = getArguments();
        if (args != null) {
            setFlightDetails(args);
        }

        // Calculate and display aggregated flight data
        calculateFlightAggregates();

        return view;
    }

    /**
     * Sets the flight details in the UI from the passed Bundle.
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

    /**
     * Calculates aggregated flight data from all flights stored in the local database.
     * Displays the results via a Toast.
     */
    private void calculateFlightAggregates() {
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
                // Use null-safe operations in case any field is null
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

    @Override
    public void onResume() {
        super.onResume();
        // Recalculate aggregates when the fragment resumes.
        calculateFlightAggregates();
    }
}
