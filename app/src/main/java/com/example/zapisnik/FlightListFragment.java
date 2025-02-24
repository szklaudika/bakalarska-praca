package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class FlightListFragment extends Fragment {

    public FlightListFragment() {
        // Prázdny konštruktor pre fragmenty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView listView = view.findViewById(R.id.list_view_flights);
        List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                flights.stream().map(f -> f.getDepartureLocation() + " -> " + f.getArrivalLocation()).collect(Collectors.toList()));

        listView.setAdapter(adapter);

        return view;
    }

}

