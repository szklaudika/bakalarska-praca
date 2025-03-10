package com.example.zapisnik;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        // Add TextWatcher to etDate to format the input dynamically
        etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Automatically format the input as the user types
                String formattedText = formatDate(charSequence.toString());
                if (!formattedText.equals(charSequence.toString())) {
                    etDate.setText(formattedText);
                    etDate.setSelection(formattedText.length());  // Keep the cursor at the end
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Add TextWatcher to etFstdDate to format the input dynamically (same logic as etDate)
        etFstdDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Automatically format the input as the user types
                String formattedText = formatDate(charSequence.toString());
                if (!formattedText.equals(charSequence.toString())) {
                    etFstdDate.setText(formattedText);
                    etFstdDate.setSelection(formattedText.length());  // Keep the cursor at the end
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    private void addFlight() {
        // Format the dates before proceeding
        String formattedDate = formatDate(etDate.getText().toString().trim());
        String formattedFstdDate = formatDate(etFstdDate.getText().toString().trim());

        // If the formatted dates are valid (not empty), proceed with adding the flight
        if (!formattedDate.isEmpty() && !formattedFstdDate.isEmpty()) {
            Flight flight = new Flight(
                    formattedDate,  // Use the formatted date here
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
                    formattedFstdDate,  // Use the formatted fstd date here
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

    // Method to format the date dynamically with dashes
    private String formatDate(String input) {
        // Remove any non-numeric characters
        input = input.replaceAll("[^0-9]", "");

        StringBuilder formattedDate = new StringBuilder();

        // Format the date as YYYY-MM-DD dynamically
        if (input.length() >= 1) formattedDate.append(input.substring(0, Math.min(4, input.length()))); // Year
        if (input.length() >= 5) formattedDate.append("-").append(input.substring(4, Math.min(6, input.length()))); // Month
        if (input.length() >= 7) formattedDate.append("-").append(input.substring(6, Math.min(8, input.length()))); // Day

        // Validate the month
        String month = formattedDate.length() >= 7 ? formattedDate.substring(5, 7) : "";
        if (!month.isEmpty() && Integer.parseInt(month) > 12) {
            formattedDate.replace(5, 7, "12");  // Set month to 12 if it's greater than 12
        }

        // Validate the day
        String day = formattedDate.length() >= 10 ? formattedDate.substring(8, 10) : "";
        if (!day.isEmpty()) {
            int intDay = Integer.parseInt(day);
            if (intDay > 31) {
                formattedDate.replace(8, 10, "31");  // Set day to 31 if it's greater than 31
            } else {
                int monthInt = Integer.parseInt(month);
                if (monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) {
                    // Set day to 30 if the month has 30 days
                    if (intDay > 30) {
                        formattedDate.replace(8, 10, "30");
                    }
                } else if (monthInt == 2) {
                    // Check for leap year February
                    int maxDay = (isLeapYear(Integer.parseInt(formattedDate.substring(0, 4)))) ? 29 : 28;
                    if (intDay > maxDay) {
                        formattedDate.replace(8, 10, String.valueOf(maxDay));
                    }
                }
            }
        }

        return formattedDate.toString();
    }

    // Helper method to check if a year is a leap year
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}
