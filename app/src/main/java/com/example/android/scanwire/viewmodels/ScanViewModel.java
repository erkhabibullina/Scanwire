package com.example.android.scanwire.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.repositories.BarcodeRepository;

import javax.inject.Inject;

public class ScanViewModel extends AndroidViewModel {

    @Inject
    BarcodeRepository barcodeRepository;

    @Inject
    public ScanViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Insert Barcode.
     *
     * @return ID of inserted Barcode
     */
    public long insertBarcode(Barcode barcode) {
        return barcodeRepository.insertBarcode(barcode);
    }

    /**
     * Insert multiple Barcodes.
     *
     * @return IDs of inserted Barcodes
     */
    public long[] insertBarcodes(Barcode[] barcodes) {
        return barcodeRepository.insertBarcodes(barcodes);
    }
}
