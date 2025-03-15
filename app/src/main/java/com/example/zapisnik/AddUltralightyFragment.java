package com.example.zapisnik;

import android.app.AlertDialog;
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

public class AddUltralightyFragment extends Fragment {

    // Letecké kvalifikácie - Ultralighty
    private CheckBox chkUllUltralighty, chkVfrUltralighty, chkInstruktor, chkVlekar, chkSkusobnyPilot, chkVysadzovac;
    private EditText etExpiryUllUltralighty, etExpiryVfrUltralighty, etExpiryInstruktor, etExpiryVlekar, etExpirySkusobnyPilot, etExpiryVysadzovac;

    // Medical - Ultralighty
    private CheckBox chkOsvedcenieUltralighty;
    private EditText etExpiryOsvedcenieUltralighty;

    // Rádio - Ultralighty
    private CheckBox chkVseobecnyPreukazUltralighty, chkObmedzenyPreukazUltralighty;
    private EditText etExpiryVseobecnyPreukazUltralighty, etExpiryObmedzenyPreukazUltralighty;

    // Angličtina - Ultralighty
    private CheckBox chkEnglishLevel4Ultralighty, chkEnglishLevel5Ultralighty, chkEnglishLevel6Ultralighty;
    private EditText etExpiryEnglishLevel4Ultralighty, etExpiryEnglishLevel5Ultralighty, etExpiryEnglishLevel6Ultralighty;

    // Poistka - Ultralighty
    private CheckBox chkPoistenieZodpovednostCRSKUltralighty, chkZodpovednostiSvetUltralighty;
    private EditText etExpiryPoistenieZodpovednostCRSKUltralighty, etExpiryZodpovednostiSvetUltralighty;

