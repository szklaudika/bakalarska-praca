package com.example.zapisnik;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.appcompat.widget.AppCompatTextView;

public class SettingsFragment extends Fragment {

    private Switch darkModeSwitch;
    private LinearLayout layoutLogout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        layoutLogout = view.findViewById(R.id.layout_logout);

        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isLightMode = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isLightMode);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            getActivity().recreate();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        layoutLogout.setOnClickListener(v -> {
            SharedPreferences userPrefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPrefs.edit();
            userEditor.clear();
            userEditor.apply();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new LoginFragment())
                    .commit();
        });

        return view;
    }
}
