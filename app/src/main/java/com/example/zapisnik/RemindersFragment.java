package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Typeface;
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

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemindersFragment extends Fragment {

    private CertificateDatabase database;

    // Views from the header
    private MaterialCalendarView materialCalendarView;
    private TextView tvStatus;

    // The main ListView
    private ListView listViewExpiring;

    // Adapter for the ListView
    private ArrayAdapter<String> adapter;
    private List<String> expiringCertificatesList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 1) Inflate the main layout (which has only a ListView)
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // 2) Find the ListView in fragment_reminders.xml
        listViewExpiring = view.findViewById(R.id.list_view_expiring);

        // 3) Inflate the header layout that has the TextView + MaterialCalendarView
        View headerView = inflater.inflate(R.layout.header_calendar, listViewExpiring, false);

        // 4) Get references to the header’s views
        materialCalendarView = headerView.findViewById(R.id.materialCalendarView);
        tvStatus = headerView.findViewById(R.id.tv_status);

        // 5) (Optional) Listen for date selections
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            int year = date.getYear();
            int month = date.getMonth() + 1; // 0-indexed
            int day = date.getDay();
            Log.d("RemindersFragment", "Selected date: " + year + "-" + month + "-" + day);
            // If desired, filter or update your reminders based on selected date
        });

        // 6) Add the header to the ListView before setting the adapter
        listViewExpiring.addHeaderView(headerView);

        // 7) Prepare your adapter (bold section headers)
        adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, expiringCertificatesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);
                TextView textView = itemView.findViewById(android.R.id.text1);
                String item = getItem(position);
                if ("Expiring Soon:".equals(item) || "Expired Certifications:".equals(item)) {
                    textView.setTypeface(null, Typeface.BOLD);
                } else {
                    textView.setTypeface(null, Typeface.NORMAL);
                }
                return itemView;
            }
        };
        listViewExpiring.setAdapter(adapter);

        // 8) Initialize your database
        database = CertificateDatabase.getInstance(requireActivity());

        // 9) Schedule periodic work for daily checks
        schedulePeriodicWork();

        // 10) Immediately check & display expiring certificates
        checkAndDisplayExpiringCertificates();

        return view;
    }

    /**
     * Periodic Worker for daily checks
     */
    private void schedulePeriodicWork() {
        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(
                CertificationReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest);
    }

    /**
     * Gathers soon-expiring and expired certificates, updates the ListView,
     * shows notifications, and highlights those dates on the calendar.
     */
    private void checkAndDisplayExpiringCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Certificate> soonExpiring = getExpiringCertificates();
            List<Certificate> expired = getExpiredCertificates();

            List<String> newList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();

            // Build the "Expiring Soon:" section
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
                        Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
                    }
                }
            }

            // Build the "Expired Certifications:" section
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
                        Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
                    }
                }
            }

            // Highlight these dates on the calendar
            highlightExpiryDatesOnCalendar(soonExpiring, expired);

            // Update the UI (ListView, status text)
            requireActivity().runOnUiThread(() -> {
                expiringCertificatesList.clear();
                expiringCertificatesList.addAll(newList);

                if (newList.isEmpty()) {
                    tvStatus.setText("No certifications expiring or expired.");
                } else {
                    tvStatus.setText("Pripomienky:");
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Highlights soon-expiring and expired certificate dates on the calendar.
     */
    private void highlightExpiryDatesOnCalendar(List<Certificate> soonExpiring, List<Certificate> expired) {
        List<Date> highlightDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Collect all soon-expiring dates
        for (Certificate cert : soonExpiring) {
            try {
                highlightDates.add(sdf.parse(cert.getExpiryDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Collect all expired dates
        for (Certificate cert : expired) {
            try {
                highlightDates.add(sdf.parse(cert.getExpiryDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ExpiryDayDecorator decorator = new ExpiryDayDecorator(requireContext(), highlightDates);

        // Must run on UI thread to add a decorator to the calendar
        requireActivity().runOnUiThread(() -> materialCalendarView.addDecorator(decorator));
    }

    /**
     * Returns a list of certificates expiring within the next 7 days.
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
                Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
            }
        }
        return soonExpiring;
    }

    /**
     * Returns a list of certificates that have already expired.
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
                Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
            }
        }
        return expired;
    }

    /**
     * Displays a notification for the given certificate.
     */
    private void showNotification(Certificate certificate, long days, boolean isExpired) {
        NotificationManager notificationManager =
                (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // your notification icon
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
     * Schedules a daily check of certificate expirations (after 24 hours).
     */
    private void scheduleDailyCheck() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::checkAndDisplayExpiringCertificates, 24 * 60 * 60 * 1000);
    }
}
