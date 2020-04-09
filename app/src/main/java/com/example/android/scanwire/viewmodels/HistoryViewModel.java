package com.example.android.scanwire.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.repositories.BarcodeRepository;

import javax.inject.Inject;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<PagedList<Barcode>> mBarcodes;

    @Inject
    BarcodeRepository barcodeRepository;

    @Inject
    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Set Barcodes to Observe.
     *
     * @param query Search String, use null to not search
     */
    public void setBarcodes(@Nullable String query) {
        mBarcodes = new LivePagedListBuilder<>(barcodeRepository.getBarcodesPagedList(query),
                Constants.BARCODE_LIST_PAGE_SIZE).build();
    }

    /**
     * Observable PagedList LiveData of Barcodes.
     */
    public LiveData<PagedList<Barcode>> getBarcodes() {
        return mBarcodes;
    }

    /**
     * Insert Barcode.
     */
    public long insertBarcode(Barcode barcode) {
        return barcodeRepository.insertBarcode(barcode);
    }

    /**
     * Delete Barcode.
     */
    public void deleteBarcode(Barcode barcode) {
        barcodeRepository.deleteBarcode(barcode);
    }

    /**
     * Delete ALL Barcodes.
     */
    public void deleteAllBarcodes() {
        barcodeRepository.deleteAllBarcodes();
    }
}
