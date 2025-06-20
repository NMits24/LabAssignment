package com.example.labassignment_naim.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BillEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BillDao billDao();
}