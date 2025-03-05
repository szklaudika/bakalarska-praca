package com.example.zapisnik;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FlightDetailFragment extends Fragment {

    private static final String TAG = "FlightDetailFragment";

    public FlightDetailFragment() {
        // Empty constructor for fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        // Get the arguments passed from the FlightListFragment
        Bundle args = getArguments();
        if (args != null) {
            // Retrieve all the data from the bundle
            String date = args.getString("date");
            String departure = args.getString("departure");
            String departureTime = args.getString("departureTime");
            String arrival = args.getString("arrival");
            String arrivalTime = args.getString("arrivalTime");
            String aircraftModel = args.getString("aircraftModel");
            String registration = args.getString("registration");
            int singlePilotTime = args.getInt("singlePilotTime");
            int multiPilotTime = args.getInt("multiPilotTime");
            int totalFlightTime = args.getInt("totalFlightTime");
            String pilotName = args.getString("pilotName");
            int landings = args.getInt("landings");
            int nightTime = args.getInt("nightTime");
            int ifrTime = args.getInt("ifrTime");
            int picTime = args.getInt("picTime");
            int copilotTime = args.getInt("copilotTime");
            int dualTime = args.getInt("dualTime");
            int instructorTime = args.getInt("instructorTime");
            String fstdDate = args.getString("fstdDate");
            String fstdType = args.getString("fstdType");
            int fstdTotalTime = args.getInt("fstdTotalTime");
            String remarks = args.getString("remarks");

            // Log the received data for debugging
            Log.d(TAG, "Date: " + date);
            Log.d(TAG, "Departure: " + departure);
            Log.d(TAG, "Departure Time: " + departureTime);
            Log.d(TAG, "Arrival: " + arrival);
            Log.d(TAG, "Arrival Time: " + arrivalTime);
            Log.d(TAG, "Aircraft Model: " + aircraftModel);
            Log.d(TAG, "Registration: " + registration);
            Log.d(TAG, "Single Pilot Time: " + singlePilotTime + " min");
            Log.d(TAG, "Multi Pilot Time: " + multiPilotTime + " min");
            Log.d(TAG, "Total Flight Time: " + totalFlightTime + " min");
            Log.d(TAG, "Pilot Name: " + pilotName);
            Log.d(TAG, "Landings: " + landings);
            Log.d(TAG, "Night Time: " + nightTime + " min");
            Log.d(TAG, "IFR Time: " + ifrTime + " min");
            Log.d(TAG, "PIC Time: " + picTime + " min");
            Log.d(TAG, "Copilot Time: " + copilotTime + " min");
            Log.d(TAG, "Dual Time: " + dualTime + " min");
            Log.d(TAG, "Instructor Time: " + instructorTime + " min");
            Log.d(TAG, "FSTD Date: " + fstdDate);
            Log.d(TAG, "FSTD Type: " + fstdType);
            Log.d(TAG, "FSTD Total Time: " + fstdTotalTime + " min");
            Log.d(TAG, "Remarks: " + remarks);

            // Set all the data into the TextViews
            ((TextView) view.findViewById(R.id.tv_flight_date)).setText("Date: " + date);
            ((TextView) view.findViewById(R.id.tv_flight_departure)).setText("From: " + departure);
            ((TextView) view.findViewById(R.id.tv_flight_departure_time)).setText("Departure Time: " + departureTime);
            ((TextView) view.findViewById(R.id.tv_flight_arrival)).setText("To: " + arrival);
            ((TextView) view.findViewById(R.id.tv_flight_arrival_time)).setText("Arrival Time: " + arrivalTime);
            ((TextView) view.findViewById(R.id.tv_flight_aircraft_model)).setText("Aircraft Model: " + aircraftModel);
            ((TextView) view.findViewById(R.id.tv_flight_registration)).setText("Registration: " + registration);
            ((TextView) view.findViewById(R.id.tv_flight_single_pilot_time)).setText("Single Pilot Time: " + singlePilotTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_multi_pilot_time)).setText("Multi Pilot Time: " + multiPilotTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_total_flight_time)).setText("Total Flight Time: " + totalFlightTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_pilot_name)).setText("Pilot Name: " + pilotName);
            ((TextView) view.findViewById(R.id.tv_flight_landings)).setText("Landings: " + landings);
            ((TextView) view.findViewById(R.id.tv_flight_night_time)).setText("Night Time: " + nightTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_ifr_time)).setText("IFR Time: " + ifrTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_pic_time)).setText("PIC Time: " + picTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_copilot_time)).setText("Copilot Time: " + copilotTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_dual_time)).setText("Dual Time: " + dualTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_instructor_time)).setText("Instructor Time: " + instructorTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_fstd_date)).setText("FSTD Date: " + fstdDate);
            ((TextView) view.findViewById(R.id.tv_flight_fstd_type)).setText("FSTD Type: " + fstdType);
            ((TextView) view.findViewById(R.id.tv_flight_fstd_total_time)).setText("FSTD Total Time: " + fstdTotalTime + " min");
            ((TextView) view.findViewById(R.id.tv_flight_remarks)).setText("Remarks: " + remarks);
        }

        return view;
    }
}
