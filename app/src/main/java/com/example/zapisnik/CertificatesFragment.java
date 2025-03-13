package com.example.zapisnik;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificatesFragment extends Fragment {

    // Tlačidlá pre výber platformy
    private Button btnPlatformVelkaEra, btnPlatformUltralighty, btnPlatformVetrone, btnPlatformVrtulniky;
    // Tlačidlá pre výber sekcie
    private Button btnSectionLk, btnSectionMedical, btnSectionRadio, btnSectionEnglish, btnSectionInsurance;

    // Premenné pre uloženie vybraných hodnôt
    private String selectedPlatform = "Velká éra";    // predvolená hodnota
    private String selectedSection = "Letecké kvalifikácie";  // predvolená hodnota

    // EditTexty
    private EditText etAcquiredDate, etExpiryDate, etDaysRemaining, etNote;

    // ===================== Platforma "Velká éra" =====================
    // Letecké kvalifikácie - Velká éra
    private LinearLayout layoutLkVelkaEra;
    private CheckBox chkPpl, chkCpl, chkAtpl, chkSepLand, chkSepSea, chkMepLand, chkMepSea,
            chkIrSep, chkIr, chkMep, chkNight, chkFi, chkIri, chkTow, chkTst, chkPar, chkAcr, chkMcc;
    // Medical - Velká éra
    private LinearLayout layoutMedicalVelkaEra;
    private CheckBox chkMedClass1, chkMedClass2;
    // Rádio - Velká éra
    private LinearLayout layoutRadioVelkaEra;
    private CheckBox chkVseobecnyPreukaz, chkObmedzenyPreukaz;
    // Angličtina - Velká éra
    private LinearLayout layoutAnglictinaVelkaEra;
    private CheckBox chkEnglishLevel4, chkEnglishLevel5, chkEnglishLevel6, chkEnglishIfr;
    // Poistka - Velká éra
    private LinearLayout layoutPoistkaVelkaEra;
    private CheckBox chkPoistenieZodpovednostCRSK, chkZodpovednostiSvet;

    // ===================== Platforma "Ultralighty" =====================
    // Letecké kvalifikácie - Ultralighty
    private LinearLayout layoutLkUltralighty;
    private CheckBox chkUll, chkVfr, chkInstruktor, chkVlekar, chkSkusobnyPilot, chkVysadzovac;
    // Medical - Ultralighty
    private LinearLayout layoutMedicalUltralighty;
    private CheckBox chkOsvedcenie;
    // Rádio - Ultralighty
    private LinearLayout layoutRadioUltralighty;
    private CheckBox chkVseobecnyPreukazUltralighty, chkObmedzenyPreukazUltralighty;
    // Angličtina - Ultralighty
    private LinearLayout layoutAnglictinaUltralighty;
    private CheckBox chkEnglishLevel4Ultralighty, chkEnglishLevel5Ultralighty, chkEnglishLevel6Ultralighty;
    // Poistka - Ultralighty
    private LinearLayout layoutPoistkaUltralighty;
    private CheckBox chkPoistenieZodpovednostCRSKUltralighty, chkZodpovednostiSvetUltralighty;

    // ===================== Platforma "Vetrone" =====================
    // Letecké kvalifikácie - Vetrone
    private LinearLayout layoutLkVetrone;
    private CheckBox chkGldVetrone, chkTmgVetrone, chkFiVetrone, chkTstVetrone;
    // Medical - Vetrone
    private LinearLayout layoutMedicalVetrone;
    private CheckBox chkMedClass1Vetrone, chkMedClass2Vetrone;
    // Rádio - Vetrone
    private LinearLayout layoutRadioVetrone;
    private CheckBox chkVseobecnyPreukazVetrone, chkObmedzenyPreukazVetrone;
    // Angličtina - Vetrone
    private LinearLayout layoutAnglictinaVetrone;
    private CheckBox chkEnglishLevel4Vetrone, chkEnglishLevel5Vetrone, chkEnglishLevel6Vetrone;
    // Poistka - Vetrone
    private LinearLayout layoutPoistkaVetrone;
    private CheckBox chkPoistenieZodpovednostCRSKVetrone, chkZodpovednostiSvetVetrone;

    // ===================== Platforma "Vrtuľníky" =====================
    // Letecké kvalifikácie - Vrtuľníky
    private LinearLayout layoutLkVrtulniky;
    private CheckBox chkPplVrtulniky, chkCplVrtulniky, chkAtplVrtulniky, chkIrVrtulniky,
            chkNightVrtulniky, chkFiVrtulniky, chkIriVrtulniky, chkTstVrtulniky, chkParVrtulniky;
    // Medical - Vrtuľníky
    private LinearLayout layoutMedicalVrtulniky;
    private CheckBox chkMedClass1Vrtulniky, chkMedClass2Vrtulniky;
    // Rádio - Vrtuľníky
    private LinearLayout layoutRadioVrtulniky;
    private CheckBox chkVseobecnyPreukazVrtulniky, chkObmedzenyPreukazVrtulniky;
    // Angličtina - Vrtuľníky
    private LinearLayout layoutAnglictinaVrtulniky;
    private CheckBox chkEnglishLevel4Vrtulniky, chkEnglishLevel5Vrtulniky, chkEnglishLevel6Vrtulniky;
    // Poistka - Vrtuľníky
    private LinearLayout layoutPoistkaVrtulniky;
    private CheckBox chkPoistenieZodpovednostCRSKVrtulniky, chkZodpovednostiSvetVrtulniky;

    private Button btnAddCertificate;
    private CertificateDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificates, container, false);

        // Inicializácia tlačidiel pre výber platformy
        btnPlatformVelkaEra = view.findViewById(R.id.btn_platform_velka_era);
        btnPlatformUltralighty = view.findViewById(R.id.btn_platform_ultralighty);
        btnPlatformVetrone = view.findViewById(R.id.btn_platform_vetrone);
        btnPlatformVrtulniky = view.findViewById(R.id.btn_platform_vrtulniky);

        // Inicializácia tlačidiel pre výber sekcie
        btnSectionLk = view.findViewById(R.id.btn_section_lk);
        btnSectionMedical = view.findViewById(R.id.btn_section_medical);
        btnSectionRadio = view.findViewById(R.id.btn_section_radio);
        btnSectionEnglish = view.findViewById(R.id.btn_section_english);
        btnSectionInsurance = view.findViewById(R.id.btn_section_insurance);

        // Listener pre tlačidlá platformy
        btnPlatformVelkaEra.setOnClickListener(v -> {
            selectedPlatform = "Velká éra";
            updateCheckboxVisibility();
        });
        btnPlatformUltralighty.setOnClickListener(v -> {
            selectedPlatform = "Ultralighty";
            updateCheckboxVisibility();
        });
        btnPlatformVetrone.setOnClickListener(v -> {
            selectedPlatform = "Vetrone";
            updateCheckboxVisibility();
        });
        btnPlatformVrtulniky.setOnClickListener(v -> {
            selectedPlatform = "Vrtuľníky";
            updateCheckboxVisibility();
        });

        // Listener pre tlačidlá sekcie
        btnSectionLk.setOnClickListener(v -> {
            selectedSection = "Letecké kvalifikácie";
            updateCheckboxVisibility();
        });
        btnSectionMedical.setOnClickListener(v -> {
            selectedSection = "Medical";
            updateCheckboxVisibility();
        });
        btnSectionRadio.setOnClickListener(v -> {
            selectedSection = "Rádio";
            updateCheckboxVisibility();
        });
        btnSectionEnglish.setOnClickListener(v -> {
            selectedSection = "Angličtina";
            updateCheckboxVisibility();
        });
        btnSectionInsurance.setOnClickListener(v -> {
            selectedSection = "Poistka";
            updateCheckboxVisibility();
        });

        // Inicializácia EditTextov
        etAcquiredDate = view.findViewById(R.id.et_acquired_date);
        etExpiryDate = view.findViewById(R.id.et_expiry_date);
        etDaysRemaining = view.findViewById(R.id.et_days_remaining);
        etNote = view.findViewById(R.id.et_note);

        // ===================== Inicializácia pre "Velká éra" =====================
        layoutLkVelkaEra = view.findViewById(R.id.layout_lk_velka_era);
        chkPpl = view.findViewById(R.id.chk_ppl);
        chkCpl = view.findViewById(R.id.chk_cpl);
        chkAtpl = view.findViewById(R.id.chk_atpl);
        chkSepLand = view.findViewById(R.id.chk_sep_land);
        chkSepSea = view.findViewById(R.id.chk_sep_sea);
        chkMepLand = view.findViewById(R.id.chk_mep_land);
        chkMepSea = view.findViewById(R.id.chk_mep_sea);
        chkIrSep = view.findViewById(R.id.chk_ir_sep);
        chkIr = view.findViewById(R.id.chk_ir);
        chkMep = view.findViewById(R.id.chk_mep);
        chkNight = view.findViewById(R.id.chk_night);
        chkFi = view.findViewById(R.id.chk_fi);
        chkIri = view.findViewById(R.id.chk_iri);
        chkTow = view.findViewById(R.id.chk_tow);
        chkTst = view.findViewById(R.id.chk_tst);
        chkPar = view.findViewById(R.id.chk_par);
        chkAcr = view.findViewById(R.id.chk_acr);
        chkMcc = view.findViewById(R.id.chk_mcc);

        layoutMedicalVelkaEra = view.findViewById(R.id.layout_medical_velka_era);
        chkMedClass1 = view.findViewById(R.id.chk_med_class1);
        chkMedClass2 = view.findViewById(R.id.chk_med_class2);

        layoutRadioVelkaEra = view.findViewById(R.id.layout_radio_velka_era);
        chkVseobecnyPreukaz = view.findViewById(R.id.chk_vseobecny_preukaz);
        chkObmedzenyPreukaz = view.findViewById(R.id.chk_obmedzeny_preukaz);

        layoutAnglictinaVelkaEra = view.findViewById(R.id.layout_anglictina_velka_era);
        chkEnglishLevel4 = view.findViewById(R.id.chk_english_level4);
        chkEnglishLevel5 = view.findViewById(R.id.chk_english_level5);
        chkEnglishLevel6 = view.findViewById(R.id.chk_english_level6);
        chkEnglishIfr = view.findViewById(R.id.chk_english_ifr);

        layoutPoistkaVelkaEra = view.findViewById(R.id.layout_poistka_velka_era);
        chkPoistenieZodpovednostCRSK = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk);
        chkZodpovednostiSvet = view.findViewById(R.id.chk_zodpovednosti_svet);

        // ===================== Inicializácia pre "Ultralighty" =====================
        layoutLkUltralighty = view.findViewById(R.id.layout_lk_ultralighty);
        chkUll = view.findViewById(R.id.chk_ull);
        chkVfr = view.findViewById(R.id.chk_vfr);
        chkInstruktor = view.findViewById(R.id.chk_instruktor);
        chkVlekar = view.findViewById(R.id.chk_vlekar);
        chkSkusobnyPilot = view.findViewById(R.id.chk_skusobny_pilot);
        chkVysadzovac = view.findViewById(R.id.chk_vysadzovac);

        layoutMedicalUltralighty = view.findViewById(R.id.layout_medical_ultralighty);
        chkOsvedcenie = view.findViewById(R.id.chk_osvedcenie);

        layoutRadioUltralighty = view.findViewById(R.id.layout_radio_ultralighty);
        chkVseobecnyPreukazUltralighty = view.findViewById(R.id.chk_vseobecny_preukaz_ultralighty);
        chkObmedzenyPreukazUltralighty = view.findViewById(R.id.chk_obmedzeny_preukaz_ultralighty);

        layoutAnglictinaUltralighty = view.findViewById(R.id.layout_anglictina_ultralighty);
        chkEnglishLevel4Ultralighty = view.findViewById(R.id.chk_english_level4_ultralighty);
        chkEnglishLevel5Ultralighty = view.findViewById(R.id.chk_english_level5_ultralighty);
        chkEnglishLevel6Ultralighty = view.findViewById(R.id.chk_english_level6_ultralighty);

        layoutPoistkaUltralighty = view.findViewById(R.id.layout_poistka_ultralighty);
        chkPoistenieZodpovednostCRSKUltralighty = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_ultralighty);
        chkZodpovednostiSvetUltralighty = view.findViewById(R.id.chk_zodpovednosti_svet_ultralighty);

        // ===================== Inicializácia pre "Vetrone" =====================
        layoutLkVetrone = view.findViewById(R.id.layout_lk_vetrone);
        chkGldVetrone = view.findViewById(R.id.chk_gld_vetrone);
        chkTmgVetrone = view.findViewById(R.id.chk_tmg_vetrone);
        chkFiVetrone = view.findViewById(R.id.chk_fi_vetrone);
        chkTstVetrone = view.findViewById(R.id.chk_tst_vetrone);

        layoutMedicalVetrone = view.findViewById(R.id.layout_medical_vetrone);
        chkMedClass1Vetrone = view.findViewById(R.id.chk_med_class1_vetrone);
        chkMedClass2Vetrone = view.findViewById(R.id.chk_med_class2_vetrone);

        layoutRadioVetrone = view.findViewById(R.id.layout_radio_vetrone);
        chkVseobecnyPreukazVetrone = view.findViewById(R.id.chk_vseobecny_preukaz_vetrone);
        chkObmedzenyPreukazVetrone = view.findViewById(R.id.chk_obmedzeny_preukaz_vetrone);

        layoutAnglictinaVetrone = view.findViewById(R.id.layout_anglictina_vetrone);
        chkEnglishLevel4Vetrone = view.findViewById(R.id.chk_english_level4_vetrone);
        chkEnglishLevel5Vetrone = view.findViewById(R.id.chk_english_level5_vetrone);
        chkEnglishLevel6Vetrone = view.findViewById(R.id.chk_english_level6_vetrone);

        layoutPoistkaVetrone = view.findViewById(R.id.layout_poistka_vetrone);
        chkPoistenieZodpovednostCRSKVetrone = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_vetrone);
        chkZodpovednostiSvetVetrone = view.findViewById(R.id.chk_zodpovednosti_svet_vetrone);

        // ===================== Inicializácia pre "Vrtuľníky" =====================
        layoutLkVrtulniky = view.findViewById(R.id.layout_lk_vrtulniky);
        chkPplVrtulniky = view.findViewById(R.id.chk_ppl_vrtulniky);
        chkCplVrtulniky = view.findViewById(R.id.chk_cpl_vrtulniky);
        chkAtplVrtulniky = view.findViewById(R.id.chk_atpl_vrtulniky);
        chkIrVrtulniky = view.findViewById(R.id.chk_ir_vrtulniky);
        chkNightVrtulniky = view.findViewById(R.id.chk_night_vrtulniky);
        chkFiVrtulniky = view.findViewById(R.id.chk_fi_vrtulniky);
        chkIriVrtulniky = view.findViewById(R.id.chk_iri_vrtulniky);
        chkTstVrtulniky = view.findViewById(R.id.chk_tst_vrtulniky);
        chkParVrtulniky = view.findViewById(R.id.chk_par_vrtulniky);

        layoutMedicalVrtulniky = view.findViewById(R.id.layout_medical_vrtulniky);
        chkMedClass1Vrtulniky = view.findViewById(R.id.chk_med_class1_vrtulniky);
        chkMedClass2Vrtulniky = view.findViewById(R.id.chk_med_class2_vrtulniky);

        layoutRadioVrtulniky = view.findViewById(R.id.layout_radio_vrtulniky);
        chkVseobecnyPreukazVrtulniky = view.findViewById(R.id.chk_vseobecny_preukaz_vrtulniky);
        chkObmedzenyPreukazVrtulniky = view.findViewById(R.id.chk_obmedzeny_preukaz_vrtulniky);

        layoutAnglictinaVrtulniky = view.findViewById(R.id.layout_anglictina_vrtulniky);
        chkEnglishLevel4Vrtulniky = view.findViewById(R.id.chk_english_level4_vrtulniky);
        chkEnglishLevel5Vrtulniky = view.findViewById(R.id.chk_english_level5_vrtulniky);
        chkEnglishLevel6Vrtulniky = view.findViewById(R.id.chk_english_level6_vrtulniky);

        layoutPoistkaVrtulniky = view.findViewById(R.id.layout_poistka_vrtulniky);
        chkPoistenieZodpovednostCRSKVrtulniky = view.findViewById(R.id.chk_poistenie_zodpovednost_cr_sk_vrtulniky);
        chkZodpovednostiSvetVrtulniky = view.findViewById(R.id.chk_zodpovednosti_svet_vrtulniky);

        // TextWatcher pre dynamické formátovanie dátumu expirácie
        etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String formatted = formatDateInput(s.toString());
                if (!formatted.equals(s.toString())) {
                    etExpiryDate.setText(formatted);
                    etExpiryDate.setSelection(formatted.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnAddCertificate = view.findViewById(R.id.btn_add_certificate);
        btnAddCertificate.setOnClickListener(v -> addCertificate());

        database = CertificateDatabase.getInstance(getActivity());
        updateCheckboxVisibility();
        return view;
    }

    private void updateCheckboxVisibility() {
        // Najprv skryť všetky layouty pre všetky platformy
        // Velká éra
        layoutLkVelkaEra.setVisibility(View.GONE);
        layoutMedicalVelkaEra.setVisibility(View.GONE);
        layoutRadioVelkaEra.setVisibility(View.GONE);
        layoutAnglictinaVelkaEra.setVisibility(View.GONE);
        layoutPoistkaVelkaEra.setVisibility(View.GONE);
        // Ultralighty
        layoutLkUltralighty.setVisibility(View.GONE);
        layoutMedicalUltralighty.setVisibility(View.GONE);
        layoutRadioUltralighty.setVisibility(View.GONE);
        layoutAnglictinaUltralighty.setVisibility(View.GONE);
        layoutPoistkaUltralighty.setVisibility(View.GONE);
        // Vetrone
        layoutLkVetrone.setVisibility(View.GONE);
        layoutMedicalVetrone.setVisibility(View.GONE);
        layoutRadioVetrone.setVisibility(View.GONE);
        layoutAnglictinaVetrone.setVisibility(View.GONE);
        layoutPoistkaVetrone.setVisibility(View.GONE);
        // Vrtuľníky
        layoutLkVrtulniky.setVisibility(View.GONE);
        layoutMedicalVrtulniky.setVisibility(View.GONE);
        layoutRadioVrtulniky.setVisibility(View.GONE);
        layoutAnglictinaVrtulniky.setVisibility(View.GONE);
        layoutPoistkaVrtulniky.setVisibility(View.GONE);

        if (selectedPlatform.equals("Velká éra")) {
            switch (selectedSection) {
                case "Letecké kvalifikácie":
                    layoutLkVelkaEra.setVisibility(View.VISIBLE);
                    break;
                case "Medical":
                    layoutMedicalVelkaEra.setVisibility(View.VISIBLE);
                    break;
                case "Rádio":
                    layoutRadioVelkaEra.setVisibility(View.VISIBLE);
                    break;
                case "Angličtina":
                    layoutAnglictinaVelkaEra.setVisibility(View.VISIBLE);
                    break;
                case "Poistka":
                    layoutPoistkaVelkaEra.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (selectedPlatform.equals("Ultralighty")) {
            switch (selectedSection) {
                case "Letecké kvalifikácie":
                    layoutLkUltralighty.setVisibility(View.VISIBLE);
                    break;
                case "Medical":
                    layoutMedicalUltralighty.setVisibility(View.VISIBLE);
                    break;
                case "Rádio":
                    layoutRadioUltralighty.setVisibility(View.VISIBLE);
                    break;
                case "Angličtina":
                    layoutAnglictinaUltralighty.setVisibility(View.VISIBLE);
                    break;
                case "Poistka":
                    layoutPoistkaUltralighty.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (selectedPlatform.equals("Vetrone")) {
            switch (selectedSection) {
                case "Letecké kvalifikácie":
                    layoutLkVetrone.setVisibility(View.VISIBLE);
                    break;
                case "Medical":
                    layoutMedicalVetrone.setVisibility(View.VISIBLE);
                    break;
                case "Rádio":
                    layoutRadioVetrone.setVisibility(View.VISIBLE);
                    break;
                case "Angličtina":
                    layoutAnglictinaVetrone.setVisibility(View.VISIBLE);
                    break;
                case "Poistka":
                    layoutPoistkaVetrone.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (selectedPlatform.equals("Vrtuľníky")) {
            switch (selectedSection) {
                case "Letecké kvalifikácie":
                    layoutLkVrtulniky.setVisibility(View.VISIBLE);
                    break;
                case "Medical":
                    layoutMedicalVrtulniky.setVisibility(View.VISIBLE);
                    break;
                case "Rádio":
                    layoutRadioVrtulniky.setVisibility(View.VISIBLE);
                    break;
                case "Angličtina":
                    layoutAnglictinaVrtulniky.setVisibility(View.VISIBLE);
                    break;
                case "Poistka":
                    layoutPoistkaVrtulniky.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void addCertificate() {
        String acquiredDate = etAcquiredDate.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String daysRemainingStr = etDaysRemaining.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (acquiredDate.isEmpty()) {
            acquiredDate = sdf.format(new Date());
        }
        int daysRemaining = 0;
        if (daysRemainingStr.isEmpty()) {
            daysRemaining = calculateDaysRemaining(expiryDate, sdf);
        } else {
            try {
                daysRemaining = Integer.parseInt(daysRemainingStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid days remaining", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<Certificate> certificatesToAdd = new ArrayList<>();

        // ===================== Pre "Velká éra" =====================
        if (selectedPlatform.equals("Velká éra")) {
            if (selectedSection.equals("Letecké kvalifikácie") && layoutLkVelkaEra.getVisibility() == View.VISIBLE) {
                if (chkPpl.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "PPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkCpl.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "CPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkAtpl.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ATPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkSepLand.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "SEP land", acquiredDate, expiryDate, daysRemaining, note));
                if (chkSepSea.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "SEP sea", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMepLand.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "MEP land", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMepSea.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "MEP sea", acquiredDate, expiryDate, daysRemaining, note));
                if (chkIrSep.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IR SEP", acquiredDate, expiryDate, daysRemaining, note));
                if (chkIr.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IR", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMep.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "MEP", acquiredDate, expiryDate, daysRemaining, note));
                if (chkNight.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "NIGHT", acquiredDate, expiryDate, daysRemaining, note));
                if (chkFi.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "FI", acquiredDate, expiryDate, daysRemaining, note));
                if (chkIri.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IRI", acquiredDate, expiryDate, daysRemaining, note));
                if (chkTow.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "TOW", acquiredDate, expiryDate, daysRemaining, note));
                if (chkTst.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "TST", acquiredDate, expiryDate, daysRemaining, note));
                if (chkPar.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "PAR", acquiredDate, expiryDate, daysRemaining, note));
                if (chkAcr.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ACR", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMcc.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "MCC", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Medical") && layoutMedicalVelkaEra.getVisibility() == View.VISIBLE) {
                if (chkMedClass1.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 1", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMedClass2.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 2", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Rádio") && layoutRadioVelkaEra.getVisibility() == View.VISIBLE) {
                if (chkVseobecnyPreukaz.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Všeobecný preukaz radiofonisty", acquiredDate, expiryDate, daysRemaining, note));
                if (chkObmedzenyPreukaz.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Obmedzený preukaz radionofisty", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Angličtina") && layoutAnglictinaVelkaEra.getVisibility() == View.VISIBLE) {
                if (chkEnglishLevel4.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 4", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel5.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 5", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel6.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 6", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishIfr.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IFR angličtina", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Poistka") && layoutPoistkaVelkaEra.getVisibility() == View.VISIBLE) {
                if (chkPoistenieZodpovednostCRSK.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti ČR + SK", acquiredDate, expiryDate, daysRemaining, note));
                if (chkZodpovednostiSvet.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti SVET", acquiredDate, expiryDate, daysRemaining, note));
            }
        }

        // ===================== Pre "Ultralighty" =====================
        if (selectedPlatform.equals("Ultralighty")) {
            if (selectedSection.equals("Letecké kvalifikácie") && layoutLkUltralighty.getVisibility() == View.VISIBLE) {
                if (chkUll.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ULL(a)", acquiredDate, expiryDate, daysRemaining, note));
                if (chkVfr.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Riadené lety VFR", acquiredDate, expiryDate, daysRemaining, note));
                if (chkInstruktor.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Inštruktor", acquiredDate, expiryDate, daysRemaining, note));
                if (chkVlekar.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Vlekár", acquiredDate, expiryDate, daysRemaining, note));
                if (chkSkusobnyPilot.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Skúšobný pilot", acquiredDate, expiryDate, daysRemaining, note));
                if (chkVysadzovac.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Vysadzovač", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Medical") && layoutMedicalUltralighty.getVisibility() == View.VISIBLE) {
                if (chkOsvedcenie.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Osvedčenie zdravotnej spôsobilosti 2. triedy", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Rádio") && layoutRadioUltralighty.getVisibility() == View.VISIBLE) {
                if (chkVseobecnyPreukazUltralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Všeobecný preukaz radiofonisty", acquiredDate, expiryDate, daysRemaining, note));
                if (chkObmedzenyPreukazUltralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Obmedzený preukaz radionofisty", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Angličtina") && layoutAnglictinaUltralighty.getVisibility() == View.VISIBLE) {
                if (chkEnglishLevel4Ultralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 4", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel5Ultralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 5", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel6Ultralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 6", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Poistka") && layoutPoistkaUltralighty.getVisibility() == View.VISIBLE) {
                if (chkPoistenieZodpovednostCRSKUltralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti ČR + SK", acquiredDate, expiryDate, daysRemaining, note));
                if (chkZodpovednostiSvetUltralighty.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti SVET", acquiredDate, expiryDate, daysRemaining, note));
            }
        }

        // ===================== Pre "Vetrone" =====================
        if (selectedPlatform.equals("Vetrone")) {
            if (selectedSection.equals("Letecké kvalifikácie") && layoutLkVetrone.getVisibility() == View.VISIBLE) {
                if (chkGldVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "GLD", acquiredDate, expiryDate, daysRemaining, note));
                if (chkTmgVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "TMG", acquiredDate, expiryDate, daysRemaining, note));
                if (chkFiVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "FI", acquiredDate, expiryDate, daysRemaining, note));
                if (chkTstVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "TST", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Medical") && layoutMedicalVetrone.getVisibility() == View.VISIBLE) {
                if (chkMedClass1Vetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 1", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMedClass2Vetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 2", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Rádio") && layoutRadioVetrone.getVisibility() == View.VISIBLE) {
                if (chkVseobecnyPreukazVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Všeobecný preukaz radiofonisty", acquiredDate, expiryDate, daysRemaining, note));
                if (chkObmedzenyPreukazVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Obmedzený preukaz radionofisty", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Angličtina") && layoutAnglictinaVetrone.getVisibility() == View.VISIBLE) {
                if (chkEnglishLevel4Vetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 4", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel5Vetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 5", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel6Vetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 6", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Poistka") && layoutPoistkaVetrone.getVisibility() == View.VISIBLE) {
                if (chkPoistenieZodpovednostCRSKVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti ČR + SK", acquiredDate, expiryDate, daysRemaining, note));
                if (chkZodpovednostiSvetVetrone.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti SVET", acquiredDate, expiryDate, daysRemaining, note));
            }
        }

        // ===================== Pre "Vrtuľníky" =====================
        if (selectedPlatform.equals("Vrtuľníky")) {
            if (selectedSection.equals("Letecké kvalifikácie") && layoutLkVrtulniky.getVisibility() == View.VISIBLE) {
                if (chkPplVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "PPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkCplVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "CPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkAtplVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ATPL", acquiredDate, expiryDate, daysRemaining, note));
                if (chkIrVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IR", acquiredDate, expiryDate, daysRemaining, note));
                if (chkNightVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "NIGHT", acquiredDate, expiryDate, daysRemaining, note));
                if (chkFiVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "FI", acquiredDate, expiryDate, daysRemaining, note));
                if (chkIriVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "IRI", acquiredDate, expiryDate, daysRemaining, note));
                if (chkTstVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "TST", acquiredDate, expiryDate, daysRemaining, note));
                if (chkParVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "PAR", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Medical") && layoutMedicalVrtulniky.getVisibility() == View.VISIBLE) {
                if (chkMedClass1Vrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 1", acquiredDate, expiryDate, daysRemaining, note));
                if (chkMedClass2Vrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Medical Certificate Class 2", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Rádio") && layoutRadioVrtulniky.getVisibility() == View.VISIBLE) {
                if (chkVseobecnyPreukazVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Všeobecný preukaz radiofonisty", acquiredDate, expiryDate, daysRemaining, note));
                if (chkObmedzenyPreukazVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Obmedzený preukaz radionofisty", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Angličtina") && layoutAnglictinaVrtulniky.getVisibility() == View.VISIBLE) {
                if (chkEnglishLevel4Vrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 4", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel5Vrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 5", acquiredDate, expiryDate, daysRemaining, note));
                if (chkEnglishLevel6Vrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "ICAO English LEVEL 6", acquiredDate, expiryDate, daysRemaining, note));
            }
            if (selectedSection.equals("Poistka") && layoutPoistkaVrtulniky.getVisibility() == View.VISIBLE) {
                if (chkPoistenieZodpovednostCRSKVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti ČR + SK", acquiredDate, expiryDate, daysRemaining, note));
                if (chkZodpovednostiSvetVrtulniky.isChecked())
                    certificatesToAdd.add(new Certificate(selectedSection, selectedPlatform, "Poistenie zodpovednosti SVET", acquiredDate, expiryDate, daysRemaining, note));
            }
        }

        if (certificatesToAdd.isEmpty()) {
            Toast.makeText(getActivity(), "Please select at least one certificate type", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            for (Certificate cert : certificatesToAdd) {
                database.certificateDao().insertCertificate(cert);
            }
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Certificate(s) Added!", Toast.LENGTH_SHORT).show();
                clearFields();
                if (isWiFiConnected()) {
                    for (Certificate cert : certificatesToAdd) {
                        sendCertificateToServer(cert);
                    }
                }
            });
        });
    }

    private void clearFields() {
        etAcquiredDate.setText("");
        etExpiryDate.setText("");
        etDaysRemaining.setText("");
        etNote.setText("");
        // ===================== Reset checkboxov pre "Velká éra" =====================
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

        // ===================== Reset checkboxov pre "Ultralighty" =====================
        chkUll.setChecked(false);
        chkVfr.setChecked(false);
        chkInstruktor.setChecked(false);
        chkVlekar.setChecked(false);
        chkSkusobnyPilot.setChecked(false);
        chkVysadzovac.setChecked(false);
        chkOsvedcenie.setChecked(false);
        chkVseobecnyPreukazUltralighty.setChecked(false);
        chkObmedzenyPreukazUltralighty.setChecked(false);
        chkEnglishLevel4Ultralighty.setChecked(false);
        chkEnglishLevel5Ultralighty.setChecked(false);
        chkEnglishLevel6Ultralighty.setChecked(false);
        chkPoistenieZodpovednostCRSKUltralighty.setChecked(false);
        chkZodpovednostiSvetUltralighty.setChecked(false);

        // ===================== Reset checkboxov pre "Vetrone" =====================
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
        chkPoistenieZodpovednostCRSKVetrone.setChecked(false);
        chkZodpovednostiSvetVetrone.setChecked(false);

        // ===================== Reset checkboxov pre "Vrtuľníky" =====================
        chkPplVrtulniky.setChecked(false);
        chkCplVrtulniky.setChecked(false);
        chkAtplVrtulniky.setChecked(false);
        chkIrVrtulniky.setChecked(false);
        chkNightVrtulniky.setChecked(false);
        chkFiVrtulniky.setChecked(false);
        chkIriVrtulniky.setChecked(false);
        chkTstVrtulniky.setChecked(false);
        chkParVrtulniky.setChecked(false);
        chkMedClass1Vrtulniky.setChecked(false);
        chkMedClass2Vrtulniky.setChecked(false);
        chkVseobecnyPreukazVrtulniky.setChecked(false);
        chkObmedzenyPreukazVrtulniky.setChecked(false);
        chkEnglishLevel4Vrtulniky.setChecked(false);
        chkEnglishLevel5Vrtulniky.setChecked(false);
        chkEnglishLevel6Vrtulniky.setChecked(false);
        chkPoistenieZodpovednostCRSKVrtulniky.setChecked(false);
        chkZodpovednostiSvetVrtulniky.setChecked(false);
    }

    private int calculateDaysRemaining(String expiryDate, SimpleDateFormat sdf) {
        try {
            Date expiry = sdf.parse(expiryDate);
            Date now = new Date();
            long diffInMillis = expiry.getTime() - now.getTime();
            return (int) (diffInMillis / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            Log.e("calculateDaysRemaining", "Error parsing expiry date: " + expiryDate, e);
            return 0;
        }
    }

    private boolean isWiFiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

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
                Log.d("Retrofit", "Error: " + t.getMessage());
            }
        });
    }

    private String formatDateInput(String input) {
        input = input.replaceAll("[^0-9]", "");
        StringBuilder sb = new StringBuilder();
        if (input.length() >= 1) {
            sb.append(input.substring(0, Math.min(4, input.length())));
        }
        if (input.length() >= 5) {
            sb.append("-").append(input.substring(4, Math.min(6, input.length())));
        }
        if (input.length() >= 7) {
            sb.append("-").append(input.substring(6, Math.min(8, input.length())));
        }
        String month = sb.length() >= 7 ? sb.substring(5, 7) : "";
        if (!month.isEmpty() && Integer.parseInt(month) > 12) {
            sb.replace(5, 7, "12");
        }
        String day = sb.length() >= 10 ? sb.substring(8, 10) : "";
        if (!day.isEmpty()) {
            int intDay = Integer.parseInt(day);
            if (intDay > 31) {
                sb.replace(8, 10, "31");
            } else {
                int monthInt = Integer.parseInt(month);
                if (monthInt == 4 || monthInt == 6 || monthInt == 9 || monthInt == 11) {
                    if (intDay > 30) {
                        sb.replace(8, 10, "30");
                    }
                } else if (monthInt == 2) {
                    int maxDay = (isLeapYear(Integer.parseInt(sb.substring(0, 4)))) ? 29 : 28;
                    if (intDay > maxDay) {
                        sb.replace(8, 10, String.valueOf(maxDay));
                    }
                }
            }
        }
        return sb.toString();
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}
