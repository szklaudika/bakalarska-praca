package com.example.zapisnik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FlightListAdapter extends ArrayAdapter<Flight> {

    private final LayoutInflater inflater;
    private final int resourceLayout;
    private final List<Flight> flights;

    public FlightListAdapter(Context context, int resource, List<Flight> flights) {
        super(context, resource, flights);
        this.inflater = LayoutInflater.from(context);
        this.resourceLayout = resource;
        this.flights = flights;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate row layout if needed
        if (convertView == null) {
            convertView = inflater.inflate(resourceLayout, parent, false);
        }

        // Get current Flight object
        Flight flight = flights.get(position);

        // Find views
        TextView tvDeparture = convertView.findViewById(R.id.tvDeparture);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        ImageView imgArrow = convertView.findViewById(R.id.imgArrow);

        // Set the date (bold, from flight.getDate())
        tvDeparture.setText(flight.getDeparturePlace() + " -> " + flight.getArrivalPlace());

        // Build the short description
        String description = flight.getDate()
                + "\n" + flight.getAircraftModel()
                + "\n" + flight.getPilotName()
                + "\n" + flight.getTotalFlightTime() + " min";
        tvDescription.setText(description);


        return convertView;
    }
}
