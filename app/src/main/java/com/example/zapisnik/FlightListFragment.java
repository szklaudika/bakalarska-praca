package com.example.zapisnik;

import android.content.Context;
import android.content.SharedPreferences;
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
        // Inflate your main layout (which might just be a ListView)
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);
        listView = view.findViewById(R.id.list_view_flights);

        // Inflate the header view from list_header.xml and add it to ListView
        View headerView = inflater.inflate(R.layout.list_header, listView, false);
        listView.addHeaderView(headerView);

        // Load local flights
        loadFlightsFromDatabase();

        // Fetch flights from the server
        fetchFlightsFromServer();

        // Handle item clicks – adjust index because of header view
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Subtract header count from position
            int adjustedPosition = position - listView.getHeaderViewsCount();
            if (adjustedPosition >= 0 && adjustedPosition < flights.size()) {
                Flight selectedFlight = flights.get(adjustedPosition);
                FlightDetailFragment detailFragment = new FlightDetailFragment();

                Bundle args = new Bundle();
                args.putString("date", selectedFlight.getDate());
                args.putString("departure", selectedFlight.getDeparturePlace());
                args.putString("departureTime", selectedFlight.getDepartureTime());
                args.putString("arrival", selectedFlight.getArrivalPlace());
                args.putString("arrivalTime", selectedFlight.getArrivalTime());
                args.putString("aircraftModel", selectedFlight.getAircraftModel());
                args.putString("registration", selectedFlight.getRegistration());
                // singlePilotTime is a primitive int, so it's safe
                args.putInt("singlePilotTime", selectedFlight.getSinglePilotTime());
                // For nullable Integer fields, use a null-check with a default value of 0.
                args.putInt("multiPilotTime", selectedFlight.getMultiPilotTime() == null ? 0 : selectedFlight.getMultiPilotTime());
                args.putInt("totalFlightTime", selectedFlight.getTotalFlightTime());
                args.putString("pilotName", selectedFlight.getPilotName());
                args.putBoolean("singlePilot", selectedFlight.isSinglePilot());
                args.putInt("landingsDay", selectedFlight.getLandingsDay() == null ? 0 : selectedFlight.getLandingsDay());
                args.putInt("landingsNight", selectedFlight.getLandingsNight() == null ? 0 : selectedFlight.getLandingsNight());
                args.putInt("nightTime", selectedFlight.getNightTime() == null ? 0 : selectedFlight.getNightTime());
                args.putInt("ifrTime", selectedFlight.getIfrTime() == null ? 0 : selectedFlight.getIfrTime());
                args.putInt("picTime", selectedFlight.getPicTime() == null ? 0 : selectedFlight.getPicTime());
                args.putInt("copilotTime", selectedFlight.getCopilotTime() == null ? 0 : selectedFlight.getCopilotTime());
                args.putInt("dualTime", selectedFlight.getDualTime() == null ? 0 : selectedFlight.getDualTime());
                args.putInt("instructorTime", selectedFlight.getInstructorTime() == null ? 0 : selectedFlight.getInstructorTime());
                args.putString("fstdDate", selectedFlight.getFstdDate());
                args.putString("fstdType", selectedFlight.getFstdType());
                args.putInt("fstdTotalTime", selectedFlight.getFstdTotalTime() == null ? 0 : selectedFlight.getFstdTotalTime());
                args.putString("remarks", selectedFlight.getRemarks());

                detailFragment.setArguments(args);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, detailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    private void loadFlightsFromDatabase() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        List<Flight> flightsFromDb = FlightDatabase.getInstance(getActivity())
                .flightDao().getFlightsByUserId(userId);
        flights.clear();
        flights.addAll(flightsFromDb);
        updateListView();
    }


    private void fetchFlightsFromServer() {
        // Retrieve the logged-in user's id
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);

        // Append the user_id parameter to the URL
        String url = "http://10.0.2.2/zapisnik_db/get_flights.php?user_id=" + userId;

        assert getActivity() != null;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response from server: " + response.toString());
                    // Instead of clearing the in-memory flights list, update the local DB with new server data.
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject flightJson = response.getJSONObject(i);
                            Log.d(TAG, "Flight data: " + flightJson.toString());

                            String date = flightJson.optString("date", "Unknown Date");
                            String departurePlace = flightJson.optString("departure_place", "Unknown Departure Place");
                            String departureTime = flightJson.optString("departure_time", "Unknown Departure Time");
                            String arrivalPlace = flightJson.optString("arrival_place", "Unknown Arrival Place");
                            String arrivalTime = flightJson.optString("arrival_time", "Unknown Arrival Time");
                            String aircraftModel = flightJson.optString("aircraft_model", "Unknown Aircraft Model");
                            String registration = flightJson.optString("registration", "Unknown Registration");
                            int singlePilotTime = flightJson.optInt("single_pilot_time", 0);
                            int multiPilotTime = flightJson.optInt("multiPilotTime", 0);
                            int totalFlightTime = flightJson.optInt("total_flight_time", 0);
                            String pilotName = flightJson.optString("pilot_name", "Unknown Pilot");
                            boolean singlePilot = flightJson.optBoolean("single_pilot", false);
                            int landingsDay = flightJson.optInt("landings_day", 0);
                            int landingsNight = flightJson.optInt("landings_night", 0);
                            int nightTime = flightJson.optInt("night_time", 0);
                            int ifrTime = flightJson.optInt("ifr_time", 0);
                            int picTime = flightJson.optInt("pic_time", 0);
                            int copilotTime = flightJson.optInt("copilot_time", 0);
                            int dualTime = flightJson.optInt("dual_time", 0);
                            int instructorTime = flightJson.optInt("instructor_time", 0);
                            String fstdDate = flightJson.optString("fstd_date", "Unknown Date");
                            String fstdType = flightJson.optString("fstd_type", "Unknown Type");
                            int fstdTotalTime = flightJson.optInt("fstd_total_time", 0);
                            String remarks = flightJson.optString("remarks", "No Remarks");

                            Flight flight = new Flight(
                                    date,
                                    departurePlace,
                                    departureTime,
                                    arrivalPlace,
                                    arrivalTime,
                                    aircraftModel,
                                    registration,
                                    singlePilotTime,
                                    multiPilotTime,
                                    totalFlightTime,
                                    pilotName,
                                    singlePilot,
                                    landingsDay,
                                    landingsNight,
                                    nightTime,
                                    ifrTime,
                                    picTime,
                                    copilotTime,
                                    dualTime,
                                    instructorTime,
                                    fstdDate,
                                    fstdType,
                                    fstdTotalTime,
                                    remarks
                            );
                            // Update the local DB only if this flight doesn't already exist.
                            Executors.newSingleThreadExecutor().execute(() -> {
                                FlightDatabase db = FlightDatabase.getInstance(getActivity());
                                List<Flight> existing = db.flightDao().getFlightByUnique(date, departurePlace, arrivalPlace, pilotName);
                                if (existing == null || existing.isEmpty()) {
                                    db.flightDao().insert(flight);
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }
                    // Refresh the UI by re-loading all flights from the local database.
                    loadFlightsFromDatabase();
                },
                error -> Log.e(TAG, "Error fetching flights: " + error.getMessage())
        );

        requestQueue.add(jsonArrayRequest);
    }




    /**
     * Aktualizácia ListView.
     *
     * Možnosti:
     * 1) Použitie vlastného FlightListAdapter-a s CardView, ktorý zobrazí informácie o lete s prispôsobeným dizajnom a šípkou.
     * 2) Alternatívne, jednoduchý ArrayAdapter, ktorý zobrazuje text (zakomentované nižšie).
     */
    private void updateListView() {
        // Použitie vlastného adaptera s vlastným layoutom:
        FlightListAdapter adapter = new FlightListAdapter(
                getActivity(),
                R.layout.list_item_flight,
                flights
        );
        listView.setAdapter(adapter);

        // Alternatívna implementácia pomocou ArrayAdapter (jednoduchý textový zoznam):
        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                flights.stream()
                        .map(f -> f.getDate() + " | " + f.getDeparturePlace() + " -> " + f.getArrivalPlace() +
                                " | " + f.getAircraftModel() + " | " + f.getPilotName() + " | " +
                                f.getTotalFlightTime() + " min")
                        .collect(Collectors.toList()));
        listView.setAdapter(adapter);
        */
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            SharedPreferences syncPrefs = getActivity().getSharedPreferences("SyncPrefs", Context.MODE_PRIVATE);
            boolean justSynced = syncPrefs.getBoolean("justSynced", false);
            if (!justSynced) {
                syncOfflineFlights();
            } else {
                // Clear the flag so future resumes can sync if needed
                syncPrefs.edit().remove("justSynced").apply();
            }
        }
    }


    private boolean isNetworkAvailable() {
        assert getActivity() != null;
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager)
                getActivity().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void syncOfflineFlights() {
        Executors.newSingleThreadExecutor().execute(() -> {
            FlightDatabase flightDb = FlightDatabase.getInstance(getActivity());
            List<Flight> unsyncedFlights = flightDb.flightDao().getUnsyncedOfflineFlights();
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
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Offline flights synced!", Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Sync failed: " + t.getMessage());
            }
        });
    }
}
