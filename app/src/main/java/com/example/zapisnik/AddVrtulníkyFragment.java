package com.example.zapisnik;

import android.content.Context;
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

public class AddVrtulníkyFragment extends Fragment {

    // Letecké kvalifikácie - Vrtuľníky
    private CheckBox chkPpl, chkCpl, chkAtpl, chkIr, chkNight, chkFi, chkIri, chkTst, chkPar;
    private EditText etExpiryPpl, etExpiryCpl, etExpiryAtpl, etExpiryIr, etExpiryNight, etExpiryFi, etExpiryIri, etExpiryTst, etExpiryPar;

    // Medical - Vrtuľníky
    private CheckBox chkMedClass1, chkMedClass2;
    private EditText etExpiryMedClass1, etExpiryMedClass2;

    // Rádio - Vrtuľníky
    private CheckBox chkVseobecnyPreukaz, chkObmedzenyPreukaz;
    private EditText etExpiryVseobecnyPreukaz, etExpiryObmedzenyPreukaz;

    // Angličtina - Vrtuľníky
    private CheckBox chkEnglishLevel4, chkEnglishLevel5, chkEnglishLevel6;
    private EditText etExpiryEnglishLevel4, etExpiryEnglishLevel5, etExpiryEnglishLevel6;

    // Poistka - Vrtuľníky
    private CheckBox chkPoistenieZodpovednostCRSK, chkZodpovednostiSvet;
    private EditText etExpiryPoistenieZodpovednostCRSK, etExpiryZodpovednostiSvet;

