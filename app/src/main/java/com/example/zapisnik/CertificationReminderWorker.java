package com.example.zapisnik;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CertificationReminderWorker extends Worker {

    public CertificationReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the logged-in user's id from SharedPreferences.
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int loggedInUserId = prefs.getInt("userId", 0);

        CertificateDatabase database = CertificateDatabase.getInstance(getApplicationContext());
        List<Certificate> certifications = database.certificateDao().getAllCertificates();
        List<Certificate> expiringCertifications = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date today = new Date();

        // Filter only certificates that belong to the logged in user and expire within 7 days.
        for (Certificate cert : certifications) {
            if (cert.getUserId() != loggedInUserId) {
                continue;  // Skip certificates that do not belong to the logged-in user.
            }
            try {
                Date expiryDate = sdf.parse(cert.getExpiryDate());
                long diffInMillis = expiryDate.getTime() - today.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                if (diffInDays <= 7 && diffInDays >= 0) {
                    expiringCertifications.add(cert);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Show notifications for each expiring certificate.
        for (Certificate cert : expiringCertifications) {
            showNotification(cert);
        }
        return Result.success();
    }

    private void showNotification(Certificate certificate) {
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "certification_reminder_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Certification Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Reminders for certification expirations");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Use your notification icon.
                .setContentTitle("Certification Expiring Soon")
                .setContentText(certificate.getCertificateType() + " expires on " + certificate.getExpiryDate())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Use the certificate's id as the notification id for uniqueness.
        notificationManager.notify(certificate.getId(), builder.build());
    }
}
