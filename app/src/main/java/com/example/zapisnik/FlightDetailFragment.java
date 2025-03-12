package com.example.zapisnik;

import android.annotation.SuppressLint;
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
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightDetailFragment extends Fragment {

    private static final String TAG = "FlightDetailFragment";

    private FlightDatabase database;

    // Declare TextViews for flight details (including new fields)
    private TextView tvDate, tvDeparture, tvDepartureTime, tvArrival, tvArrivalTime, tvAircraftModel,
            tvRegistration, tvSinglePilotTime, tvMultiPilotTime, tvTotalFlightTime, tvPilotName,
            tvSinglePilot, tvLandingsDay, tvLandingsNight, tvNightTime, tvIfrTime, tvPicTime,
            tvCopilotTime, tvDualTime, tvInstructorTime, tvFstdDate, tvFstdType, tvFstdTotalTime, tvRemarks;

    public FlightDetailFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
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

        // Initialize the local database instance
        database = FlightDatabase.getInstance(getActivity());

        // For logging purposes, load flights from database and server.
        loadFlightsFromDatabase();
        loadFlightsFromServer();

        // Retrieve passed data via Bundle and set flight details.
        Bundle args = getArguments();
        if (args != null) {
            setFlightDetails(args);
        }

        return view;
    }

    /**
     * Converts minutes into a formatted string "Xh Ym".
     */
    private String formatTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    /**
     * Returns the string value or "-" if null or empty.
     */
    private String getStringOrDash(Bundle args, String key) {
        String value = args.getString(key);
        // Check if the value is actually null, empty, or the literal string "null"
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return "-";
        }
        return value;
    }


    /**
     * Returns a numeric value as formatted time (if requested) or raw value.
     * Also, if the numeric field equals 0 (and formatting is requested), returns "-".
     */
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

    /**
     * Returns the boolean value as provided true/false string or "-" if not provided.
     */
    private String getBooleanOrDash(Bundle args, String key, String trueText, String falseText) {
        if (args.containsKey(key)) {
            boolean value = args.getBoolean(key);
            return value ? trueText : falseText;
        }
        return "-";
    }

    /**
     * Retrieves values from the Bundle and sets them into the appropriate TextViews.
     * If a field is null, missing, or (for time fields) equals 0, a dash ("-") is displayed.
     */
    private void setFlightDetails(Bundle args) {
        tvDate.setText(" " + getStringOrDash(args, "date"));
        tvDeparture.setText(" " + getStringOrDash(args, "departure"));
        tvDepartureTime.setText(" " + getStringOrDash(args, "departureTime"));
        tvArrival.setText(" " + getStringOrDash(args, "arrival"));
        tvArrivalTime.setText(" " + getStringOrDash(args, "arrivalTime"));
        tvAircraftModel.setText(" " + getStringOrDash(args, "aircraftModel"));
        tvRegistration.setText(" " + getStringOrDash(args, "registration"));
        tvSinglePilotTime.setText(" " + getIntOrDash(args, "singlePilotTime", false)); // Required field
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
     * Load flights from the local database (for logging purposes).
     */
    public void loadFlightsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> flights = database.flightDao().getAllFlights();
            Log.d(TAG, "Loaded flights from DB: " + flights.size());
        });
    }

    /**
     * Load flights from the server using Retrofit (for logging purposes).
     */
    private void loadFlightsFromServer() {
        FlightApi api = RetrofitClient.getFlightApi();
        Call<List<Flight>> call = api.getAllFlights();
        call.enqueue(new Callback<List<Flight>>() {
            @Override
            public void onResponse(Call<List<Flight>> call, Response<List<Flight>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Flight> serverFlights = response.body();
                    Log.d(TAG, "Loaded flights from server: " + serverFlights.size());
                }
            }

            @Override
            public void onFailure(Call<List<Flight>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load flights from server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
