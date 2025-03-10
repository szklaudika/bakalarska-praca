package com.example.zapisnik;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CertificationReminderWorker extends Worker {

    public CertificationReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Call the method to check for expiring certificates
        checkForExpiringCertificates();
        return Result.success();
    }

    private void checkForExpiringCertificates() {
        CertificateDatabase database = CertificateDatabase.getInstance(getApplicationContext());
        List<Certificate> expiringCertifications = getExpiringCertifications(database);

        // You can now show notifications for expiring certificates
        for (Certificate cert : expiringCertifications) {
            showNotification(cert);
        }
    }

    private List<Certificate> getExpiringCertifications(CertificateDatabase database) {
        // Fetch all certificates from the database
        List<Certificate> certifications = database.certificateDao().getAllCertificates();
        List<Certificate> expiringCertifications = new ArrayList<>();

        // Use a date format to parse the expiry date string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Assuming the date format is "yyyy-MM-dd"
        Date today = new Date();  // Get the current date

        // Loop through each certificate to check the expiration date
        for (Certificate cert : certifications) {
            try {
                // Parse the expiry date of the certificate
                Date expiryDate = sdf.parse(cert.getExpiryDate());

                // Calculate the difference in milliseconds between the expiry date and today
                long diffInMillis = expiryDate.getTime() - today.getTime();

                // Convert the difference in milliseconds to days
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);  // Convert to days

                // If the certificate is expiring within the next 7 days (inclusive), add it to the list
                if (diffInDays <= 7 && diffInDays >= 0) {
                    expiringCertifications.add(cert);
                }

            } catch (ParseException e) {
                // Log any issues with parsing the expiry date
                Log.e("getExpiringCertifications", "Error parsing expiry date for certificate: " + cert.getName(), e);
            }
        }

        // Return the list of certificates that are expiring within the next 7 days
        return expiringCertifications;
    }


    private void showNotification(Certificate certificate) {
        // Your existing notification logic goes here
    }
}
