package com.example.android.scanwire.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.android.scanwire.data.dao.BarcodeDao;
import com.example.android.scanwire.models.Barcode;


@androidx.room.Database(entities = {
        Barcode.class
}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {


    public static final String DB_NAME = "barcodes";

    /**
     * DAO for Barcodes.
     */
    public abstract BarcodeDao barcodeDao();
}
