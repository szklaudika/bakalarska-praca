package com.example.zapisnik;

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

    private EditText etDepartureLocation, etArrivalLocation, etFlightDate, etFlightTime, etFlightDuration, etFlightType;
    private Button btnAddFlight;

    public AddFlightFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_flight, container, false);

        etDepartureLocation = view.findViewById(R.id.et_departure_location);
        etArrivalLocation = view.findViewById(R.id.et_arrival_location);
        etFlightDate = view.findViewById(R.id.et_flight_date);
        etFlightTime = view.findViewById(R.id.et_flight_time);
        etFlightDuration = view.findViewById(R.id.et_flight_duration);
        etFlightType = view.findViewById(R.id.et_flight_type);
        btnAddFlight = view.findViewById(R.id.btn_add_flight);

        btnAddFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFlight();
            }
        });

        return view;
    }

    private void addFlight() {
        String departureLocation = etDepartureLocation.getText().toString().trim();
        String arrivalLocation = etArrivalLocation.getText().toString().trim();
        String flightDate = etFlightDate.getText().toString().trim();
        String flightTime = etFlightTime.getText().toString().trim();
        String flightDuration = etFlightDuration.getText().toString().trim();
        String flightType = etFlightType.getText().toString().trim();

        // Validation
        if (departureLocation.isEmpty() || arrivalLocation.isEmpty() || flightDate.isEmpty() ||
                flightTime.isEmpty() || flightDuration.isEmpty() || flightType.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Uloženie do databázy
        Flight flight = new Flight(departureLocation, arrivalLocation, flightDate, flightTime, flightDuration, flightType);
        FlightDatabase.getInstance(getActivity()).flightDao().insertFlight(flight);

        Toast.makeText(getActivity(), "Flight added!", Toast.LENGTH_SHORT).show();

        // Vyčistenie polí
        etDepartureLocation.setText("");
        etArrivalLocation.setText("");
        etFlightDate.setText("");
        etFlightTime.setText("");
        etFlightDuration.setText("");
        etFlightType.setText("");
    }

}
