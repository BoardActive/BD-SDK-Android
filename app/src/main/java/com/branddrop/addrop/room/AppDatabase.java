package com.branddrop.addrop.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.branddrop.addrop.room.table.MessageEntity;


@Database(entities = {MessageEntity.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MessageDAO getMessageDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "BrandDrop")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}