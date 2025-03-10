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
    private static final String BASE_URL = "http://10.0.2.2/zapisnik_db/"; // Replace with your actual server URL

    private FlightDatabase database;

    // Declare TextViews for the flight details
    private TextView tvDate, tvDeparture, tvDepartureTime, tvArrival, tvArrivalTime, tvAircraftModel,
            tvRegistration, tvSinglePilotTime, tvMultiPilotTime, tvTotalFlightTime, tvPilotName,
            tvLandings, tvNightTime, tvIfrTime, tvPicTime, tvCopilotTime, tvDualTime, tvInstructorTime,
            tvFstdDate, tvFstdType, tvFstdTotalTime, tvRemarks;

    public FlightDetailFragment() {
        // Empty constructor for fragment
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        // Initialize the TextViews for the flight details
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
        tvLandings = view.findViewById(R.id.tv_flight_landings);
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

        // Initialize the local database
        database = FlightDatabase.getInstance(getActivity());

        // Load flight details from the database or server
        loadFlightsFromDatabase();
        loadFlightsFromServer();

        // Retrieve the arguments from the FlightListFragment (if any)
        Bundle args = getArguments();
        if (args != null) {
            // Set all the received data into the TextViews
            setFlightDetails(args);
        }

        return view;
    }
    private void setFlightDetails(Bundle args) {
        // Use a default value for fields that can be null
        String date = args.getString("date", "Unknown Date");
        String departure = args.getString("departure", "Unknown Departure Place");
        String departureTime = args.getString("departureTime", "Unknown Departure Time");
        String arrival = args.getString("arrival", "Unknown Arrival Place");
        String arrivalTime = args.getString("arrivalTime", "Unknown Arrival Time");
        String aircraftModel = args.getString("aircraftModel", "Unknown Aircraft Model");
        String registration = args.getString("registration", "Unknown Registration");
        int singlePilotTime = args.getInt("singlePilotTime", 0);
        int multiPilotTime = args.getInt("multiPilotTime", 0);
        int totalFlightTime = args.getInt("totalFlightTime", 0);
        String pilotName = args.getString("pilotName", "Unknown Pilot");
        int landings = args.getInt("landings", 0);
        int nightTime = args.getInt("nightTime", 0);
        int ifrTime = args.getInt("ifrTime", 0);
        int picTime = args.getInt("picTime", 0);
        int copilotTime = args.getInt("copilotTime", 0);
        int dualTime = args.getInt("dualTime", 0);
        int instructorTime = args.getInt("instructorTime", 0);
        String fstdDate = args.getString("fstdDate", "Unknown Date");
        String fstdType = args.getString("fstdType", "Unknown Type");
        int fstdTotalTime = args.getInt("fstdTotalTime", 0);
        String remarks = args.getString("remarks", "No Remarks");

        // Set the values into the TextViews
        tvDate.setText("Date: " + date);
        tvDeparture.setText("From: " + departure);
        tvDepartureTime.setText("Departure Time: " + departureTime);
        tvArrival.setText("To: " + arrival);
        tvArrivalTime.setText("Arrival Time: " + arrivalTime);
        tvAircraftModel.setText("Aircraft Model: " + aircraftModel);
        tvRegistration.setText("Registration: " + registration);
        tvSinglePilotTime.setText("Single Pilot Time: " + singlePilotTime + " min");
        tvMultiPilotTime.setText("Multi Pilot Time: " + multiPilotTime + " min");
        tvTotalFlightTime.setText("Total Flight Time: " + totalFlightTime + " min");
        tvPilotName.setText("Pilot Name: " + pilotName);
        tvLandings.setText("Landings: " + landings);
        tvNightTime.setText("Night Time: " + nightTime + " min");
        tvIfrTime.setText("IFR Time: " + ifrTime + " min");
        tvPicTime.setText("PIC Time: " + picTime + " min");
        tvCopilotTime.setText("Copilot Time: " + copilotTime + " min");
        tvDualTime.setText("Dual Time: " + dualTime + " min");
        tvInstructorTime.setText("Instructor Time: " + instructorTime + " min");
        tvFstdDate.setText("FSTD Date: " + fstdDate);
        tvFstdType.setText("FSTD Type: " + fstdType);
        tvFstdTotalTime.setText("FSTD Total Time: " + fstdTotalTime + " min");
        tvRemarks.setText("Remarks: " + remarks);
    }



    // Load flights from the local database (SQLite or Room)
    public void loadFlightsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> flights = database.flightDao().getAllFlights();
            // No need to update the list as we removed the ListView
        });
    }

    // Load flights from the server using Retrofit
    private void loadFlightsFromServer() {
        FlightApi api = RetrofitClient.getFlightApi();
        Call<List<Flight>> call = api.getAllFlights();

        call.enqueue(new Callback<List<Flight>>() {
            @Override
            public void onResponse(Call<List<Flight>> call, Response<List<Flight>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Flight> serverFlights = response.body();
                    // Handle server flights here, but no ListView update needed
                }
            }

            @Override
            public void onFailure(Call<List<Flight>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load flights from server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
