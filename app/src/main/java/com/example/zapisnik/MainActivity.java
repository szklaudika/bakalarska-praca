package com.example.zapisnik;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_POST_NOTIFICATIONS = 101;

    private NetworkChangeReceiver networkChangeReceiver;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Theme is set in MyApplication or before super.onCreate() if needed.
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLightMode = prefs.getBoolean("dark_mode", false);
        if (isLightMode) {
            setTheme(R.style.Theme_Zapisnik_Lightmode);
        } else {
            setTheme(R.style.Theme_Zapisnik);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            // When starting, show the login fragment.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new LoginFragment())
                    .commit();
        }

        // Listen for fragment changes so we can update the bottom nav's visibility.
        getSupportFragmentManager().addOnBackStackChangedListener(() -> updateBottomNavVisibility());
        updateBottomNavVisibility();

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        scheduleImmediateReminderWork();
        scheduleReminderWork();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            }
        }
    }

    private void updateBottomNavVisibility() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment == null || currentFragment instanceof LoginFragment || currentFragment instanceof RegistrationFragment) {
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Prevent clicks if the current fragment is login or registration.
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (currentFragment instanceof LoginFragment || currentFragment instanceof RegistrationFragment) {
                        // Do nothing
                        return false;
                    }
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
                        updateBottomNavVisibility();
                        return true;
                    }
                    return false;
                }
            };

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

    public void navigateToFragment(Fragment fragment, int navItemId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commitAllowingStateLoss();

        // Update the bottom nav visibility after the fragment transaction is committed
        getSupportFragmentManager().executePendingTransactions();
        updateBottomNavVisibility();

        // Postpone selection update slightly to ensure correct UI synchronization
        bottomNavigationView.post(() -> bottomNavigationView.setSelectedItemId(navItemId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted – notifications should work.
            } else {
                Toast.makeText(this, "Notification permission is required for alerts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
