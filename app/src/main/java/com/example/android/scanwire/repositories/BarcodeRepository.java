package com.example.android.scanwire.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.android.scanwire.data.dao.BarcodeDao;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.AppExecutor;
import com.example.android.scanwire.utils.BarcodeUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BarcodeRepository {

    public static final String TAG = BarcodeRepository.class.getSimpleName();

    @Inject
    BarcodeDao barcodeDao;

    @Inject
    AppExecutor appExecutor;

    @Inject
    Context applicationContext;

    @Inject
    public BarcodeRepository(BarcodeDao barcodeDao, AppExecutor appExecutor, Context context) {
        this.barcodeDao = barcodeDao;
        this.appExecutor = appExecutor;
        this.applicationContext = context;
    }

    /**
     * Builds SQL String query based on variables.
     *
     * @param query Search String
     * @return Complete SQL query String ready to be used with RawQuery in DAO
     */
    private String queryBuilder(String query) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM barcodes WHERE ");

        queryBuilder.append(Barcode.rawValueColumn)
                .append(" LIKE '%' || '")
                .append(query)
                .append("' || '%'");

        // Fix for searching String type representation
        List<Integer> typeMatches = BarcodeUtil.getQueryTypes(applicationContext, query);
        for(int i: typeMatches) {
            queryBuilder.append(" OR ")
                    .append(Barcode.typeColumn)
                    .append(" IS ")
                    .append(i);
        }

        queryBuilder.append(" ORDER BY creation_epoch DESC");

        Log.d(TAG, "SQL Query: " + queryBuilder.toString());

        return queryBuilder.toString();
    }

    /**
     * Get Barcodes as LiveData by query.
     *
     * @param query Search String, use null to not search
     */
    public DataSource.Factory<Integer, Barcode> getBarcodesPagedList(@Nullable String query) {
        Log.d(TAG, "Retrieving all Barcodes matching query..");
        if(query != null && !query.isEmpty()) {
            String queryString = queryBuilder(query);
            SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString);
            return barcodeDao.getBarcodesPagedList(sqlQuery);
        }
        return barcodeDao.getBarcodesPagedList();
    }

    /**
     * Get specific Barcode as LiveData.
     *
     * @param id ID of Barcode to retrieve
     */
    public LiveData<Barcode> getBarcodeLiveData(long id) {
        Log.d(TAG, "Retrieving Barcode (by ID) as LiveData..");
        return barcodeDao.getBarcodeLiveData(id);
    }

    /**
     * Insert Barcode into DB.
     *
     * @return ID of inserted Barcode
     */
    public long insertBarcode(Barcode barcode) {
        long id = appExecutor.dbFuture(() -> barcodeDao.insertBarcode(barcode));
        Log.d(TAG, "Inserted Barcode[id:" + id + "] into db..");
        return id;
    }

    /**
     * Insert multiple Barcodes into DB.
     *
     * @return IDs of inserted Barcodes
     */
    public long[] insertBarcodes(Barcode[] barcodes) {
        long[] ids = appExecutor.dbFuture(() -> barcodeDao.insertBarcodes(barcodes));
        for (long id : ids) {
            Log.d(TAG, "Inserted Barcode[id:" + id + "] into db..");
        }
        return ids;
    }

    /**
     * Update Barcode in DB.
     */
    public void updateBarcode(Barcode barcode) {
        Log.d(TAG, "Updating Barcode[id:" + barcode.getId() + "] in db..");
        appExecutor.dbThread().execute(() -> barcodeDao.updateBarcode(barcode));
    }

    /**
     * Delete Barcode from DB.
     */
    public void deleteBarcode(Barcode barcode) {
        Log.d(TAG, "Deleting Barcode[id:" + barcode.getId() + "] in db..");
        appExecutor.dbThread().execute(() -> barcodeDao.deleteBarcode(barcode));
    }

    /**
     * Delete ALL Barcodes from DB.
     */
    public void deleteAllBarcodes() {
        Log.d(TAG, "Deleting ALL Barcodes in db..");
        appExecutor.dbThread().execute(() -> barcodeDao.deleteAllBarcodes());
    }
}
