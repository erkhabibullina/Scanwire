package com.example.android.scanwire.data.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.android.scanwire.models.Barcode;

@Dao
public interface BarcodeDao {

    // Get Barcodes as PagedList LiveData (sorted by date)
    @Query("SELECT * FROM barcodes ORDER BY creation_epoch DESC")
    DataSource.Factory<Integer, Barcode> getBarcodesPagedList();

    // Get Barcodes as PagedList LiveData by Raw Query
    @RawQuery(observedEntities = Barcode.class)
    DataSource.Factory<Integer, Barcode> getBarcodesPagedList(SupportSQLiteQuery query);

    // Get Barcode matching ID as LiveData
    @Query("SELECT * FROM barcodes WHERE " + Barcode.idColumn + " IS :id")
    LiveData<Barcode> getBarcodeLiveData(long id);

    // Insert Barcode and return ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBarcode(Barcode barcode);

    // Insert multiple Barcodes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertBarcodes(Barcode[] barcodes);

    // Update Barcode
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateBarcode(Barcode barcode);

    // Delete Barcode
    @Delete
    void deleteBarcode(Barcode barcode);

    // Delete ALL Barcodes
    @Query("DELETE FROM barcodes")
    void deleteAllBarcodes();
}
