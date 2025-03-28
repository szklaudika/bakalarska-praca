package com.example.zapisnik;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme based on preference before calling super.onCreate()
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("dark_mode", true)) {
            // When true, use light mode theme (Theme.Zapisnik.Lightmode)
            setTheme(R.style.Theme_Zapisnik_Lightmode);
        } else {
            // Otherwise, use the default theme
            setTheme(R.style.Theme_Zapisnik);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Set default fragment.
        // Instead of showing the FlightListFragment, we show the LoginFragment.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new LoginFragment())
                    .commit();
        }

        // Register NetworkChangeReceiver for monitoring Wi-Fi connectivity
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        // Schedule immediate work to show reminders right after startup
        scheduleImmediateReminderWork();

        // Schedule periodic work for certificate reminders
        scheduleReminderWork();
    }

    // One-time work for immediate notification check
    private void scheduleImmediateReminderWork() {
        OneTimeWorkRequest immediateWorkRequest = new OneTimeWorkRequest.Builder(CertificationReminderWorker.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(immediateWorkRequest);
    }

    // Periodic work for certificate reminders (every day)
    private void scheduleReminderWork() {
        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(
                CertificationReminderWorker.class, 1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "certReminder",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderWorkRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the NetworkChangeReceiver when the activity is destroyed.
        unregisterReceiver(networkChangeReceiver);
    }

    // Bottom navigation listener for switching between fragments
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_flight_list:
                            selectedFragment = new FlightListFragment();
                            break;
                        case R.id.nav_add_flight:
                            selectedFragment = new AddFlightFragment();
                            break;
                        case R.id.nav_certificates:
                            selectedFragment = new CertificatesFragment();
                            break;
                        case R.id.nav_reminders:
                            selectedFragment = new RemindersFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        // Optionally add a new case for login or registration if needed
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, selectedFragment)
                            .commitAllowingStateLoss();
                    return true;
                }
            };
}
