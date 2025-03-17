package com.example.zapisnik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CertificatesFragment extends Fragment {

    private Button btnPlatformVelkaEra, btnPlatformUltralighty, btnPlatformVetrone, btnPlatformVrtulniky;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout defined in fragment_certificates.xml
        View view = inflater.inflate(R.layout.fragment_certificates, container, false);

        // Initialize the buttons using their IDs
        btnPlatformVelkaEra = view.findViewById(R.id.btn_platform_velka_era);
        btnPlatformUltralighty = view.findViewById(R.id.btn_platform_ultralighty);
        btnPlatformVetrone = view.findViewById(R.id.btn_platform_vetrone);
        btnPlatformVrtulniky = view.findViewById(R.id.btn_platform_vrtulniky);

        // Set click listeners
        btnPlatformVelkaEra.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new AddVelkaEraFragment())
                    .addToBackStack(null)
                    .commit();
        });


        // You can implement similar listeners for the other buttons when their fragments are created.
        btnPlatformUltralighty.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new AddUltralightyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnPlatformVetrone.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new AddVetroneFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnPlatformVrtulniky.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new AddVrtulníkyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


}
