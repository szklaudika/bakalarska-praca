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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVetroneFragment extends Fragment {

    // Letecké kvalifikácie - Vetrone
    private CheckBox chkGldVetrone, chkTmgVetrone, chkFiVetrone, chkTstVetrone;
    private EditText etExpiryGldVetrone, etExpiryTmgVetrone, etExpiryFiVetrone, etExpiryTstVetrone;

    // Medical - Vetrone
    private CheckBox chkMedClass1Vetrone, chkMedClass2Vetrone;
    private EditText etExpiryMedClass1Vetrone, etExpiryMedClass2Vetrone;

    // Rádio - Vetrone
    private CheckBox chkVseobecnyPreukazVetrone, chkObmedzenyPreukazVetrone;
    private EditText etExpiryVseobecnyPreukazVetrone, etExpiryObmedzenyPreukazVetrone;

    // Angličtina - Vetrone
    private CheckBox chkEnglishLevel4Vetrone, chkEnglishLevel5Vetrone, chkEnglishLevel6Vetrone;
    private EditText etExpiryEnglishLevel4Vetrone, etExpiryEnglishLevel5Vetrone, etExpiryEnglishLevel6Vetrone;

    // Poistka - Vetrone
    private CheckBox chkPoistenieZodpovednostCrSkVetrone, chkZodpovednostiSvetVetrone;
    private EditText etExpiryPoistenieZodpovednostCrSkVetrone, etExpiryZodpovednostiSvetVetrone;

    // Common note field and Add button
    private EditText etNoteVetrone;
    private Button btnAddCertificate;

    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vetrone, container, false);

        // Inicializácia UI komponentov - Letecké kvalifikácie
        chkGldVetrone = view.findViewById(R.id.chk_gld_vetrone);
        etExpiryGldVetrone = view.findViewById(R.id.et_expiry_gld_vetrone);

        chkTmgVetrone = view.findViewById(R.id.chk_tmg_vetrone);
        etExpiryTmgVetrone = view.findViewById(R.id.et_expiry_tmg_vetrone);

        chkFiVetrone = view.findViewById(R.id.chk_fi_vetrone);
        etExpiryFiVetrone = view.findViewById(R.id.et_expiry_fi_vetrone);

        chkTstVetrone = view.findViewById(R.id.chk_tst_vetrone);
        etExpiryTstVetrone = view.findViewById(R.id.et_expiry_tst_vetrone);

        // Inicializácia Medical sekcie - Vetrone
        chkMedClass1Vetrone = view.findViewById(R.id.chk_med_class1_vetrone);
        etExpiryMedClass1Vetrone = view.findViewById(R.id.et_expiry_med_class1_vetrone);
        chkMedClass2Vetrone = view.findViewById(R.id.chk_med_class2_vetrone);
        etExpiryMedClass2Vetrone = view.findViewById(R.id.et_expiry_med_class2_vetrone);

        // Inicializácia Rádio sekcie - Vetrone
        chkVseobecnyPreukazVetrone = view.findViewById(R.id.chk_vseobecny_preukaz_vetrone);
        etExpiryVseobecnyPreukazVetrone = view.findViewById(R.id.et_expiry_vseobecny_preukaz_vetrone);
        chkObmedzenyPreukazVetrone = view.findViewById(R.id.chk_obmedzeny_preukaz_vetrone);
        etExpiryObmedzenyPreukazVetrone = view.findViewById(R.id.et_expiry_obmedzeny_preukaz_vetrone);

        // Inicializácia Angličtina sekcie - Vetrone
        chkEnglishLevel4Vetrone = view.findViewById(R.id.chk_english_level4_vetrone);
        etExpiryEnglishLevel4Vetrone = view.findViewById(R.id.et_expiry_english_level4_vetrone);
        chkEnglishLevel5Vetrone = view.findViewById(R.id.chk_english_level5_vetrone);
        etExpiryEnglishLevel5Vetrone = view.findViewById(R.id.et_expiry_english_level5_vetrone);
        chkEnglishLevel6Vetrone = view.findViewById(R.id.chk_english_level6_vetrone);
        etExpiryEnglishLevel6Vetrone = view.findViewById(R.id.et_expiry_english_level6_vetrone);

        // Inicializácia Poistka sekcie - Vetrone
        chkPoistenieZodpovednostCrSkVetrone = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_vetrone);
        etExpiryPoistenieZodpovednostCrSkVetrone = view.findViewById(R.id.et_expiry_poistenie_zodpovednost_cr_sk_vetrone);
        chkZodpovednostiSvetVetrone = view.findViewById(R.id.chk_zodpovednosti_svet_vetrone);
        etExpiryZodpovednostiSvetVetrone = view.findViewById(R.id.et_expiry_zodpovednosti_svet_vetrone);

        // Inicializácia spoločného note field a tlačidla
        etNoteVetrone = view.findViewById(R.id.et_note_vetrone);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        // Nastavenie viditeľnosti expiry EditTextov podľa stavu checkboxov.
        setupExpiryForCheckbox(chkGldVetrone, etExpiryGldVetrone);
        setupExpiryForCheckbox(chkTmgVetrone, etExpiryTmgVetrone);
        setupExpiryForCheckbox(chkFiVetrone, etExpiryFiVetrone);
        setupExpiryForCheckbox(chkTstVetrone, etExpiryTstVetrone);

        setupExpiryForCheckbox(chkMedClass1Vetrone, etExpiryMedClass1Vetrone);
        setupExpiryForCheckbox(chkMedClass2Vetrone, etExpiryMedClass2Vetrone);

        setupExpiryForCheckbox(chkVseobecnyPreukazVetrone, etExpiryVseobecnyPreukazVetrone);
        setupExpiryForCheckbox(chkObmedzenyPreukazVetrone, etExpiryObmedzenyPreukazVetrone);

        setupExpiryForCheckbox(chkEnglishLevel4Vetrone, etExpiryEnglishLevel4Vetrone);
        setupExpiryForCheckbox(chkEnglishLevel5Vetrone, etExpiryEnglishLevel5Vetrone);
        setupExpiryForCheckbox(chkEnglishLevel6Vetrone, etExpiryEnglishLevel6Vetrone);

        setupExpiryForCheckbox(chkPoistenieZodpovednostCrSkVetrone, etExpiryPoistenieZodpovednostCrSkVetrone);
        setupExpiryForCheckbox(chkZodpovednostiSvetVetrone, etExpiryZodpovednostiSvetVetrone);

        // Nastavenie dynamického formátovania dátumu (yyyy-MM-dd)
        setExpiryTextWatcher(etExpiryGldVetrone);
        setExpiryTextWatcher(etExpiryTmgVetrone);
        setExpiryTextWatcher(etExpiryFiVetrone);
        setExpiryTextWatcher(etExpiryTstVetrone);

        setExpiryTextWatcher(etExpiryMedClass1Vetrone);
        setExpiryTextWatcher(etExpiryMedClass2Vetrone);

        setExpiryTextWatcher(etExpiryVseobecnyPreukazVetrone);
        setExpiryTextWatcher(etExpiryObmedzenyPreukazVetrone);

        setExpiryTextWatcher(etExpiryEnglishLevel4Vetrone);
        setExpiryTextWatcher(etExpiryEnglishLevel5Vetrone);
        setExpiryTextWatcher(etExpiryEnglishLevel6Vetrone);

        setExpiryTextWatcher(etExpiryPoistenieZodpovednostCrSkVetrone);
        setExpiryTextWatcher(etExpiryZodpovednostiSvetVetrone);

        btnAddCertificate.setOnClickListener(v -> addCertificate());

        // Inicializácia databázy
        database = CertificateDatabase.getInstance(getActivity());

        return view;
    }

    // Zobrazenie/skrytie expiry EditText na základe stavu checkboxu.
    private void setupExpiryForCheckbox(CheckBox checkbox, final EditText expiryEditText) {
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                expiryEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    // Nastavenie TextWatcher-u pre dynamické formátovanie dátumu (yyyy-MM-dd).
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

    // Formátuje vstup (iba číslice) do formátu yyyy-MM-dd.
    private String formatDateInput(String input) {
        input = input.replaceAll("[^0-9]", "");
        StringBuilder sb = new StringBuilder();
        if (input.length() >= 1) sb.append(input.substring(0, Math.min(4, input.length())));
        if (input.length() >= 5) sb.append("-").append(input.substring(4, Math.min(6, input.length())));
        if (input.length() >= 7) sb.append("-").append(input.substring(6, Math.min(8, input.length())));
        // Kontrola mesiaca: ak je väčší ako 12, nastavíme 12.
        if (sb.length() >= 7) {
            String month = sb.substring(5, 7);
            if (!month.isEmpty() && Integer.parseInt(month) > 12) {
                sb.replace(5, 7, "12");
            }
        }
        // Kontrola dňa: ak je väčší ako 31, nastavíme 31.
        if (sb.length() >= 10) {
            String day = sb.substring(8, 10);
            if (!day.isEmpty() && Integer.parseInt(day) > 31) {
                sb.replace(8, 10, "31");
            }
        }
        return sb.toString();
    }

    // Validuje expiry dátum pre zaškrtnutý checkbox; ak nie je platný, zobrazí Toast a vráti null.
    private String requireExpiry(CheckBox checkbox, EditText expiryEditText, String fieldName) {
        if (checkbox.isChecked()) {
            String expiry = expiryEditText.getText().toString().trim();
            if (expiry.equals("0") || expiry.equals("0-") || expiry.equals("0-0") ||
                    expiry.equals("0-0-0") || expiry.isEmpty()) {
                Toast.makeText(getActivity(), "Valid expiry date required for " + fieldName, Toast.LENGTH_SHORT).show();
                return null;
            }
            return expiry;
        }
        return null;
    }

    // Zhromažďuje údaje, vytvára objekty Certificate, ukladá ich lokálne a odosiela na server.
    private void addCertificate() {
        // Retrieve user id from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        if (userId == 0) {
            Toast.makeText(getActivity(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that at least one certificate type is selected.
        if (!chkGldVetrone.isChecked() && !chkTmgVetrone.isChecked() && !chkFiVetrone.isChecked() && !chkTstVetrone.isChecked() &&
                !chkMedClass1Vetrone.isChecked() && !chkMedClass2Vetrone.isChecked() &&
                !chkVseobecnyPreukazVetrone.isChecked() && !chkObmedzenyPreukazVetrone.isChecked() &&
                !chkEnglishLevel4Vetrone.isChecked() && !chkEnglishLevel5Vetrone.isChecked() && !chkEnglishLevel6Vetrone.isChecked() &&
                !chkPoistenieZodpovednostCrSkVetrone.isChecked() && !chkZodpovednostiSvetVetrone.isChecked()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etNoteVetrone.getText().toString().trim();
        List<Certificate> certificatesToAdd = new ArrayList<>();

        // Letecké kvalifikácie - Vetrone
        if (chkGldVetrone.isChecked()) {
            String expiry = requireExpiry(chkGldVetrone, etExpiryGldVetrone, "GLD");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Aviation Qualifications", "Vetrone", "GLD", expiry, note));
        }
        if (chkTmgVetrone.isChecked()) {
            String expiry = requireExpiry(chkTmgVetrone, etExpiryTmgVetrone, "TMG");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Aviation Qualifications", "Vetrone", "TMG", expiry, note));
        }
        if (chkFiVetrone.isChecked()) {
            String expiry = requireExpiry(chkFiVetrone, etExpiryFiVetrone, "FI");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Aviation Qualifications", "Vetrone", "FI", expiry, note));
        }
        if (chkTstVetrone.isChecked()) {
            String expiry = requireExpiry(chkTstVetrone, etExpiryTstVetrone, "TST");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Aviation Qualifications", "Vetrone", "TST", expiry, note));
        }
        // Medical - Vetrone
        if (chkMedClass1Vetrone.isChecked()) {
            String expiry = requireExpiry(chkMedClass1Vetrone, etExpiryMedClass1Vetrone, "Medical Certificate Class 1");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Vetrone", "Medical Certificate Class 1", expiry, note));
        }
        if (chkMedClass2Vetrone.isChecked()) {
            String expiry = requireExpiry(chkMedClass2Vetrone, etExpiryMedClass2Vetrone, "Medical Certificate Class 2");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Vetrone", "Medical Certificate Class 2", expiry, note));
        }
        // Rádio - Vetrone
        if (chkVseobecnyPreukazVetrone.isChecked()) {
            String expiry = requireExpiry(chkVseobecnyPreukazVetrone, etExpiryVseobecnyPreukazVetrone, "Všeobecný preukaz radiofonisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Radio", "Vetrone", "General Radiotelephone Operator License", expiry, note));
        }
        if (chkObmedzenyPreukazVetrone.isChecked()) {
            String expiry = requireExpiry(chkObmedzenyPreukazVetrone, etExpiryObmedzenyPreukazVetrone, "Obmedzený preukaz radionofisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Radio", "Vetrone", "Restricted Radiotelephone Operator License", expiry, note));
        }
        // Angličtina - Vetrone
        if (chkEnglishLevel4Vetrone.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel4Vetrone, etExpiryEnglishLevel4Vetrone, "ICAO English LEVEL 4");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("English", "Vetrone", "ICAO English LEVEL 4", expiry, note));
        }
        if (chkEnglishLevel5Vetrone.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel5Vetrone, etExpiryEnglishLevel5Vetrone, "ICAO English LEVEL 5");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("English", "Vetrone", "ICAO English LEVEL 5", expiry, note));
        }
        if (chkEnglishLevel6Vetrone.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel6Vetrone, etExpiryEnglishLevel6Vetrone, "ICAO English LEVEL 6");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("English", "Vetrone", "ICAO English LEVEL 6", expiry, note));
        }
        // Poistka - Vetrone
        if (chkPoistenieZodpovednostCrSkVetrone.isChecked()) {
            String expiry = requireExpiry(chkPoistenieZodpovednostCrSkVetrone, etExpiryPoistenieZodpovednostCrSkVetrone, "Poistenie ČR+SK");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("English", "Vetrone", "Liability insurance (Czech Republic + Slovakia)", expiry, note));
        }
        if (chkZodpovednostiSvetVetrone.isChecked()) {
            String expiry = requireExpiry(chkZodpovednostiSvetVetrone, etExpiryZodpovednostiSvetVetrone, "Poistenie SVET");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("English", "Vetrone", "Worldwide liability insurance", expiry, note));
        }

        if (certificatesToAdd.isEmpty()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type and provide expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the user id and mark as addedOffline if no network is available.
        boolean offline = !isWiFiConnected();
        for (Certificate cert : certificatesToAdd) {
            cert.setUserId(userId);
            cert.setAddedOffline(offline);
        }

        // Insert certificates locally and send them to the server (if network available).
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
                Log.d("AddVetroneFragment", "No network available, certificates stored locally only");
            }
        });
    }


    // Vyčistenie všetkých vstupných polí a reset checkboxov.
    private void clearFields() {
        etExpiryGldVetrone.setText("");
        etExpiryTmgVetrone.setText("");
        etExpiryFiVetrone.setText("");
        etExpiryTstVetrone.setText("");

        etExpiryMedClass1Vetrone.setText("");
        etExpiryMedClass2Vetrone.setText("");

        etExpiryVseobecnyPreukazVetrone.setText("");
        etExpiryObmedzenyPreukazVetrone.setText("");

        etExpiryEnglishLevel4Vetrone.setText("");
        etExpiryEnglishLevel5Vetrone.setText("");
        etExpiryEnglishLevel6Vetrone.setText("");

        etExpiryPoistenieZodpovednostCrSkVetrone.setText("");
        etExpiryZodpovednostiSvetVetrone.setText("");

        etNoteVetrone.setText("");

        chkGldVetrone.setChecked(false);
        chkTmgVetrone.setChecked(false);
        chkFiVetrone.setChecked(false);
        chkTstVetrone.setChecked(false);

        chkMedClass1Vetrone.setChecked(false);
        chkMedClass2Vetrone.setChecked(false);

        chkVseobecnyPreukazVetrone.setChecked(false);
        chkObmedzenyPreukazVetrone.setChecked(false);

        chkEnglishLevel4Vetrone.setChecked(false);
        chkEnglishLevel5Vetrone.setChecked(false);
        chkEnglishLevel6Vetrone.setChecked(false);

        chkPoistenieZodpovednostCrSkVetrone.setChecked(false);
        chkZodpovednostiSvetVetrone.setChecked(false);
    }

    // Kontrola, či je aktívne sieťové pripojenie.
    private boolean isWiFiConnected() {
        if (getActivity() != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    // Odoslanie certifikátu na server pomocou Retrofit.
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
