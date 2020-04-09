package com.example.android.scanwire.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.repositories.BarcodeRepository;

import javax.inject.Inject;

public class GenerateViewModel extends AndroidViewModel {

    @Inject
    BarcodeRepository barcodeRepository;

    @Inject
    public GenerateViewModel(@NonNull Application application) {
        super(application);
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
}
