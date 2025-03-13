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

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, expiringCertificatesList);
        listViewExpiring.setAdapter(adapter);

        // Naplánuj periodickú prácu pre pripomienky certifikátov
        schedulePeriodicWork();

        // Ihneď skontrolujeme a zobrazíme certifikáty, ktoré sa blížia k expirácii
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
     * Metóda, ktorá v samostatnom vlákne získa zo všetkých certifikátov tie, ktoré expirujú do 7 dní,
     * odošle notifikácie a aktualizuje zoznam v UI.
     */
    private void checkAndDisplayExpiringCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> expiringCerts = getExpiringCertificates();
            expiringCertificatesList.clear();
            for (Certificate cert : expiringCerts) {
                String certificateInfo = cert.getCertificateType() + " - Expires: " + cert.getExpiryDate();
                expiringCertificatesList.add(certificateInfo);
                showNotification(cert);
            }
            getActivity().runOnUiThread(() -> {
                if (expiringCerts.isEmpty()) {
                    tvStatus.setText("No certifications expiring soon.");
                } else {
                    tvStatus.setText("Expiring Certifications (" + expiringCerts.size() + "):");
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Prejde všetky certifikáty v databáze a vráti tie, ktoré expirujú do 7 dní.
     */
    private List<Certificate> getExpiringCertificates() {
        List<Certificate> allCertificates = database.certificateDao().getAllCertificates();
        List<Certificate> expiringCerts = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        for (Certificate cert : allCertificates) {
            try {
                Date expiryDate = sdf.parse(cert.getExpiryDate());
                long diffInMillis = expiryDate.getTime() - today.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                if (diffInDays <= 7 && diffInDays >= 0) {
                    expiringCerts.add(cert);
                }
            } catch (ParseException e) {
                Log.e("RemindersFragment", "Error parsing expiry date for certificate: " + cert.getCertificateType(), e);
            }
        }
        return expiringCerts;
    }

    /**
     * Zobrazí notifikáciu pre daný certifikát.
     */
    private void showNotification(Certificate certificate) {
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Uistite sa, že máte túto ikonu v res/drawable
                .setContentTitle("Certification Expiring Soon")
                .setContentText(certificate.getCertificateType() + " expires on " + certificate.getExpiryDate())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Používame jedinečné ID certifikátu pre notifikáciu
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
