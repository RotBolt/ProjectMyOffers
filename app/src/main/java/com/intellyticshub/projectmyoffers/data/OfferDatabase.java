package com.intellyticshub.projectmyoffers.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

@Database(entities = {OfferModel.class}, version = 1, exportSchema = false)
public abstract class OfferDatabase extends RoomDatabase {

    abstract OfferDao offerDao();

    private static volatile OfferDatabase instance;

    public static OfferDatabase getDatabase(Context context) {
        if (instance != null) return instance;

        synchronized (OfferDatabase.class) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(), OfferDatabase.class, "offers.db"
            ).build();
            return instance;
        }
    }
}
