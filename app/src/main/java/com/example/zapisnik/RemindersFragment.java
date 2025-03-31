package com.example.zapisnik;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

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

        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        listViewExpiring = view.findViewById(R.id.list_view_expiring);

        // Inflate header_calendar.xml which contains the calendar and status TextView.
        View headerView = inflater.inflate(R.layout.header_calendar, listViewExpiring, false);
        materialCalendarView = headerView.findViewById(R.id.materialCalendarView);
        tvStatus = headerView.findViewById(R.id.tv_status);

        // Set a listener for date selection.
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            int year = date.getYear();
            int month = date.getMonth() + 1; // months are 0-indexed
            int day = date.getDay();
            Log.d("RemindersFragment", "Selected date: " + year + "-" + month + "-" + day);
            // Show the popup for the selected day.
            showPopupForSelectedDay(date);
        });

        listViewExpiring.addHeaderView(headerView);

        // Use a custom adapter that inflates different layouts for header lines and certificate items.
        adapter = new ArrayAdapter<String>(requireContext(), 0, expiringCertificatesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String item = getItem(position);
                if ("Expiring Soon:".equals(item) || "Expired Certifications:".equals(item)) {
                    // Inflate a simple header layout (without a card)
                    convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.reminder_item_header, parent, false);
                    TextView headerTv = convertView.findViewById(R.id.tv_header);
                    headerTv.setText(item);
                    // Check current mode to adjust text color: black in light mode, white in dark mode.
                    SharedPreferences prefs = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    boolean isLightMode = prefs.getBoolean("dark_mode", true);
                    if (isLightMode) {
                        headerTv.setTextColor(getContext().getResources().getColor(android.R.color.black));
                    } else {
                        headerTv.setTextColor(getContext().getResources().getColor(android.R.color.white));
                    }
                } else {
                    // Inflate the certificate item layout with a CardView.
                    convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.list_item_reminder, parent, false);
                    TextView itemTv = convertView.findViewById(R.id.tv_item);
                    itemTv.setText(item);
                }
                return convertView;
            }
        };

        listViewExpiring.setAdapter(adapter);

        database = CertificateDatabase.getInstance(requireActivity());

        schedulePeriodicWork();
        checkAndDisplayExpiringCertificates();

        return view;
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(
                CertificationReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest);
    }

    private void checkAndDisplayExpiringCertificates() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Retrieve current user id
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);

            List<Certificate> soonExpiring = getExpiringCertificates(userId);
            List<Certificate> expired = getExpiredCertificates(userId);

            List<String> newList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date today = new Date();

            if (!soonExpiring.isEmpty()) {
                newList.add("Expiring Soon:");
                for (Certificate cert : soonExpiring) {
                    try {
                        Date expiryDate = sdf.parse(cert.getExpiryDate());
                        long diffInMillis = expiryDate.getTime() - today.getTime();
                        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                        String certificateInfo = cert.getCertificateType()
                                + "\nExpires: " + cert.getExpiryDate()
                                + "\n(" + diffInDays + " days remaining)";
                        newList.add(certificateInfo);
                        showNotification(cert, diffInDays, false);
                    } catch (ParseException e) {
                        Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
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
                        String certificateInfo = cert.getCertificateType()
                                + "\nExpired on: " + cert.getExpiryDate()
                                + "\n(" + diffInDays + " days ago)";
                        newList.add(certificateInfo);
                        showNotification(cert, diffInDays, true);
                    } catch (ParseException e) {
                        Log.e("RemindersFragment", "Error parsing expiry date: " + cert.getCertificateType(), e);
                    }
                }
            }

            highlightExpiryDatesOnCalendar(soonExpiring, expired);

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
     * When a day is selected on the calendar, this method filters certificates that expire on that day
     * and shows a small popup window above the calendar.
     */
    private void showPopupForSelectedDay(final CalendarDay selectedDay) {
        Executors.newSingleThreadExecutor().execute(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String selectedStr = sdf.format(selectedDay.getDate());
            // Retrieve current user id
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", 0);

            // Query certificates only for this user.
            List<Certificate> allCertificates = database.certificateDao().getCertificatesByUserId(userId);
            List<Certificate> matchingCertificates = new ArrayList<>();
            for (Certificate cert : allCertificates) {
                if (cert.getExpiryDate().equals(selectedStr)) {
                    matchingCertificates.add(cert);
                }
            }

            if (matchingCertificates.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    // Optionally show a Toast.
                });
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Certificate cert : matchingCertificates) {
                sb.append(cert.getCertificateType())
                        .append("\nExpires: ")
                        .append(cert.getExpiryDate())
                        .append("\n\n");
            }

            requireActivity().runOnUiThread(() -> {
                LayoutInflater inflater = (LayoutInflater)
                        requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_certificates, null);
                TextView tvPopup = popupView.findViewById(R.id.tv_popup);
                tvPopup.setText(sb.toString());
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                View rootView = requireActivity().getWindow().getDecorView().getRootView();
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                new Handler(Looper.getMainLooper()).postDelayed(popupWindow::dismiss, 3000);
            });
        });
    }

    private void highlightExpiryDatesOnCalendar(List<Certificate> soonExpiring, List<Certificate> expired) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isLightMode = prefs.getBoolean("dark_mode", true);

        List<Date> soonDates = new ArrayList<>();
        List<Date> expiredDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (Certificate cert : soonExpiring) {
            try {
                soonDates.add(sdf.parse(cert.getExpiryDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (Certificate cert : expired) {
            try {
                expiredDates.add(sdf.parse(cert.getExpiryDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int expiredDrawable = isLightMode
                ? R.drawable.custom_expired_highlight_light
                : R.drawable.custom_expired_highlight;
        int soonDrawable = isLightMode
                ? R.drawable.custom_highlight_light
                : R.drawable.custom_highlight;

        ExpiryDayDecorator soonDecorator = new ExpiryDayDecorator(
                requireContext(),
                soonDates,
                soonDrawable
        );

        ExpiryDayDecorator expiredDecorator = new ExpiryDayDecorator(
                requireContext(),
                expiredDates,
                expiredDrawable
        );

        requireActivity().runOnUiThread(() -> {
            materialCalendarView.removeDecorators();
            materialCalendarView.addDecorator(soonDecorator);
            materialCalendarView.addDecorator(expiredDecorator);
        });
    }

    private List<Certificate> getExpiringCertificates(int userId) {
        List<Certificate> allCertificates = database.certificateDao().getCertificatesByUserId(userId);
        List<Certificate> soonExpiring = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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

    private List<Certificate> getExpiredCertificates(int userId) {
        List<Certificate> allCertificates = database.certificateDao().getCertificatesByUserId(userId);
        List<Certificate> expired = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
                .setSmallIcon(R.drawable.ic_notification)
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

    private void scheduleDailyCheck() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::checkAndDisplayExpiringCertificates, 24 * 60 * 60 * 1000);
    }
}
