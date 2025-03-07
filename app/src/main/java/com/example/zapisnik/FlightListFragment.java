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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightListFragment extends Fragment {

    private static final String TAG = "FlightListFragment";
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Flight> flights = new ArrayList<>();

    public FlightListFragment() {
        // Empty constructor for fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);

        listView = view.findViewById(R.id.list_view_flights);

        // Fetch flight data from server
        fetchFlightsFromServer();

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

    // Fetch flight data from the server using Volley
    private void fetchFlightsFromServer() {
        String url = "http://10.0.2.2/zapisnik_db/get_flights.php"; // Replace with your actual endpoint URL

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        flights.clear(); // Clear the previous data
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject flightJson = response.getJSONObject(i);
                                Flight flight = new Flight(
                                        flightJson.getString("date"),
                                        flightJson.getString("departure_place"), // Corrected to match the JSON key
                                        flightJson.getString("departure_time"), // Corrected to match the JSON key
                                        flightJson.getString("arrival_place"), // Corrected to match the JSON key
                                        flightJson.getString("arrival_time"), // Corrected to match the JSON key
                                        flightJson.getString("aircraft_model"), // Corrected to match the JSON key
                                        flightJson.getString("registration"), // Corrected to match the JSON key
                                        flightJson.getInt("single_pilot_time"),
                                        flightJson.getInt("multi_pilot_time"),
                                        flightJson.getInt("total_flight_time"),
                                        flightJson.getString("pilot_name"), // Corrected to match the JSON key
                                        flightJson.getInt("landings"),
                                        flightJson.getInt("night_time"),
                                        flightJson.getInt("ifr_time"),
                                        flightJson.getInt("pic_time"),
                                        flightJson.getInt("copilot_time"),
                                        flightJson.getInt("dual_time"),
                                        flightJson.getInt("instructor_time"),
                                        flightJson.getString("fstd_date"),
                                        flightJson.getString("fstd_type"),
                                        flightJson.getInt("fstd_total_time"),
                                        flightJson.getString("remarks") // Corrected to match the JSON key
                                );
                                flights.add(flight);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // After getting the data, update the ListView
                        updateListView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching flights: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    // Update ListView with fetched data
    private void updateListView() {
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                flights.stream().map(f -> f.getDate() + " | " + f.getDeparturePlace() + " -> " + f.getArrivalPlace() + " | " + f.getTotalFlightTime() + " min").collect(Collectors.toList()));

        listView.setAdapter(adapter);
    }
}
