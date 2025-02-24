package com.example.zapisnik;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Certificate.class}, version = 1)
public abstract class CertificateDatabase extends RoomDatabase {
    private static CertificateDatabase instance;

    public abstract CertificateDao certificateDao();

    public static synchronized CertificateDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CertificateDatabase.class, "certificate_database")
                    .allowMainThreadQueries()  // ❗ Toto je len pre jednoduché aplikácie, ideálne použiť background thread
                    .build();
        }
        return instance;
    }
}