    // Common note field and Add button
    private EditText etNote;
    private Button btnAddCertificate;

    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ultralighty, container, false);

        // Inicializácia UI komponentov – Letecké kvalifikácie
        chkUllUltralighty = view.findViewById(R.id.chk_ull_ultralighty);
        etExpiryUllUltralighty = view.findViewById(R.id.et_expiry_ull_ultralighty);

        chkVfrUltralighty = view.findViewById(R.id.chk_vfr_ultralighty);
        etExpiryVfrUltralighty = view.findViewById(R.id.et_expiry_vfr_ultralighty);

        chkInstruktor = view.findViewById(R.id.chk_instruktor);
        etExpiryInstruktor = view.findViewById(R.id.et_expiry_instruktor);

        chkVlekar = view.findViewById(R.id.chk_vlekar);
        etExpiryVlekar = view.findViewById(R.id.et_expiry_vlekar);

        chkSkusobnyPilot = view.findViewById(R.id.chk_skusobny_pilot);
        etExpirySkusobnyPilot = view.findViewById(R.id.et_expiry_skusobny_pilot);

        chkVysadzovac = view.findViewById(R.id.chk_vysadzovac);
        etExpiryVysadzovac = view.findViewById(R.id.et_expiry_vysadzovac);

        // Medical
        chkOsvedcenieUltralighty = view.findViewById(R.id.chk_osvedcenie_ultralighty);
        etExpiryOsvedcenieUltralighty = view.findViewById(R.id.et_expiry_osvedcenie_ultralighty);

        // Rádio
        chkVseobecnyPreukazUltralighty = view.findViewById(R.id.chk_vseobecny_preukaz_ultralighty);
        etExpiryVseobecnyPreukazUltralighty = view.findViewById(R.id.et_expiry_vseobecny_preukaz_ultralighty);

        chkObmedzenyPreukazUltralighty = view.findViewById(R.id.chk_obmedzeny_preukaz_ultralighty);
        etExpiryObmedzenyPreukazUltralighty = view.findViewById(R.id.et_expiry_obmedzeny_preukaz_ultralighty);

        // Angličtina
        chkEnglishLevel4Ultralighty = view.findViewById(R.id.chk_english_level4_ultralighty);
        etExpiryEnglishLevel4Ultralighty = view.findViewById(R.id.et_expiry_english_level4_ultralighty);

        chkEnglishLevel5Ultralighty = view.findViewById(R.id.chk_english_level5_ultralighty);
        etExpiryEnglishLevel5Ultralighty = view.findViewById(R.id.et_expiry_english_level5_ultralighty);

        chkEnglishLevel6Ultralighty = view.findViewById(R.id.chk_english_level6_ultralighty);
        etExpiryEnglishLevel6Ultralighty = view.findViewById(R.id.et_expiry_english_level6_ultralighty);

        // Poistka
        chkPoistenieZodpovednostCRSKUltralighty = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_ultralighty);
        etExpiryPoistenieZodpovednostCRSKUltralighty = view.findViewById(R.id.et_expiry_poistenie_zodpovednost_cr_sk_ultralighty);

        chkZodpovednostiSvetUltralighty = view.findViewById(R.id.chk_zodpovednosti_svet_ultralighty);
        etExpiryZodpovednostiSvetUltralighty = view.findViewById(R.id.et_expiry_zodpovednosti_svet_ultralighty);

        // Note a tlačidlo
        etNote = view.findViewById(R.id.et_note_ultralighty);
        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);

        // Nastavenie viditeľnosti expiry EditTextov na základe stavu checkboxov
        setupExpiryForCheckbox(chkUllUltralighty, etExpiryUllUltralighty);
        setupExpiryForCheckbox(chkVfrUltralighty, etExpiryVfrUltralighty);
        setupExpiryForCheckbox(chkInstruktor, etExpiryInstruktor);
        setupExpiryForCheckbox(chkVlekar, etExpiryVlekar);
        setupExpiryForCheckbox(chkSkusobnyPilot, etExpirySkusobnyPilot);
        setupExpiryForCheckbox(chkVysadzovac, etExpiryVysadzovac);
        setupExpiryForCheckbox(chkOsvedcenieUltralighty, etExpiryOsvedcenieUltralighty);
        setupExpiryForCheckbox(chkVseobecnyPreukazUltralighty, etExpiryVseobecnyPreukazUltralighty);
        setupExpiryForCheckbox(chkObmedzenyPreukazUltralighty, etExpiryObmedzenyPreukazUltralighty);
        setupExpiryForCheckbox(chkEnglishLevel4Ultralighty, etExpiryEnglishLevel4Ultralighty);
        setupExpiryForCheckbox(chkEnglishLevel5Ultralighty, etExpiryEnglishLevel5Ultralighty);
        setupExpiryForCheckbox(chkEnglishLevel6Ultralighty, etExpiryEnglishLevel6Ultralighty);
        setupExpiryForCheckbox(chkPoistenieZodpovednostCRSKUltralighty, etExpiryPoistenieZodpovednostCRSKUltralighty);
        setupExpiryForCheckbox(chkZodpovednostiSvetUltralighty, etExpiryZodpovednostiSvetUltralighty);

        // Nastavenie dynamického formátovania dátumu
        setExpiryTextWatcher(etExpiryUllUltralighty);
        setExpiryTextWatcher(etExpiryVfrUltralighty);
        setExpiryTextWatcher(etExpiryInstruktor);
        setExpiryTextWatcher(etExpiryVlekar);
        setExpiryTextWatcher(etExpirySkusobnyPilot);
        setExpiryTextWatcher(etExpiryVysadzovac);
        setExpiryTextWatcher(etExpiryOsvedcenieUltralighty);
        setExpiryTextWatcher(etExpiryVseobecnyPreukazUltralighty);
        setExpiryTextWatcher(etExpiryObmedzenyPreukazUltralighty);
        setExpiryTextWatcher(etExpiryEnglishLevel4Ultralighty);
        setExpiryTextWatcher(etExpiryEnglishLevel5Ultralighty);
        setExpiryTextWatcher(etExpiryEnglishLevel6Ultralighty);
        setExpiryTextWatcher(etExpiryPoistenieZodpovednostCRSKUltralighty);
        setExpiryTextWatcher(etExpiryZodpovednostiSvetUltralighty);

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

    // Formátuje vstup na formát yyyy-MM-dd.
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

    // Pomocná metóda: Overí expiry dátum pre zaškrtnutý checkbox.
    // Ak je checkbox zaškrtnutý, overí vstup a v prípade neplatného zobrazí Toast.
    private String requireExpiry(CheckBox checkbox, EditText expiryEditText, String fieldName) {
        if (checkbox.isChecked()) {
            String expiry = expiryEditText.getText().toString().trim();
            if (expiry.equals("0") || expiry.equals("0-") || expiry.equals("0-0") || expiry.equals("0-0-0") || expiry.isEmpty()) {
                Toast.makeText(getActivity(), "Pre " + fieldName + " je potrebné zadať platný dátum expirácie", Toast.LENGTH_SHORT).show();
                return null;
            }
            return expiry;
        }
        return null;
    }

    // Hlavná metóda: Zhromažďuje údaje, vytvára objekty Certificate, ukladá ich lokálne a odosiela na server.
    private void addCertificate() {
        // Overenie, či je vybraný aspoň jeden typ kvalifikácie.
        if (!chkUllUltralighty.isChecked() && !chkVfrUltralighty.isChecked() &&
                !chkInstruktor.isChecked() && !chkVlekar.isChecked() &&
                !chkSkusobnyPilot.isChecked() && !chkVysadzovac.isChecked() &&
                !chkOsvedcenieUltralighty.isChecked() &&
                !chkVseobecnyPreukazUltralighty.isChecked() && !chkObmedzenyPreukazUltralighty.isChecked() &&
                !chkEnglishLevel4Ultralighty.isChecked() && !chkEnglishLevel5Ultralighty.isChecked() &&
                !chkEnglishLevel6Ultralighty.isChecked() &&
                !chkPoistenieZodpovednostCRSKUltralighty.isChecked() && !chkZodpovednostiSvetUltralighty.isChecked()) {
            Toast.makeText(getActivity(), "Prosím, vyberte aspoň jeden typ kvalifikácie", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etNote.getText().toString().trim();

        List<Certificate> certificatesToAdd = new ArrayList<>();

        // Letecké kvalifikácie - Ultralighty
        if (chkUllUltralighty.isChecked()) {
            String expiry = requireExpiry(chkUllUltralighty, etExpiryUllUltralighty, "ULL(a)");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "ULL(a)", expiry, note));
        }
        if (chkVfrUltralighty.isChecked()) {
            String expiry = requireExpiry(chkVfrUltralighty, etExpiryVfrUltralighty, "Riadené lety VFR");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "Riadené lety VFR", expiry, note));
        }
        if (chkInstruktor.isChecked()) {
            String expiry = requireExpiry(chkInstruktor, etExpiryInstruktor, "Inštruktor");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "Inštruktor", expiry, note));
        }
        if (chkVlekar.isChecked()) {
            String expiry = requireExpiry(chkVlekar, etExpiryVlekar, "Vlekár");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "Vlekár", expiry, note));
        }
        if (chkSkusobnyPilot.isChecked()) {
            String expiry = requireExpiry(chkSkusobnyPilot, etExpirySkusobnyPilot, "Skúšobný pilot");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "Skúšobný pilot", expiry, note));
        }
        if (chkVysadzovac.isChecked()) {
            String expiry = requireExpiry(chkVysadzovac, etExpiryVysadzovac, "Vysadzovač");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Letecké kvalifikácie", "Ultralighty", "Vysadzovač", expiry, note));
        }

        // Medical - Ultralighty
        if (chkOsvedcenieUltralighty.isChecked()) {
            String expiry = requireExpiry(chkOsvedcenieUltralighty, etExpiryOsvedcenieUltralighty, "Osvedčenie zdravotnej spôsobilosti 2. triedy");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Medical", "Ultralighty", "Osvedčenie zdravotnej spôsobilosti 2. triedy", expiry, note));
        }

        // Rádio - Ultralighty
        if (chkVseobecnyPreukazUltralighty.isChecked()) {
            String expiry = requireExpiry(chkVseobecnyPreukazUltralighty, etExpiryVseobecnyPreukazUltralighty, "Všeobecný preukaz radiofonisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Ultralighty", "Všeobecný preukaz radiofonisty", expiry, note));
        }
        if (chkObmedzenyPreukazUltralighty.isChecked()) {
            String expiry = requireExpiry(chkObmedzenyPreukazUltralighty, etExpiryObmedzenyPreukazUltralighty, "Obmedzený preukaz radiofonisty");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Rádio", "Ultralighty", "Obmedzený preukaz radiofonisty", expiry, note));
        }

        // Angličtina - Ultralighty
        if (chkEnglishLevel4Ultralighty.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel4Ultralighty, etExpiryEnglishLevel4Ultralighty, "ICAO English LEVEL 4");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Ultralighty", "ICAO English LEVEL 4", expiry, note));
        }
        if (chkEnglishLevel5Ultralighty.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel5Ultralighty, etExpiryEnglishLevel5Ultralighty, "ICAO English LEVEL 5");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Ultralighty", "ICAO English LEVEL 5", expiry, note));
        }
        if (chkEnglishLevel6Ultralighty.isChecked()) {
            String expiry = requireExpiry(chkEnglishLevel6Ultralighty, etExpiryEnglishLevel6Ultralighty, "ICAO English LEVEL 6");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Angličtina", "Ultralighty", "ICAO English LEVEL 6", expiry, note));
        }

        // Poistka - Ultralighty
        if (chkPoistenieZodpovednostCRSKUltralighty.isChecked()) {
            String expiry = requireExpiry(chkPoistenieZodpovednostCRSKUltralighty, etExpiryPoistenieZodpovednostCRSKUltralighty, "Poistenie zodpovednosti ČR + SK");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Ultralighty", "Poistenie zodpovednosti ČR + SK", expiry, note));
        }
        if (chkZodpovednostiSvetUltralighty.isChecked()) {
            String expiry = requireExpiry(chkZodpovednostiSvetUltralighty, etExpiryZodpovednostiSvetUltralighty, "Poistenie zodpovednosti SVET");
            if (expiry != null)
                certificatesToAdd.add(new Certificate("Poistka", "Ultralighty", "Poistenie zodpovednosti SVET", expiry, note));
        }

        if (certificatesToAdd.isEmpty()) {
            Toast.makeText(getActivity(), "Prosím, vyberte aspoň jeden typ kvalifikácie a zadajte dátum expirácie", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vloženie certifikátov do lokálnej databázy a odoslanie na server, ak je dostupná sieť.
        Executors.newSingleThreadExecutor().execute(() -> {
            for (Certificate cert : certificatesToAdd) {
                database.certificateDao().insertCertificate(cert);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Kvalifikácia/y pridaná/e!", Toast.LENGTH_SHORT).show();
                    clearFields();
                });
            }
            if (isWiFiConnected()) {
                for (Certificate cert : certificatesToAdd) {
                    sendCertificateToServer(cert);
                }
            } else {
                Log.d("AddUltralightyFragment", "Nedostupná sieť, certifikáty uložené len lokálne");
            }
        });
    }

    // Vyčistenie všetkých vstupných polí a reset checkboxov.
    private void clearFields() {
        etExpiryUllUltralighty.setText("");
        etExpiryVfrUltralighty.setText("");
        etExpiryInstruktor.setText("");
        etExpiryVlekar.setText("");
        etExpirySkusobnyPilot.setText("");
        etExpiryVysadzovac.setText("");

        etExpiryOsvedcenieUltralighty.setText("");

        etExpiryVseobecnyPreukazUltralighty.setText("");
        etExpiryObmedzenyPreukazUltralighty.setText("");

        etExpiryEnglishLevel4Ultralighty.setText("");
        etExpiryEnglishLevel5Ultralighty.setText("");
        etExpiryEnglishLevel6Ultralighty.setText("");

        etExpiryPoistenieZodpovednostCRSKUltralighty.setText("");
        etExpiryZodpovednostiSvetUltralighty.setText("");

        etNote.setText("");

        chkUllUltralighty.setChecked(false);
        chkVfrUltralighty.setChecked(false);
        chkInstruktor.setChecked(false);
        chkVlekar.setChecked(false);
        chkSkusobnyPilot.setChecked(false);
        chkVysadzovac.setChecked(false);
        chkOsvedcenieUltralighty.setChecked(false);
        chkVseobecnyPreukazUltralighty.setChecked(false);
        chkObmedzenyPreukazUltralighty.setChecked(false);
        chkEnglishLevel4Ultralighty.setChecked(false);
        chkEnglishLevel5Ultralighty.setChecked(false);
        chkEnglishLevel6Ultralighty.setChecked(false);
        chkPoistenieZodpovednostCRSKUltralighty.setChecked(false);
        chkZodpovednostiSvetUltralighty.setChecked(false);
    }

    // Kontrola, či je dostupné pripojenie na WiFi.
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
                    Toast.makeText(getActivity(), "Certifikát zosynchronizovaný", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Retrofit", "Chybná odpoveď: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Retrofit", "Chyba pri odosielaní certifikátu: " + t.getMessage());
            }
        });
    }
}
