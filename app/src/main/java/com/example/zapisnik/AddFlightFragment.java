package com.example.zapisnik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFlightFragment extends Fragment {

    private EditText etDate, etDeparturePlace, etDepartureTime, etArrivalPlace, etArrivalTime, etAircraftModel, etRegistration,
            etSinglePilotTime, etMultiPilotTime, etTotalFlightTime, etPilotName, etLandings, etNightTime,
            etIfrTime, etPicTime, etCopilotTime, etDualTime, etInstructorTime, etFstdDate, etFstdType,
            etFstdTotalTime, etRemarks;
    private Button btnAddFlight;
    private FlightDatabase flightDatabase;

    public AddFlightFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flight, container, false);

        // Initialize UI elements
        etDate = view.findViewById(R.id.et_flight_date);
        etDeparturePlace = view.findViewById(R.id.et_departure_place);
        etDepartureTime = view.findViewById(R.id.et_departure_time);
        etArrivalPlace = view.findViewById(R.id.et_arrival_place);
        etArrivalTime = view.findViewById(R.id.et_arrival_time);
        etAircraftModel = view.findViewById(R.id.et_aircraft_model);
        etRegistration = view.findViewById(R.id.et_registration);
        etSinglePilotTime = view.findViewById(R.id.et_single_pilot_time);
        etMultiPilotTime = view.findViewById(R.id.et_multi_pilot_time);
        etTotalFlightTime = view.findViewById(R.id.et_total_flight_time);
        etPilotName = view.findViewById(R.id.et_pilot_name);
        etLandings = view.findViewById(R.id.et_landings);
        etNightTime = view.findViewById(R.id.et_night_time);
        etIfrTime = view.findViewById(R.id.et_ifr_time);
        etPicTime = view.findViewById(R.id.et_pic_time);
        etCopilotTime = view.findViewById(R.id.et_copilot_time);
        etDualTime = view.findViewById(R.id.et_dual_time);
        etInstructorTime = view.findViewById(R.id.et_instructor_time);
        etFstdDate = view.findViewById(R.id.et_fstd_date);
        etFstdType = view.findViewById(R.id.et_fstd_type);
        etFstdTotalTime = view.findViewById(R.id.et_fstd_total_time);
        etRemarks = view.findViewById(R.id.et_remarks);
        btnAddFlight = view.findViewById(R.id.btn_add_flight);

        // Initialize local database instance
        flightDatabase = FlightDatabase.getInstance(getActivity());

        // Add flight on button click
        btnAddFlight.setOnClickListener(v -> addFlight());

        return view;
    }

    private void addFlight() {
        Flight flight = new Flight(
                etDate.getText().toString().trim(),
                etDeparturePlace.getText().toString().trim(),
                etDepartureTime.getText().toString().trim(),
                etArrivalPlace.getText().toString().trim(),
                etArrivalTime.getText().toString().trim(),
                etAircraftModel.getText().toString().trim(),
                etRegistration.getText().toString().trim(),
                Integer.parseInt(etSinglePilotTime.getText().toString().trim()),
                Integer.parseInt(etMultiPilotTime.getText().toString().trim()),
                Integer.parseInt(etTotalFlightTime.getText().toString().trim()),
                etPilotName.getText().toString().trim(),
                Integer.parseInt(etLandings.getText().toString().trim()),
                Integer.parseInt(etNightTime.getText().toString().trim()),
                Integer.parseInt(etIfrTime.getText().toString().trim()),
                Integer.parseInt(etPicTime.getText().toString().trim()),
                Integer.parseInt(etCopilotTime.getText().toString().trim()),
                Integer.parseInt(etDualTime.getText().toString().trim()),
                Integer.parseInt(etInstructorTime.getText().toString().trim()),
                etFstdDate.getText().toString().trim(),
                etFstdType.getText().toString().trim(),
                Integer.parseInt(etFstdTotalTime.getText().toString().trim()),
                etRemarks.getText().toString().trim()
        );

        // Check if network is available
        if (isNetworkAvailable()) {
            sendFlightToServer(flight);
        } else {
            saveFlightLocally(flight);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
    }

    private void saveFlightLocally(Flight flight) {
        Executors.newSingleThreadExecutor().execute(() -> {
            flightDatabase.flightDao().insert(flight);
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Flight saved locally!", Toast.LENGTH_SHORT).show());
        });
    }

    private void sendFlightToServer(Flight flight) {
        FlightApi flightApi = RetrofitClient.getFlightApi();

        flightApi.addFlight(flight).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Flight added to server!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Server Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to add flight to server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
