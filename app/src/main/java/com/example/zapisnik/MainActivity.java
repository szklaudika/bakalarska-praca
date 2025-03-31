package com.example.zapisnik;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLightMode = prefs.getBoolean("dark_mode", false);
        if (isLightMode) {
            setTheme(R.style.Theme_Zapisnik_Lightmode);
        } else {
            setTheme(R.style.Theme_Zapisnik);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new LoginFragment())
                    .commit();
        }

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        scheduleImmediateReminderWork();
        scheduleReminderWork();
    }

    private void scheduleImmediateReminderWork() {
        OneTimeWorkRequest immediateWorkRequest = new OneTimeWorkRequest.Builder(CertificationReminderWorker.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(immediateWorkRequest);
    }

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
        unregisterReceiver(networkChangeReceiver);
    }

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
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, selectedFragment)
                                .commitAllowingStateLoss();
                    }
                    return true;
                }
            };
}
