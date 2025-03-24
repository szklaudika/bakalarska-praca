package com.example.zapisnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check if the user is logged in by reading the userId from SharedPreferences.
            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);
            if (userId != 0) {
                // Schedule an immediate one-time work request for certification reminders.
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CertificationReminderWorker.class)
                        .setInitialDelay(0, TimeUnit.SECONDS)
                        .build();
                WorkManager.getInstance(context).enqueue(workRequest);
            }
        }
    }
}
