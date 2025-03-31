package com.example.zapisnik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVelkaEraFragment extends Fragment {

    // Letecké kvalifikácie section
    private CheckBox chkPpl, chkCpl, chkAtpl, chkSepLand, chkSepSea, chkMepLand, chkMepSea,
            chkIrSep, chkIr, chkMep, chkNight, chkFi, chkIri, chkTow, chkTst, chkPar, chkAcr, chkMcc;
    private EditText etExpiryPpl, etExpiryCpl, etExpiryAtpl, etExpirySepLand, etExpirySepSea,
            etExpiryMepLand, etExpiryMepSea, etExpiryIrSep, etExpiryIr, etExpiryMep, etExpiryNight,
            etExpiryFi, etExpiryIri, etExpiryTow, etExpiryTst, etExpiryPar, etExpiryAcr, etExpiryMcc;

    // Medical section
    private CheckBox chkMedClass1, chkMedClass2;
    private EditText etExpiryMedClass1, etExpiryMedClass2;

    // Rádio section
    private CheckBox chkVseobecnyPreukaz, chkObmedzenyPreukaz;
    private EditText etExpiryVseobecnyPreukaz, etExpiryObmedzenyPreukaz;

    // Angličtina section
    private CheckBox chkEnglishLevel4, chkEnglishLevel5, chkEnglishLevel6, chkEnglishIfr;
    private EditText etExpiryEnglishLevel4, etExpiryEnglishLevel5, etExpiryEnglishLevel6, etExpiryEnglishIfr;

    // Poistka section
    private CheckBox chkPoistenieZodpovednostCRSK, chkZodpovednostiSvet;
    private EditText etExpiryPoistkaCRSK, etExpiryPoistkaSvet;

    // Note field and Add button
    private EditText etNote;
    private Button btnAddCertificate;

    // Database instance
    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_velka_era, container, false);

        // Initialize all UI components
        // Letecké kvalifikácie
        chkPpl = view.findViewById(R.id.chk_ppl);
        etExpiryPpl = view.findViewById(R.id.et_expiry_ppl);
        chkCpl = view.findViewById(R.id.chk_cpl);
        etExpiryCpl = view.findViewById(R.id.et_expiry_cpl);
        chkAtpl = view.findViewById(R.id.chk_atpl);
        etExpiryAtpl = view.findViewById(R.id.et_expiry_atpl);
        chkSepLand = view.findViewById(R.id.chk_sep_land);
        etExpirySepLand = view.findViewById(R.id.et_expiry_sep_land);
        chkSepSea = view.findViewById(R.id.chk_sep_sea);
        etExpirySepSea = view.findViewById(R.id.et_expiry_sep_sea);
        chkMepLand = view.findViewById(R.id.chk_mep_land);
        etExpiryMepLand = view.findViewById(R.id.et_expiry_mep_land);
        chkMepSea = view.findViewById(R.id.chk_mep_sea);
        etExpiryMepSea = view.findViewById(R.id.et_expiry_mep_sea);
        chkIrSep = view.findViewById(R.id.chk_ir_sep);
        etExpiryIrSep = view.findViewById(R.id.et_expiry_ir_sep);
        chkIr = view.findViewById(R.id.chk_ir);
        etExpiryIr = view.findViewById(R.id.et_expiry_ir);
        chkMep = view.findViewById(R.id.chk_mep);
        etExpiryMep = view.findViewById(R.id.et_expiry_mep);
        chkNight = view.findViewById(R.id.chk_night);
        etExpiryNight = view.findViewById(R.id.et_expiry_night);
        chkFi = view.findViewById(R.id.chk_fi);
        etExpiryFi = view.findViewById(R.id.et_expiry_fi);
        chkIri = view.findViewById(R.id.chk_iri);
        etExpiryIri = view.findViewById(R.id.et_expiry_iri);
        chkTow = view.findViewById(R.id.chk_tow);
        etExpiryTow = view.findViewById(R.id.et_expiry_tow);
        chkTst = view.findViewById(R.id.chk_tst);
        etExpiryTst = view.findViewById(R.id.et_expiry_tst);
        chkPar = view.findViewById(R.id.chk_par);
        etExpiryPar = view.findViewById(R.id.et_expiry_par);
        chkAcr = view.findViewById(R.id.chk_acr);
        etExpiryAcr = view.findViewById(R.id.et_expiry_acr);
        chkMcc = view.findViewById(R.id.chk_mcc);
        etExpiryMcc = view.findViewById(R.id.et_expiry_mcc);

        // Medical
        chkMedClass1 = view.findViewById(R.id.chk_med_class1);
        etExpiryMedClass1 = view.findViewById(R.id.et_expiry_med_class1);
        chkMedClass2 = view.findViewById(R.id.chk_med_class2);
        etExpiryMedClass2 = view.findViewById(R.id.et_expiry_med_class2);

        // Rádio
        chkVseobecnyPreukaz = view.findViewById(R.id.chk_vseobecny_preukaz);
        etExpiryVseobecnyPreukaz = view.findViewById(R.id.et_expiry_vseobecny_preukaz);
        chkObmedzenyPreukaz = view.findViewById(R.id.chk_obmedzeny_preukaz);
        etExpiryObmedzenyPreukaz = view.findViewById(R.id.et_expiry_obmedzeny_preukaz);

        // Angličtina
        chkEnglishLevel4 = view.findViewById(R.id.chk_english_level4);
        etExpiryEnglishLevel4 = view.findViewById(R.id.et_expiry_english_level4);
        chkEnglishLevel5 = view.findViewById(R.id.chk_english_level5);
        etExpiryEnglishLevel5 = view.findViewById(R.id.et_expiry_english_level5);
        chkEnglishLevel6 = view.findViewById(R.id.chk_english_level6);
        etExpiryEnglishLevel6 = view.findViewById(R.id.et_expiry_english_level6);
        chkEnglishIfr = view.findViewById(R.id.chk_english_ifr);
        etExpiryEnglishIfr = view.findViewById(R.id.et_expiry_english_ifr);

        // Poistka
        chkPoistenieZodpovednostCRSK = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk);
        etExpiryPoistkaCRSK = view.findViewById(R.id.et_expiry_poistka_cr_sk);
        chkZodpovednostiSvet = view.findViewById(R.id.chk_zodpovednosti_svet);
        etExpiryPoistkaSvet = view.findViewById(R.id.et_expiry_poistka_svet);

        // Note and button
        etNote = view.findViewById(R.id.et_note);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        // Set up expiry field visibility based on checkbox state.
        setupExpiryForCheckbox(chkPpl, etExpiryPpl);
        setupExpiryForCheckbox(chkCpl, etExpiryCpl);
        setupExpiryForCheckbox(chkAtpl, etExpiryAtpl);
        setupExpiryForCheckbox(chkSepLand, etExpirySepLand);
        setupExpiryForCheckbox(chkSepSea, etExpirySepSea);
        setupExpiryForCheckbox(chkMepLand, etExpiryMepLand);
        setupExpiryForCheckbox(chkMepSea, etExpiryMepSea);
        setupExpiryForCheckbox(chkIrSep, etExpiryIrSep);
        setupExpiryForCheckbox(chkIr, etExpiryIr);
        setupExpiryForCheckbox(chkMep, etExpiryMep);
        setupExpiryForCheckbox(chkNight, etExpiryNight);
        setupExpiryForCheckbox(chkFi, etExpiryFi);
        setupExpiryForCheckbox(chkIri, etExpiryIri);
        setupExpiryForCheckbox(chkTow, etExpiryTow);
        setupExpiryForCheckbox(chkTst, etExpiryTst);
        setupExpiryForCheckbox(chkPar, etExpiryPar);
        setupExpiryForCheckbox(chkAcr, etExpiryAcr);
        setupExpiryForCheckbox(chkMcc, etExpiryMcc);
        setupExpiryForCheckbox(chkMedClass1, etExpiryMedClass1);
        setupExpiryForCheckbox(chkMedClass2, etExpiryMedClass2);
        setupExpiryForCheckbox(chkVseobecnyPreukaz, etExpiryVseobecnyPreukaz);
        setupExpiryForCheckbox(chkObmedzenyPreukaz, etExpiryObmedzenyPreukaz);
        setupExpiryForCheckbox(chkEnglishLevel4, etExpiryEnglishLevel4);
        setupExpiryForCheckbox(chkEnglishLevel5, etExpiryEnglishLevel5);
        setupExpiryForCheckbox(chkEnglishLevel6, etExpiryEnglishLevel6);
        setupExpiryForCheckbox(chkEnglishIfr, etExpiryEnglishIfr);
        setupExpiryForCheckbox(chkPoistenieZodpovednostCRSK, etExpiryPoistkaCRSK);
        setupExpiryForCheckbox(chkZodpovednostiSvet, etExpiryPoistkaSvet);

        // Set dynamic date formatting for expiry fields.
        setExpiryTextWatcher(etExpiryPpl);
        setExpiryTextWatcher(etExpiryCpl);
        setExpiryTextWatcher(etExpiryAtpl);
        setExpiryTextWatcher(etExpirySepLand);
        setExpiryTextWatcher(etExpirySepSea);
        setExpiryTextWatcher(etExpiryMepLand);
        setExpiryTextWatcher(etExpiryMepSea);
        setExpiryTextWatcher(etExpiryIrSep);
        setExpiryTextWatcher(etExpiryIr);
        setExpiryTextWatcher(etExpiryMep);
        setExpiryTextWatcher(etExpiryNight);
        setExpiryTextWatcher(etExpiryFi);
        setExpiryTextWatcher(etExpiryIri);
        setExpiryTextWatcher(etExpiryTow);
        setExpiryTextWatcher(etExpiryTst);
        setExpiryTextWatcher(etExpiryPar);
        setExpiryTextWatcher(etExpiryAcr);
        setExpiryTextWatcher(etExpiryMcc);
        setExpiryTextWatcher(etExpiryMedClass1);
        setExpiryTextWatcher(etExpiryMedClass2);
        setExpiryTextWatcher(etExpiryVseobecnyPreukaz);
        setExpiryTextWatcher(etExpiryObmedzenyPreukaz);
        setExpiryTextWatcher(etExpiryEnglishLevel4);
        setExpiryTextWatcher(etExpiryEnglishLevel5);
        setExpiryTextWatcher(etExpiryEnglishLevel6);
        setExpiryTextWatcher(etExpiryEnglishIfr);
        setExpiryTextWatcher(etExpiryPoistkaCRSK);
        setExpiryTextWatcher(etExpiryPoistkaSvet);

        btnAddCertificate.setOnClickListener(v -> addCertificate());

        // Initialize the database
        database = CertificateDatabase.getInstance(getActivity());

        return view;
    }

    // Shows/hides the expiry EditText based on checkbox state.
    private void setupExpiryForCheckbox(CheckBox checkbox, final EditText expiryEditText) {
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                expiryEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    // Sets a TextWatcher for dynamic date formatting (yyyy-MM-dd).
    private void setExpiryTextWatcher(final EditText etExpiry) {
        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String formatted = formatDateInput(s.toString());
                if (!formatted.equals(s.toString())) {
                    etExpiry.setText(formatted);
                    etExpiry.setSelection(formatted.length());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // Formats a string of digits as yyyy-MM-dd.
    private String formatDateInput(String input) {
        input = input.replaceAll("[^0-9]", "");
        StringBuilder sb = new StringBuilder();
        if (input.length() >= 1) sb.append(input.substring(0, Math.min(4, input.length())));
        if (input.length() >= 5) sb.append("-").append(input.substring(4, Math.min(6, input.length())));
        if (input.length() >= 7) sb.append("-").append(input.substring(6, Math.min(8, input.length())));
        // Basic check: if the month is greater than 12, set it to 12.
        if (sb.length() >= 7) {
            String month = sb.substring(5, 7);
            if (!month.isEmpty() && Integer.parseInt(month) > 12) {
                sb.replace(5, 7, "12");
            }
        }
        // Similar check for the day.
        if (sb.length() >= 10) {
            String day = sb.substring(8, 10);
            if (!day.isEmpty() && Integer.parseInt(day) > 31) {
                sb.replace(8, 10, "31");
            }
        }
        return sb.toString();
    }

    // Helper: Validates the expiry date for a checked certificate.
    private String requireExpiry(CheckBox checkbox, EditText expiryEditText, String fieldName) {
        if (checkbox.isChecked()) {
            String expiry = expiryEditText.getText().toString().trim();
            if (expiry.equals("0") || expiry.equals("0-") || expiry.equals("0-0") || expiry.equals("0-0-0") || expiry.isEmpty()) {
                Toast.makeText(getActivity(), "Valid expiry date required for " + fieldName, Toast.LENGTH_SHORT).show();
                return null;
            }
            return expiry;
        }
        return null;
    }

    // Main method: Collects data, creates Certificate objects, inserts them locally, and sends each to the server.
    private void addCertificate() {
        // Retrieve user id from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        if (userId == 0) {
            Toast.makeText(getActivity(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that at least one certificate type is selected.
        if (!chkPpl.isChecked() && !chkCpl.isChecked() && !chkAtpl.isChecked() &&
                !chkSepLand.isChecked() && !chkSepSea.isChecked() && !chkMepLand.isChecked() &&
                !chkMepSea.isChecked() && !chkIrSep.isChecked() && !chkIr.isChecked() &&
                !chkMep.isChecked() && !chkNight.isChecked() && !chkFi.isChecked() &&
                !chkIri.isChecked() && !chkTow.isChecked() && !chkTst.isChecked() &&
                !chkPar.isChecked() && !chkAcr.isChecked() && !chkMcc.isChecked() &&
                !chkMedClass1.isChecked() && !chkMedClass2.isChecked() &&
                !chkVseobecnyPreukaz.isChecked() && !chkObmedzenyPreukaz.isChecked() &&
                !chkEnglishLevel4.isChecked() && !chkEnglishLevel5.isChecked() &&
                !chkEnglishLevel6.isChecked() && !chkEnglishIfr.isChecked() &&
                !chkPoistenieZodpovednostCRSK.isChecked() && !chkZodpovednostiSvet.isChecked()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etNote.getText().toString().trim();
        // Create a list to hold certificate records.
        List<Certificate> certificatesToAdd = new ArrayList<>();

        // For each certificate type that is checked, validate its expiry date and create a record.
        if (chkPpl.isChecked()) {
            String expiry = requireExpiry(chkPpl, etExpiryPpl, "PPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "PPL", expiry, note));
        }
        if (chkCpl.isChecked()) {
            String expiry = requireExpiry(chkCpl, etExpiryCpl, "CPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "CPL", expiry, note));
        }
        if (chkAtpl.isChecked()) {
            String expiry = requireExpiry(chkAtpl, etExpiryAtpl, "ATPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "ATPL", expiry, note));
        }
        if (chkSepLand.isChecked()) {
            String expiry = requireExpiry(chkSepLand, etExpirySepLand, "SEP Land");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "SEP land", expiry, note));
        }
        if (chkSepSea.isChecked()) {
            String expiry = requireExpiry(chkSepSea, etExpirySepSea, "SEP Sea");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "SEP sea", expiry, note));
        }
        if (chkMepLand.isChecked()) {
            String expiry = requireExpiry(chkMepLand, etExpiryMepLand, "MEP Land");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "MEP land", expiry, note));
        }
        if (chkMepSea.isChecked()) {
            String expiry = requireExpiry(chkMepSea, etExpiryMepSea, "MEP Sea");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "MEP sea", expiry, note));
        }
        if (chkIrSep.isChecked()) {
            String expiry = requireExpiry(chkIrSep, etExpiryIrSep, "IR SEP");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "IR SEP", expiry, note));
        }
        if (chkIr.isChecked()) {
            String expiry = requireExpiry(chkIr, etExpiryIr, "IR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "IR", expiry, note));
        }
        if (chkMep.isChecked()) {
            String expiry = requireExpiry(chkMep, etExpiryMep, "MEP");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "MEP", expiry, note));
        }
        if (chkNight.isChecked()) {
            String expiry = requireExpiry(chkNight, etExpiryNight, "Night");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "NIGHT", expiry, note));
        }
        if (chkFi.isChecked()) {
            String expiry = requireExpiry(chkFi, etExpiryFi, "FI");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "FI", expiry, note));
        }
        if (chkIri.isChecked()) {
            String expiry = requireExpiry(chkIri, etExpiryIri, "IRI");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "IRI", expiry, note));
        }
        if (chkTow.isChecked()) {
            String expiry = requireExpiry(chkTow, etExpiryTow, "TOW");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "TOW", expiry, note));
        }
        if (chkTst.isChecked()) {
            String expiry = requireExpiry(chkTst, etExpiryTst, "TST");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "TST", expiry, note));
        }
        if (chkPar.isChecked()) {
            String expiry = requireExpiry(chkPar, etExpiryPar, "PAR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "PAR", expiry, note));
        }
        if (chkAcr.isChecked()) {
            String expiry = requireExpiry(chkAcr, etExpiryAcr, "ACR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "ACR", expiry, note));
        }
        if (chkMcc.isChecked()) {
            String expiry = requireExpiry(chkMcc, etExpiryMcc, "MCC");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Velká éra", "MCC", expiry, note));
        }
        // Medical section
        if (chkMedClass1.isChecked()) {
            String expiry = requireExpiry(chkMedClass1, etExpiryMedClass1, "Medical Certificate Class 1");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Velká éra", "Medical Certificate Class 1", expiry, note));
        }
        if (chkMedClass2.isChecked()) {
            String expiry = requireExpiry(chkMedClass2, etExpiryMedClass2, "Medical Certificate Class 2");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Velká éra", "Medical Certificate Class 2", expiry, note));
        }
        // Rádio section
        if (chkVseobecnyPreukaz.isChecked()) {
            String expiry = requireExpiry(chkVseobecnyPreukaz, etExpiryVseobecnyPreukaz, "Všeobecný preukaz");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Velká éra", "Všeobecný preukaz radiofonisty", expiry, note));
        }
        if (chkObmedzenyPreukaz.isChecked()) {
            String expiry = requireExpiry(chkObmedzenyPreukaz, etExpiryObmedzenyPreukaz, "Obmedzený preukaz");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Velká éra", "Obmedzený preukaz radionofisty", expiry, note));
        }
        // Angličtina section
        if (chkEnglishLevel4.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel4, etExpiryEnglishLevel4, "English Level 4");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Velká éra", "ICAO English LEVEL 4", expiry, note));
        }
        if (chkEnglishLevel5.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel5, etExpiryEnglishLevel5, "English Level 5");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Velká éra", "ICAO English LEVEL 5", expiry, note));
        }
        if (chkEnglishLevel6.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel6, etExpiryEnglishLevel6, "English Level 6");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Velká éra", "ICAO English LEVEL 6", expiry, note));
        }
        if (chkEnglishIfr.isChecked()) {
            String expiry = requireExpiry(chkEnglishIfr, etExpiryEnglishIfr, "English IFR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Velká éra", "IFR angličtina", expiry, note));
        }
        // Poistka section
        if (chkPoistenieZodpovednostCRSK.isChecked()) {
            String expiry = requireExpiry(chkPoistenieZodpovednostCRSK, etExpiryPoistkaCRSK, "Poistenie ČR+SK");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Velká éra", "Poistenie zodpovednosti ČR + SK", expiry, note));
        }
        if (chkZodpovednostiSvet.isChecked()) {
            String expiry = requireExpiry(chkZodpovednostiSvet, etExpiryPoistkaSvet, "Poistenie SVET");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Velká éra", "Poistenie zodpovednosti SVET", expiry, note));
        }

        if (certificatesToAdd.isEmpty()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type and provide expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve and set the user id for each certificate and mark as addedOffline if no network.

        boolean offline = !isWiFiConnected();
        for (Certificate cert : certificatesToAdd) {
            cert.setUserId(userId);
            cert.setAddedOffline(offline);
        }

        // Insert each certificate locally and send immediately to server (if network available).
        Executors.newSingleThreadExecutor().execute(() -> {
            for (Certificate cert : certificatesToAdd) {
                database.certificateDao().insertCertificate(cert);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Certificate(s) Added!", Toast.LENGTH_SHORT).show();
                    clearFields();
                });
            }
            if (isWiFiConnected()) {
                for (Certificate cert : certificatesToAdd) {
                    sendCertificateToServer(cert);
                }
            } else {
                Log.d("AddVelkaEraFragment", "No network available, certificates stored locally only");
            }
        });
    }

    // Clears all input fields and resets checkboxes.
    private void clearFields() {
        etExpiryPpl.setText("");
        etExpiryCpl.setText("");
        etExpiryAtpl.setText("");
        etExpirySepLand.setText("");
        etExpirySepSea.setText("");
        etExpiryMepLand.setText("");
        etExpiryMepSea.setText("");
        etExpiryIrSep.setText("");
        etExpiryIr.setText("");
        etExpiryMep.setText("");
        etExpiryNight.setText("");
        etExpiryFi.setText("");
        etExpiryIri.setText("");
        etExpiryTow.setText("");
        etExpiryTst.setText("");
        etExpiryPar.setText("");
        etExpiryAcr.setText("");
        etExpiryMcc.setText("");

        etExpiryMedClass1.setText("");
        etExpiryMedClass2.setText("");

        etExpiryVseobecnyPreukaz.setText("");
        etExpiryObmedzenyPreukaz.setText("");

        etExpiryEnglishLevel4.setText("");
        etExpiryEnglishLevel5.setText("");
        etExpiryEnglishLevel6.setText("");
        etExpiryEnglishIfr.setText("");

        etExpiryPoistkaCRSK.setText("");
        etExpiryPoistkaSvet.setText("");

        etNote.setText("");

        chkPpl.setChecked(false);
        chkCpl.setChecked(false);
        chkAtpl.setChecked(false);
        chkSepLand.setChecked(false);
        chkSepSea.setChecked(false);
        chkMepLand.setChecked(false);
        chkMepSea.setChecked(false);
        chkIrSep.setChecked(false);
        chkIr.setChecked(false);
        chkMep.setChecked(false);
        chkNight.setChecked(false);
        chkFi.setChecked(false);
        chkIri.setChecked(false);
        chkTow.setChecked(false);
        chkTst.setChecked(false);
        chkPar.setChecked(false);
        chkAcr.setChecked(false);
        chkMcc.setChecked(false);
        chkMedClass1.setChecked(false);
        chkMedClass2.setChecked(false);
        chkVseobecnyPreukaz.setChecked(false);
        chkObmedzenyPreukaz.setChecked(false);
        chkEnglishLevel4.setChecked(false);
        chkEnglishLevel5.setChecked(false);
        chkEnglishLevel6.setChecked(false);
        chkEnglishIfr.setChecked(false);
        chkPoistenieZodpovednostCRSK.setChecked(false);
        chkZodpovednostiSvet.setChecked(false);
    }

    // Checks if there is an active network connection.
    private boolean isWiFiConnected() {
        if (getActivity() != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    // Sends a certificate to the server using Retrofit.
    private void sendCertificateToServer(Certificate certificate) {
        RetrofitClient.getApi().addCertificate(certificate).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.certificateDao().markAsSynced(certificate.getId());
                    });
                    Toast.makeText(getActivity(), "Certificate synchronized", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Retrofit", "Failed response: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Retrofit", "Error sending certificate: " + t.getMessage());
            }
        });
    }

}
