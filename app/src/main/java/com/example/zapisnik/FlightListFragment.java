package com.example.zapisnik;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightListFragment extends Fragment {

    private static final String TAG = "FlightListFragment";
    private ListView listView;
    private final List<Flight> flights = new ArrayList<>();

    public FlightListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);
        listView = view.findViewById(R.id.list_view_flights);

        // Load local flights
        loadFlightsFromDatabase();

        // Fetch flights from the server
        fetchFlightsFromServer();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Flight selectedFlight = flights.get(position);
            FlightDetailFragment detailFragment = new FlightDetailFragment();

            // Pass flight details
            Bundle args = new Bundle();
            args.putString("date", selectedFlight.getDate());
            args.putString("departure", selectedFlight.getDeparturePlace());
            args.putString("departureTime", selectedFlight.getDepartureTime());
            args.putString("arrival", selectedFlight.getArrivalPlace());
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

            // Replace fragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        return view;
    }

    private void loadFlightsFromDatabase() {
        List<Flight> flightsFromDb = FlightDatabase.getInstance(getActivity()).flightDao().getAllFlights();
        flights.addAll(flightsFromDb);
        updateListView();
    }

    private void fetchFlightsFromServer() {
        String url = "http://10.0.2.2/zapisnik_db/get_flights.php";
        assert getActivity() != null;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response from server: " + response.toString());  // Log the raw server response

                    flights.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject flightJson = response.getJSONObject(i);

                            // Log the individual flight data for debugging
                            Log.d(TAG, "Flight data: " + flightJson.toString());

                            // Use the correct field names as per the server response (lowercase)
                            String departurePlace = flightJson.optString("departure_place", "Unknown Departure Place");
                            String departureTime = flightJson.optString("departure_time", "Unknown Departure Time");

                            // Log missing fields
                            if (departurePlace.equals("Unknown Departure Place")) {
                                Log.e(TAG, "departurePlace is missing or empty for this flight");
                            }
                            if (departureTime.equals("Unknown Departure Time")) {
                                Log.e(TAG, "departureTime is missing or empty for this flight");
                            }

                            Flight flight = new Flight(
                                    flightJson.getString("date"),
                                    departurePlace,
                                    departureTime,
                                    flightJson.optString("arrival_place", "Unknown Arrival Place"),
                                    flightJson.optString("arrival_time", "Unknown Arrival Time"),
                                    flightJson.optString("aircraft_model", "Unknown Aircraft Model"),
                                    flightJson.optString("registration", "Unknown Registration"),
                                    flightJson.optInt("single_pilot_time", 0),
                                    flightJson.optInt("multi_pilot_time", 0),
                                    flightJson.optInt("total_flight_time", 0),
                                    flightJson.optString("pilot_name", "Unknown Pilot"),
                                    flightJson.optInt("landings", 0),
                                    flightJson.optInt("night_time", 0),
                                    flightJson.optInt("ifr_time", 0),
                                    flightJson.optInt("pic_time", 0),
                                    flightJson.optInt("copilot_time", 0),
                                    flightJson.optInt("dual_time", 0),
                                    flightJson.optInt("instructor_time", 0),
                                    flightJson.optString("fstd_date", "Unknown Date"),
                                    flightJson.optString("fstd_type", "Unknown Type"),
                                    flightJson.optInt("fstd_total_time", 0),
                                    flightJson.optString("remarks", "No Remarks")
                            );
                            flights.add(flight);

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }
                    updateListView();
                },
                error -> Log.e(TAG, "Error fetching flights: " + error.getMessage())
        );

        requestQueue.add(jsonArrayRequest);
    }

        private void updateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                flights.stream()
                        .map(f -> f.getDate() + " | " + f.getDeparturePlace() + " -> " + f.getArrivalPlace() +
                                " | " + f.getAircraftModel() + " | " + f.getPilotName() + " | " +
                                f.getTotalFlightTime() + " min")
                        .collect(Collectors.toList()));

        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            syncOfflineFlights();
        }
    }

    private boolean isNetworkAvailable() {
        assert getActivity() != null;
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getActivity().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void syncOfflineFlights() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Flight> unsyncedFlights = FlightDatabase.getInstance(getActivity()).flightDao().getUnsyncedFlights();
            for (Flight flight : unsyncedFlights) {
                sendFlightToServer(flight);
            }
        });
    }

    private void sendFlightToServer(Flight flight) {
        FlightApi flightApi = RetrofitClient.getFlightApi();

        flightApi.addFlight(flight).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() ->
                            FlightDatabase.getInstance(getActivity()).flightDao().markAsSynced(flight.getId()));

                    assert getActivity() != null;
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Offline flights synced!", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Sync failed: " + t.getMessage());
            }
        });
    }
}
