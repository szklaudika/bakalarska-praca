package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemindersFragment extends Fragment {

    private CertificateDatabase database;
    private TextView tvStatus;
    private ListView listViewExpiring;
    private ArrayAdapter<String> adapter;
    private List<String> expiringCertificatesList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Predpokladáme, že layout obsahuje TextView s id "tv_status" a ListView s id "list_view_expiring"
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);
        tvStatus = view.findViewById(R.id.tv_status);
        listViewExpiring = view.findViewById(R.id.list_view_expiring);

        database = CertificateDatabase.getInstance(getActivity());

        // Použitie vlastného ArrayAdapter-u, ktorý zmení štýl položiek pre hlavičky
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, expiringCertificatesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                String item = getItem(position);
                if ("Expiring Soon:".equals(item) || "Expired Certifications:".equals(item)) {
                    textView.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    textView.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                return view;
            }
        };
        listViewExpiring.setAdapter(adapter);

        // Naplánovanie periodickej práce pre pripomienky certifikátov
        schedulePeriodicWork();

        // Ihneď skontrolujeme a zobrazíme certifikáty, ktoré expirujú do 7 dní a tie, ktoré už vypršali.
        checkAndDisplayExpiringCertificates();

        return view;
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(
                CertificationReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(getContext()).enqueue(reminderWorkRequest);
    }

    /**
     * Táto metóda v samostatnom vlákne:
     * - Získa certifikáty, ktoré expirujú do 7 dní (s počtom zostávajúcich dní),
     * - Získa aj certifikáty, ktoré už vypršali (s počtom dní, ktoré uplynuli od expirácie),
     * - Pripraví zoznam s oddelenými sekciami ("Expiring Soon:" a "Expired Certifications:"),
     * - Zobrazí notifikácie pre každý certifikát.
     */
    private void checkAndDisplayExpiringCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> soonExpiring = getExpiringCertificates();
            List<Certificate> expired = getExpiredCertificates();

            // Vytvoríme dočasný zoznam, do ktorého budeme ukladať reťazce
            List<String> newList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();

            if (!soonExpiring.isEmpty()) {
                newList.add("Expiring Soon:");
                for (Certificate cert : soonExpiring) {
                    try {
                        Date expiryDate = sdf.parse(cert.getExpiryDate());
                        long diffInMillis = expiryDate.getTime() - today.getTime();
                        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                        String certificateInfo = cert.getCertificateType() + " - Expires: " + cert.getExpiryDate()
                                + " (" + diffInDays + " days remaining)";
                        newList.add(certificateInfo);
                        showNotification(cert, diffInDays, false);
                    } catch (ParseException e) {
                        Log.e("RemindersFragment", "Error parsing expiry date for certificate: "
                                + cert.getCertificateType(), e);
                    }
                }
            }
            if (!expired.isEmpty()) {
                newList.add("Expired Certifications:");
                for (Certificate cert : expired) {
                    try {
                        Date expiryDate = sdf.parse(cert.getExpiryDate());
                        long diffInMillis = today.getTime() - expiryDate.getTime();
                        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                        String certificateInfo = cert.getCertificateType() + " - Expired on: " + cert.getExpiryDate()
                                + " (" + diffInDays + " days ago)";
                        newList.add(certificateInfo);
                        showNotification(cert, diffInDays, true);
                    } catch (ParseException e) {
                        Log.e("RemindersFragment", "Error parsing expiry date for certificate: "
                                + cert.getCertificateType(), e);
                    }
                }
            }
            // Aktualizácia UI – zmeňte pôvodný zoznam na UI vlákne
            getActivity().runOnUiThread(() -> {
                expiringCertificatesList.clear();
                expiringCertificatesList.addAll(newList);
                if (newList.isEmpty()) {
                    tvStatus.setText("No certifications expiring or expired.");
                } else {
                    tvStatus.setText("Certification Reminders:");
                }
                adapter.notifyDataSetChanged();
            });
        });
    }


    /**
     * Prejde všetky certifikáty v databáze a vráti tie, ktoré expirujú do 7 dní (teda zostávajúce dni >= 0 a <= 7).
     */
    private List<Certificate> getExpiringCertificates() {
        List<Certificate> allCertificates = database.certificateDao().getAllCertificates();
        List<Certificate> soonExpiring = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        for (Certificate cert : allCertificates) {
            try {
                Date expiryDate = sdf.parse(cert.getExpiryDate());
                long diffInMillis = expiryDate.getTime() - today.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                if (diffInDays <= 7 && diffInDays >= 0) {
                    soonExpiring.add(cert);
                }
            } catch (ParseException e) {
                Log.e("RemindersFragment", "Error parsing expiry date for certificate: " + cert.getCertificateType(), e);
            }
        }
        return soonExpiring;
    }

    /**
     * Prejde všetky certifikáty v databáze a vráti tie, ktoré už sú expirované (teda zostávajúce dni < 0).
     */
    private List<Certificate> getExpiredCertificates() {
        List<Certificate> allCertificates = database.certificateDao().getAllCertificates();
        List<Certificate> expired = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        for (Certificate cert : allCertificates) {
            try {
                Date expiryDate = sdf.parse(cert.getExpiryDate());
                long diffInMillis = today.getTime() - expiryDate.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                if (diffInDays > 0) {
                    expired.add(cert);
                }
            } catch (ParseException e) {
                Log.e("RemindersFragment", "Error parsing expiry date for certificate: " + cert.getCertificateType(), e);
            }
        }
        return expired;
    }

    /**
     * Zobrazenie notifikácie pre daný certifikát.
     * Ak je isExpired true, notifikácia indikuje, že certifikát už vypršal, inak že expiruje čoskoro.
     */
    private void showNotification(Certificate certificate, long days, boolean isExpired) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "certification_reminder_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Certification Reminders";
            String description = "Reminders for certification expirations";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        String contentText;
        if (isExpired) {
            contentText = certificate.getCertificateType() + " expired on " + certificate.getExpiryDate()
                    + " (" + days + " days ago)";
        } else {
            contentText = certificate.getCertificateType() + " expires on " + certificate.getExpiryDate()
                    + " (" + days + " days remaining)";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Uistite sa, že máte túto ikonu v res/drawable
                .setContentTitle("Certification Reminder")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(certificate.getId(), builder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndDisplayExpiringCertificates();
        scheduleDailyCheck();
    }

    /**
     * Naplánuje dennú kontrolu expirácie certifikátov (oneskorená o 24 hodín).
     */
    private void scheduleDailyCheck() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::checkAndDisplayExpiringCertificates, 24 * 60 * 60 * 1000);
    }
}
