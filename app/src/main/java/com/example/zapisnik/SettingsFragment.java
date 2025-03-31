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
import android.widget.TextView;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Switch darkModeSwitch;
    private LinearLayout layoutLanguage, layoutLogout;
    private TextView textSelectedLanguage;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        layoutLanguage = view.findViewById(R.id.layout_language);
        textSelectedLanguage = view.findViewById(R.id.text_selected_language);
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

        String language = prefs.getString("app_language", "en");
        textSelectedLanguage.setText(language.equals("en") ? "English" : "Slovak");

        layoutLanguage.setOnClickListener(v -> {
            String newLang = language.equals("en") ? "sk" : "en";
            setLocale(newLang);
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

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        getActivity().recreate();
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
