package com.example.zapisnik;

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
import android.widget.Button;
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
    private TextView tvReminderStatus;
    private Button btnCheckCertificates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        tvReminderStatus = view.findViewById(R.id.tv_reminder_status);
        btnCheckCertificates = view.findViewById(R.id.btn_check_certificates);

        // Initialize database
        database = CertificateDatabase.getInstance(getActivity());

        // Set up the button click listener
        btnCheckCertificates.setOnClickListener(v -> checkForExpiringCertificates());

        // Schedule the periodic work for certification reminders
        schedulePeriodicWork();

        return view;
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(CertificationReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(getContext()).enqueue(reminderWorkRequest);
    }

    // Method to check for expiring certificates
    private void checkForExpiringCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> expiringCertifications = getExpiringCertifications();

            // Notify user if any certifications are expiring
            for (Certificate cert : expiringCertifications) {
                showNotification(cert);
            }

            // Update UI status
            getActivity().runOnUiThread(() -> {
                if (expiringCertifications.isEmpty()) {
                    tvReminderStatus.setText("No certifications expiring soon.");
                } else {
                    tvReminderStatus.setText("You have " + expiringCertifications.size() + " certification(s) expiring soon.");
                }
            });
        });
    }

    // Method to get the list of certifications that are expiring within the next 7 days
    private List<Certificate> getExpiringCertifications() {
        List<Certificate> certifications = database.certificateDao().getAllCertificates();
        List<Certificate> expiringCertifications = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        for (Certificate cert : certifications) {
            try {
                Date expiryDate = sdf.parse(cert.getExpiryDate());
                long diffInMillis = expiryDate.getTime() - today.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);  // Convert milliseconds to days
                if (diffInDays <= 7 && diffInDays >= 0) { // Notify 7 days before expiration
                    expiringCertifications.add(cert);
                }
            } catch (ParseException e) {
                Log.e("RemindersFragment", "Error parsing expiry date", e);
            }
        }
        return expiringCertifications;
    }

    // Method to show a notification for a certificate that is expiring soon
    private void showNotification(Certificate certificate) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "certification_reminder_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Certification Reminders";
            String description = "Reminders for certification expirations";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Use your app's icon here
                .setContentTitle("Certification Expiring Soon")
                .setContentText(certificate.getName() + " is expiring on " + certificate.getExpiryDate())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(certificate.getId(), builder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Schedule a daily check when the fragment is resumed
        scheduleDailyCheck();
    }

    // Schedule a daily check for expiry dates
    private void scheduleDailyCheck() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> checkForExpiringCertificates(), 24 * 60 * 60 * 1000); // 24 hours
    }
}
