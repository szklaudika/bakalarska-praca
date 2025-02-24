package com.example.zapisnik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CertificatesFragment extends Fragment {

    private EditText etCertificateName, etExpiryDate;
    private Button btnAddCertificate;
    private CertificateDatabase database;

    public CertificatesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificates, container, false);

        etCertificateName = view.findViewById(R.id.et_certificate_name);
        etExpiryDate = view.findViewById(R.id.et_expiry_date);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        // Inicializácia databázy
        database = CertificateDatabase.getInstance(getActivity());

        btnAddCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCertificate();
            }
        });

        return view;
    }

    private void addCertificate() {
        String certificateName = etCertificateName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (certificateName.isEmpty() || expiryDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Uloženie certifikátu do DB
        Certificate newCertificate = new Certificate(certificateName, expiryDate);
        database.certificateDao().insertCertificate(newCertificate);

        Toast.makeText(getActivity(), "Certificate Added!", Toast.LENGTH_SHORT).show();

        // Vyčistenie polí
        etCertificateName.setText("");
        etExpiryDate.setText("");
    }
}
