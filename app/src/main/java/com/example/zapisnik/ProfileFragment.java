package com.example.zapisnik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private CertificateDatabase database;
    private ListView listViewCertificates;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        listViewCertificates = view.findViewById(R.id.list_view_certificates);

        // Inicializácia databázy
        database = CertificateDatabase.getInstance(getActivity());

        // Načítanie certifikátov z DB
        loadCertificates();

        return view;
    }

    private void loadCertificates() {
        List<Certificate> certificates = database.certificateDao().getAllCertificates();
        List<String> certificateList = new ArrayList<>();

        for (Certificate cert : certificates) {
            certificateList.add(cert.getName() + " - Expires: " + cert.getExpiryDate());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, certificateList);
        listViewCertificates.setAdapter(adapter);
    }
}