    // Common note field and Add Certificate Button
    private EditText etNote;
    private Button btnAddCertificate;

    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vrtulniky, container, false);

        // Inicializácia komponentov pre Letecké kvalifikácie - Vrtuľníky
        chkPpl = view.findViewById(R.id.chk_ppl_vrtulniky);
        etExpiryPpl = view.findViewById(R.id.et_expiry_ppl_vrtulniky);
        chkCpl = view.findViewById(R.id.chk_cpl_vrtulniky);
        etExpiryCpl = view.findViewById(R.id.et_expiry_cpl_vrtulniky);
        chkAtpl = view.findViewById(R.id.chk_atpl_vrtulniky);
        etExpiryAtpl = view.findViewById(R.id.et_expiry_atpl_vrtulniky);
        chkIr = view.findViewById(R.id.chk_ir_vrtulniky);
        etExpiryIr = view.findViewById(R.id.et_expiry_ir_vrtulniky);
        chkNight = view.findViewById(R.id.chk_night_vrtulniky);
        etExpiryNight = view.findViewById(R.id.et_expiry_night_vrtulniky);
        chkFi = view.findViewById(R.id.chk_fi_vrtulniky);
        etExpiryFi = view.findViewById(R.id.et_expiry_fi_vrtulniky);
        chkIri = view.findViewById(R.id.chk_iri_vrtulniky);
        etExpiryIri = view.findViewById(R.id.et_expiry_iri_vrtulniky);
        chkTst = view.findViewById(R.id.chk_tst_vrtulniky);
        etExpiryTst = view.findViewById(R.id.et_expiry_tst_vrtulniky);
        chkPar = view.findViewById(R.id.chk_par_vrtulniky);
        etExpiryPar = view.findViewById(R.id.et_expiry_par_vrtulniky);

        // Inicializácia komponentov pre Medical - Vrtuľníky
        chkMedClass1 = view.findViewById(R.id.chk_med_class1_vrtulniky);
        etExpiryMedClass1 = view.findViewById(R.id.et_expiry_med_class1_vrtulniky);
        chkMedClass2 = view.findViewById(R.id.chk_med_class2_vrtulniky);
        etExpiryMedClass2 = view.findViewById(R.id.et_expiry_med_class2_vrtulniky);

        // Inicializácia komponentov pre Rádio - Vrtuľníky
        chkVseobecnyPreukaz = view.findViewById(R.id.chk_vseobecny_preukaz_vrtulniky);
        etExpiryVseobecnyPreukaz = view.findViewById(R.id.et_expiry_vseobecny_preukaz_vrtulniky);
        chkObmedzenyPreukaz = view.findViewById(R.id.chk_obmedzeny_preukaz_vrtulniky);
        etExpiryObmedzenyPreukaz = view.findViewById(R.id.et_expiry_obmedzeny_preukaz_vrtulniky);

        // Inicializácia komponentov pre Angličtina - Vrtuľníky
        chkEnglishLevel4 = view.findViewById(R.id.chk_english_level4_vrtulniky);
        etExpiryEnglishLevel4 = view.findViewById(R.id.et_expiry_english_level4_vrtulniky);
        chkEnglishLevel5 = view.findViewById(R.id.chk_english_level5_vrtulniky);
        etExpiryEnglishLevel5 = view.findViewById(R.id.et_expiry_english_level5_vrtulniky);
        chkEnglishLevel6 = view.findViewById(R.id.chk_english_level6_vrtulniky);
        etExpiryEnglishLevel6 = view.findViewById(R.id.et_expiry_english_level6_vrtulniky);

        // Inicializácia komponentov pre Poistka - Vrtuľníky
        chkPoistenieZodpovednostCRSK = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_vrtulniky);
        etExpiryPoistenieZodpovednostCRSK = view.findViewById(R.id.et_expiry_poistenie_zodpovednost_cr_sk_vrtulniky);
        chkZodpovednostiSvet = view.findViewById(R.id.chk_zodpovednosti_svet_vrtulniky);
        etExpiryZodpovednostiSvet = view.findViewById(R.id.et_expiry_zodpovednosti_svet_vrtulniky);

        // Inicializácia poľa pre poznámku a tlačidla
        etNote = view.findViewById(R.id.et_note_vetrone);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        // Nastavenie viditeľnosti EditTextov pre expiry dátum podľa stavu checkboxu
        setupExpiryForCheckbox(chkPpl, etExpiryPpl);
        setupExpiryForCheckbox(chkCpl, etExpiryCpl);
        setupExpiryForCheckbox(chkAtpl, etExpiryAtpl);
        setupExpiryForCheckbox(chkIr, etExpiryIr);
        setupExpiryForCheckbox(chkNight, etExpiryNight);
        setupExpiryForCheckbox(chkFi, etExpiryFi);
        setupExpiryForCheckbox(chkIri, etExpiryIri);
        setupExpiryForCheckbox(chkTst, etExpiryTst);
        setupExpiryForCheckbox(chkPar, etExpiryPar);

        setupExpiryForCheckbox(chkMedClass1, etExpiryMedClass1);
        setupExpiryForCheckbox(chkMedClass2, etExpiryMedClass2);

        setupExpiryForCheckbox(chkVseobecnyPreukaz, etExpiryVseobecnyPreukaz);
        setupExpiryForCheckbox(chkObmedzenyPreukaz, etExpiryObmedzenyPreukaz);

        setupExpiryForCheckbox(chkEnglishLevel4, etExpiryEnglishLevel4);
        setupExpiryForCheckbox(chkEnglishLevel5, etExpiryEnglishLevel5);
        setupExpiryForCheckbox(chkEnglishLevel6, etExpiryEnglishLevel6);

        setupExpiryForCheckbox(chkPoistenieZodpovednostCRSK, etExpiryPoistenieZodpovednostCRSK);
        setupExpiryForCheckbox(chkZodpovednostiSvet, etExpiryZodpovednostiSvet);

        // Pridanie TextWatcherov pre dynamické formátovanie dátumu
        setExpiryTextWatcher(etExpiryPpl);
        setExpiryTextWatcher(etExpiryCpl);
        setExpiryTextWatcher(etExpiryAtpl);
        setExpiryTextWatcher(etExpiryIr);
        setExpiryTextWatcher(etExpiryNight);
        setExpiryTextWatcher(etExpiryFi);
        setExpiryTextWatcher(etExpiryIri);
        setExpiryTextWatcher(etExpiryTst);
        setExpiryTextWatcher(etExpiryPar);

        setExpiryTextWatcher(etExpiryMedClass1);
        setExpiryTextWatcher(etExpiryMedClass2);

        setExpiryTextWatcher(etExpiryVseobecnyPreukaz);
        setExpiryTextWatcher(etExpiryObmedzenyPreukaz);

        setExpiryTextWatcher(etExpiryEnglishLevel4);
        setExpiryTextWatcher(etExpiryEnglishLevel5);
        setExpiryTextWatcher(etExpiryEnglishLevel6);

        setExpiryTextWatcher(etExpiryPoistenieZodpovednostCRSK);
        setExpiryTextWatcher(etExpiryZodpovednostiSvet);

        btnAddCertificate.setOnClickListener(v -> addCertificate());

        // Inicializácia databázy
        database = CertificateDatabase.getInstance(getActivity());

        return view;
    }

    // Zobrazenie/skrytie EditTextu pre expiry podľa stavu checkboxu.
    private void setupExpiryForCheckbox(CheckBox checkbox, final EditText expiryEditText) {
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                expiryEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    // Nastavenie TextWatcher pre dynamické formátovanie vstupu do tvaru yyyy-MM-dd.
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

    // Metóda na formátovanie vstupu na dátum (yyyy-MM-dd).
    private String formatDateInput(String input) {
        input = input.replaceAll("[^0-9]", "");
        StringBuilder sb = new StringBuilder();
        if (input.length() >= 1) sb.append(input.substring(0, Math.min(4, input.length())));
        if (input.length() >= 5) sb.append("-").append(input.substring(4, Math.min(6, input.length())));
        if (input.length() >= 7) sb.append("-").append(input.substring(6, Math.min(8, input.length())));
        if (sb.length() >= 7) {
            String month = sb.substring(5, 7);
            if (!month.isEmpty() && Integer.parseInt(month) > 12) {
                sb.replace(5, 7, "12");
            }
        }
        if (sb.length() >= 10) {
            String day = sb.substring(8, 10);
            if (!day.isEmpty() && Integer.parseInt(day) > 31) {
                sb.replace(8, 10, "31");
            }
        }
        return sb.toString();
    }

    // Validácia vstupu expiry dátumu, ak je príslušný checkbox zaškrtnutý.
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

    // Hlavná metóda na zber dát, vytvorenie objektov Certificate a ich uloženie lokálne (a odoslanie na server, ak je sieť dostupná).
    private void addCertificate() {
        // Overenie, či bolo aspoň jedno certifikačné pole vybraté.
        if (!chkPpl.isChecked() && !chkCpl.isChecked() && !chkAtpl.isChecked() &&
                !chkIr.isChecked() && !chkNight.isChecked() && !chkFi.isChecked() &&
                !chkIri.isChecked() && !chkTst.isChecked() && !chkPar.isChecked() &&
                !chkMedClass1.isChecked() && !chkMedClass2.isChecked() &&
                !chkVseobecnyPreukaz.isChecked() && !chkObmedzenyPreukaz.isChecked() &&
                !chkEnglishLevel4.isChecked() && !chkEnglishLevel5.isChecked() &&
                !chkEnglishLevel6.isChecked() &&
                !chkPoistenieZodpovednostCRSK.isChecked() && !chkZodpovednostiSvet.isChecked()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etNote.getText().toString().trim();
        String acquiredDate = "";
        int daysRemaining = 0;

        List<Certificate> certificatesToAdd = new ArrayList<>();

        // Letecké kvalifikácie - Vrtuľníky
        if (chkPpl.isChecked()) {
            String expiry = requireExpiry(chkPpl, etExpiryPpl, "PPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "PPL", expiry, note));
        }
        if (chkCpl.isChecked()) {
            String expiry = requireExpiry(chkCpl, etExpiryCpl, "CPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "CPL", expiry, note));
        }
        if (chkAtpl.isChecked()) {
            String expiry = requireExpiry(chkAtpl, etExpiryAtpl, "ATPL");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "ATPL", expiry, note));
        }
        if (chkIr.isChecked()) {
            String expiry = requireExpiry(chkIr, etExpiryIr, "IR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "IR", expiry, note));
        }
        if (chkNight.isChecked()) {
            String expiry = requireExpiry(chkNight, etExpiryNight, "NIGHT");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "NIGHT", expiry, note));
        }
        if (chkFi.isChecked()) {
            String expiry = requireExpiry(chkFi, etExpiryFi, "FI");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "FI", expiry, note));
        }
        if (chkIri.isChecked()) {
            String expiry = requireExpiry(chkIri, etExpiryIri, "IRI");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "IRI", expiry, note));
        }
        if (chkTst.isChecked()) {
            String expiry = requireExpiry(chkTst, etExpiryTst, "TST");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "TST", expiry, note));
        }
        if (chkPar.isChecked()) {
            String expiry = requireExpiry(chkPar, etExpiryPar, "PAR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Vrtuľníky", "PAR", expiry, note));
        }

        // Medical - Vrtuľníky
        if (chkMedClass1.isChecked()) {
            String expiry = requireExpiry(chkMedClass1, etExpiryMedClass1, "Medical Certificate Class 1");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Vrtuľníky", "Medical Certificate Class 1", expiry, note));
        }
        if (chkMedClass2.isChecked()) {
            String expiry = requireExpiry(chkMedClass2, etExpiryMedClass2, "Medical Certificate Class 2");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Vrtuľníky", "Medical Certificate Class 2", expiry, note));
        }

        // Rádio - Vrtuľníky
        if (chkVseobecnyPreukaz.isChecked()) {
            String expiry = requireExpiry(chkVseobecnyPreukaz, etExpiryVseobecnyPreukaz, "Všeobecný preukaz radiofonisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Vrtuľníky", "Všeobecný preukaz radiofonisty", expiry, note));
        }
        if (chkObmedzenyPreukaz.isChecked()) {
            String expiry = requireExpiry(chkObmedzenyPreukaz, etExpiryObmedzenyPreukaz, "Obmedzený preukaz radionofisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Vrtuľníky", "Obmedzený preukaz radionofisty", expiry, note));
        }

        // Angličtina - Vrtuľníky
        if (chkEnglishLevel4.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel4, etExpiryEnglishLevel4, "ICAO English LEVEL 4");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Vrtuľníky", "ICAO English LEVEL 4", expiry, note));
        }
        if (chkEnglishLevel5.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel5, etExpiryEnglishLevel5, "ICAO English LEVEL 5");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Vrtuľníky", "ICAO English LEVEL 5", expiry, note));
        }
        if (chkEnglishLevel6.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel6, etExpiryEnglishLevel6, "ICAO English LEVEL 6");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Vrtuľníky", "ICAO English LEVEL 6", expiry, note));
        }

        // Poistka - Vrtuľníky
        if (chkPoistenieZodpovednostCRSK.isChecked()) {
            String expiry = requireExpiry(chkPoistenieZodpovednostCRSK, etExpiryPoistenieZodpovednostCRSK, "Poistenie ČR+SK");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Vrtuľníky", "Poistenie zodpovednosti ČR + SK", expiry, note));
        }
        if (chkZodpovednostiSvet.isChecked()) {
            String expiry = requireExpiry(chkZodpovednostiSvet, etExpiryZodpovednostiSvet, "Poistenie SVET");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Vrtuľníky", "Poistenie zodpovednosti SVET", expiry, note));
        }

        if (certificatesToAdd.isEmpty()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type and provide expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

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
                Log.d("AddVrtulnikyFragment", "No network available, certificates stored locally only");
            }
        });
    }

    // Vyčistenie všetkých vstupných polí a reset checkboxov.
    private void clearFields() {
        etExpiryPpl.setText("");
        etExpiryCpl.setText("");
        etExpiryAtpl.setText("");
        etExpiryIr.setText("");
        etExpiryNight.setText("");
        etExpiryFi.setText("");
        etExpiryIri.setText("");
        etExpiryTst.setText("");
        etExpiryPar.setText("");

        etExpiryMedClass1.setText("");
        etExpiryMedClass2.setText("");

        etExpiryVseobecnyPreukaz.setText("");
        etExpiryObmedzenyPreukaz.setText("");

        etExpiryEnglishLevel4.setText("");
        etExpiryEnglishLevel5.setText("");
        etExpiryEnglishLevel6.setText("");

        etExpiryPoistenieZodpovednostCRSK.setText("");
        etExpiryZodpovednostiSvet.setText("");

        etNote.setText("");

        chkPpl.setChecked(false);
        chkCpl.setChecked(false);
        chkAtpl.setChecked(false);
        chkIr.setChecked(false);
        chkNight.setChecked(false);
        chkFi.setChecked(false);
        chkIri.setChecked(false);
        chkTst.setChecked(false);
        chkPar.setChecked(false);

        chkMedClass1.setChecked(false);
        chkMedClass2.setChecked(false);

        chkVseobecnyPreukaz.setChecked(false);
        chkObmedzenyPreukaz.setChecked(false);

        chkEnglishLevel4.setChecked(false);
        chkEnglishLevel5.setChecked(false);
        chkEnglishLevel6.setChecked(false);

        chkPoistenieZodpovednostCRSK.setChecked(false);
        chkZodpovednostiSvet.setChecked(false);
    }

    // Kontrola aktívneho sieťového pripojenia.
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
