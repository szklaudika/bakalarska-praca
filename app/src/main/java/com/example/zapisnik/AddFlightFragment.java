package com.example.zapisnik;

import android.annotation.SuppressLint;
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

public class AddFlightFragment extends Fragment {

    private EditText etDate, etDeparturePlace, etDepartureTime, etArrivalPlace, etArrivalTime, etAircraftModel, etRegistration,
            etSinglePilotTime, etMultiPilotTime, etTotalFlightTime, etPilotName, etLandings, etNightTime,
            etIfrTime, etPicTime, etCopilotTime, etDualTime, etInstructorTime, etFstdDate, etFstdType,
            etFstdTotalTime, etRemarks;
    private Button btnAddFlight;

    public AddFlightFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flight, container, false);

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

        FlightDatabase.getInstance(getActivity()).flightDao().insertFlight(flight);
        Toast.makeText(getActivity(), "Flight added!", Toast.LENGTH_SHORT).show();
    }
}
