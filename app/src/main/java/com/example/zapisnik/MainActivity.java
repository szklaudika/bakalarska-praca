package com.example.zapisnik;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nastavenie Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Predvolený fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new FlightListFragment())
                    .commit();
        }

        // Zaregistruj NetworkChangeReceiver pre monitorovanie Wi-Fi pripojenia
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Zrušiť registráciu NetworkChangeReceiver pri zničení aktivity
        unregisterReceiver(networkChangeReceiver);
    }

    // Navigácia medzi fragmentami
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

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, selectedFragment)
                            .commit();

                    return true;
                }
            };
}
