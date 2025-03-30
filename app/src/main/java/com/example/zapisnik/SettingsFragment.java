package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Switch darkModeSwitch;
    private LinearLayout layoutLanguage, layoutLogout;
    private TextView textSelectedLanguage;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the updated fragment_settings layout
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find views for the "Dark Mode" row
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode);

        // Find views for the "Language" row
        layoutLanguage = view.findViewById(R.id.layout_language);
        textSelectedLanguage = view.findViewById(R.id.text_selected_language);

        // Find views for the "Logout" row
        layoutLogout = view.findViewById(R.id.layout_logout);

        // Retrieve stored preference
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // 1) Dark Mode Switch: interpret "dark_mode" preference as:
        //    true = Light mode (Theme.Zapisnik.Lightmode)
        //    false = Dark mode (Theme.Zapisnik)
        boolean isLightMode = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isLightMode);

        // When toggled, save the preference and recreate the activity so the new theme is applied
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            getActivity().recreate();
        });

        // 2) Language Row: Show current language, and toggle on click (simple example)
        String language = prefs.getString("app_language", "en");
        if (language.equals("en")) {
            textSelectedLanguage.setText("English");
        } else {
            textSelectedLanguage.setText("Slovak");
        }

        layoutLanguage.setOnClickListener(v -> {
            // Toggle language as a simple demonstration
            if (textSelectedLanguage.getText().toString().equals("English")) {
                setLocale("sk");
            } else {
                setLocale("en");
            }
        });

        // 3) Logout Row: Clear preferences and redirect to LoginFragment
        layoutLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Helper method to change the app language and recreate the activity.
     */
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        // Save language preference
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        // Restart the activity to apply changes
        getActivity().recreate();
    }
}
