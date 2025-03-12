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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFlightFragment extends Fragment {

    private EditText etDate, etDeparturePlace, etDepartureTime, etArrivalPlace, etArrivalTime,
            etAircraftModel, etRegistration, etSinglePilotTime, etMultiPilotTime, etTotalFlightTime,
            etPilotName, etLandingsDay, etLandingsNight, etNightTime, etIfrTime, etPicTime, etCopilotTime,
            etDualTime, etInstructorTime, etFstdDate, etFstdType, etFstdTotalTime, etRemarks;

    // RadioGroup for selecting if the flight is single pilot
    private RadioGroup rgSinglePilot;

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
        rgSinglePilot = view.findViewById(R.id.rg_single_pilot);
        etMultiPilotTime = view.findViewById(R.id.et_multi_pilot_time);
        etTotalFlightTime = view.findViewById(R.id.et_total_flight_time);
        etPilotName = view.findViewById(R.id.et_pilot_name);
        etLandingsDay = view.findViewById(R.id.et_landings_day);
        etLandingsNight = view.findViewById(R.id.et_landings_night);
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

        // Dynamically format date input for etDate
        etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String formattedText = formatDate(editable.toString());
                if (!formattedText.equals(editable.toString())) {
                    etDate.setText(formattedText);
                    etDate.setSelection(formattedText.length());
                }
            }
        });

        // Add TextWatcher to format departure time as "HH:MM"
        etDepartureTime.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(isUpdating) return;
                isUpdating = true;
                String text = s.toString();
                // Remove any existing colon to recalculate
                text = text.replace(":", "");
                if(text.length() >= 2){
                    text = text.substring(0, 2) + ":" + text.substring(2);
                }
                if(text.length() > 5){
                    text = text.substring(0,5);
                }
                etDepartureTime.setText(text);
                etDepartureTime.setSelection(text.length());
                isUpdating = false;
            }
        });

        // Add TextWatcher to format arrival time as "HH:MM"
        etArrivalTime.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(isUpdating) return;
                isUpdating = true;
                String text = s.toString();
                // Remove any existing colon to recalculate
                text = text.replace(":", "");
                if(text.length() >= 2){
                    text = text.substring(0, 2) + ":" + text.substring(2);
                }
                if(text.length() > 5){
                    text = text.substring(0,5);
                }
                etArrivalTime.setText(text);
                etArrivalTime.setSelection(text.length());
                isUpdating = false;
            }
        });

        // Dynamically format date input for etFstdDate (optional field)
        etFstdDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable editable) {
                String formattedText = formatDate(editable.toString());
                if (!formattedText.equals(editable.toString())) {
                    etFstdDate.setText(formattedText);
                    etFstdDate.setSelection(formattedText.length());
                }
            }
        });

        return view;
    }

    private void addFlight() {
        // Format required flight date
        String formattedDate = formatDate(etDate.getText().toString().trim());
        if (formattedDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a valid flight date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Optional FSTD date: if empty, assign null
        String fstdDateInput = etFstdDate.getText().toString().trim();
        String formattedFstdDate = fstdDateInput.isEmpty() ? null : formatDate(fstdDateInput);

        // Check that at least one of Landings Day or Landings Night is provided
        String landingsDayStr = etLandingsDay.getText().toString().trim();
        String landingsNightStr = etLandingsNight.getText().toString().trim();
        if (landingsDayStr.isEmpty() && landingsNightStr.isEmpty()) {
            Toast.makeText(getActivity(), "At least one of Landings Day or Landings Night must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Required numeric field: single pilot time (must be provided)
            int singlePilotMinutes = Integer.parseInt(etSinglePilotTime.getText().toString().trim());

            // Optional numeric fields: if empty, assign null
            Integer multiPilotTime = etMultiPilotTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etMultiPilotTime.getText().toString().trim());
            // Total Flight Time is required
            int totalFlightTime = Integer.parseInt(etTotalFlightTime.getText().toString().trim());

            // Landings: at least one must be non-null
            Integer landingsDay = landingsDayStr.isEmpty() ? null : Integer.parseInt(landingsDayStr);
            Integer landingsNight = landingsNightStr.isEmpty() ? null : Integer.parseInt(landingsNightStr);

            // Optional time fields
            Integer nightTime = etNightTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etNightTime.getText().toString().trim());
            Integer ifrTime = etIfrTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etIfrTime.getText().toString().trim());
            Integer picTime = etPicTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etPicTime.getText().toString().trim());
            Integer copilotTime = etCopilotTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etCopilotTime.getText().toString().trim());
            Integer dualTime = etDualTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etDualTime.getText().toString().trim());
            Integer instructorTime = etInstructorTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etInstructorTime.getText().toString().trim());

            // Optional FSTD fields
            String fstdType = etFstdType.getText().toString().trim();
            if (fstdType.isEmpty()) {
                fstdType = null;
            }
            Integer fstdTotalTime = etFstdTotalTime.getText().toString().trim().isEmpty() ?
                    null : Integer.parseInt(etFstdTotalTime.getText().toString().trim());

            // Retrieve remaining required text fields
            String pilotName = etPilotName.getText().toString().trim();
            String departurePlace = etDeparturePlace.getText().toString().trim();
            String departureTimeStr = etDepartureTime.getText().toString().trim();
            String arrivalPlace = etArrivalPlace.getText().toString().trim();
            String arrivalTimeStr = etArrivalTime.getText().toString().trim();
            String aircraftModel = etAircraftModel.getText().toString().trim();
            String registration = etRegistration.getText().toString().trim();
            String remarks = etRemarks.getText().toString().trim();

            // Retrieve the single pilot boolean from RadioGroup (default false)
            int selectedSinglePilotId = rgSinglePilot.getCheckedRadioButtonId();
            boolean isSinglePilot = false;
            if (selectedSinglePilotId != -1) {
                String option = ((RadioButton) getView().findViewById(selectedSinglePilotId)).getText().toString();
                isSinglePilot = option.equalsIgnoreCase("yes");
            }

            // Create Flight object
            Flight flight = new Flight(
                    formattedDate,
                    departurePlace,
                    departureTimeStr,
                    arrivalPlace,
                    arrivalTimeStr,
                    aircraftModel,
                    registration,
                    singlePilotMinutes,
                    multiPilotTime,
                    totalFlightTime,
                    pilotName,
                    isSinglePilot,
                    landingsDay,
                    landingsNight,
                    nightTime,
                    ifrTime,
                    picTime,
                    copilotTime,
                    dualTime,
                    instructorTime,
                    formattedFstdDate,
                    fstdType,
                    fstdTotalTime,
                    remarks
            );

            // 1) Save flight locally
            saveFlightLocally(flight);

            // 2) Always try sending flight to the server
            sendFlightToServer(flight);

        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFlightLocally(Flight flight) {
        Executors.newSingleThreadExecutor().execute(() -> {
            flightDatabase.flightDao().insert(flight);
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getActivity(), "Flight saved locally!", Toast.LENGTH_SHORT).show());
        });
    }

    private void sendFlightToServer(Flight flight) {
        FlightApi flightApi = RetrofitClient.getFlightApi();
        flightApi.addFlight(flight).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Flight added to server!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Server Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Failed to add flight to server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to format date strings into "YYYY-MM-DD" format
    private String formatDate(String input) {
        // Remove any non-numeric characters
        input = input.replaceAll("[^0-9]", "");
        StringBuilder formattedDate = new StringBuilder();
        if (input.length() >= 1)
            formattedDate.append(input.substring(0, Math.min(4, input.length()))); // Year
        if (input.length() >= 5)
            formattedDate.append("-").append(input.substring(4, Math.min(6, input.length()))); // Month
        if (input.length() >= 7)
            formattedDate.append("-").append(input.substring(6, Math.min(8, input.length()))); // Day

        // Validate the month
        String month = formattedDate.length() >= 7 ? formattedDate.substring(5, 7) : "";
        if (!month.isEmpty() && Integer.parseInt(month) > 12) {
            formattedDate.replace(5, 7, "12");
        }

        // Validate the day
        String day = formattedDate.length() >= 10 ? formattedDate.substring(8, 10) : "";
        if (!day.isEmpty()) {
            int intDay = Integer.parseInt(day);
            if (intDay > 31) {
                formattedDate.replace(8, 10, "31");
            } else {
                int monthInt = Integer.parseInt(month);
                if (monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) {
                    if (intDay > 30) {
                        formattedDate.replace(8, 10, "30");
                    }
                } else if (monthInt == 2) {
                    int maxDay = (isLeapYear(Integer.parseInt(formattedDate.substring(0, 4)))) ? 29 : 28;
                    if (intDay > maxDay) {
                        formattedDate.replace(8, 10, String.valueOf(maxDay));
                    }
                }
            }
        }
        return formattedDate.toString();
    }

    // Helper method to check for leap year
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}
