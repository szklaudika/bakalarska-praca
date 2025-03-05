package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;
import java.util.stream.Collectors;

public class FlightListFragment extends Fragment {

    private static final String TAG = "FlightListFragment";

    public FlightListFragment() {
        // Empty constructor for fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ListView listView = view.findViewById(R.id.list_view_flights);
        List<Flight> flights = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                flights.stream().map(f -> f.getDate() + " | " + f.getDeparturePlace() + " -> " + f.getArrivalPlace() + " | " + f.getTotalFlightTime() + " min").collect(Collectors.toList()));

        listView.setAdapter(adapter);

        // Log the list of flights
        Log.d(TAG, "Loaded " + flights.size() + " flights from the database.");
        for (Flight flight : flights) {
            Log.d(TAG, "Flight: " + flight.getDate() + " | " + flight.getDeparturePlace() + " -> " + flight.getArrivalPlace() + " | " + flight.getTotalFlightTime() + " min");
        }

        // Set click listener on list item
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Flight selectedFlight = flights.get(position);

            // Log the selected flight details
            Log.d(TAG, "Selected Flight: " + selectedFlight.getDate() + " | " + selectedFlight.getDeparturePlace() + " -> " + selectedFlight.getArrivalPlace());

            // Create a new fragment to show flight details
            FlightDetailFragment detailFragment = new FlightDetailFragment();

            // Pass all the data to the new fragment
            Bundle args = new Bundle();
            args.putString("date", selectedFlight.getDate());
            args.putString("departure", selectedFlight.getDeparturePlace());
            args.putString("arrival", selectedFlight.getArrivalPlace());
            args.putString("departureTime", selectedFlight.getDepartureTime());
            args.putString("arrivalTime", selectedFlight.getArrivalTime());
            args.putString("aircraftModel", selectedFlight.getAircraftModel());
            args.putString("registration", selectedFlight.getRegistration());
            args.putInt("singlePilotTime", selectedFlight.getSinglePilotTime());
            args.putInt("multiPilotTime", selectedFlight.getMultiPilotTime());
            args.putInt("totalFlightTime", selectedFlight.getTotalFlightTime());
            args.putString("pilotName", selectedFlight.getPilotName());
            args.putInt("landings", selectedFlight.getLandings());
            args.putInt("nightTime", selectedFlight.getNightTime());
            args.putInt("ifrTime", selectedFlight.getIfrTime());
            args.putInt("picTime", selectedFlight.getPicTime());
            args.putInt("copilotTime", selectedFlight.getCopilotTime());
            args.putInt("dualTime", selectedFlight.getDualTime());
            args.putInt("instructorTime", selectedFlight.getInstructorTime());
            args.putString("fstdDate", selectedFlight.getFstdDate());
            args.putString("fstdType", selectedFlight.getFstdType());
            args.putInt("fstdTotalTime", selectedFlight.getFstdTotalTime());
            args.putString("remarks", selectedFlight.getRemarks());

            detailFragment.setArguments(args);

            // Replace the fragment in the main layout
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
