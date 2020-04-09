package com.example.android.scanwire.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.repositories.BarcodeRepository;

import javax.inject.Inject;

public class BarcodeViewModel extends AndroidViewModel {

    @Inject
    BarcodeRepository barcodeRepository;

    @Inject
    public BarcodeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Barcode> getBarcode(long id) {
        return barcodeRepository.getBarcodeLiveData(id);
    }

    public void deleteBarcode(Barcode barcode) {
        barcodeRepository.deleteBarcode(barcode);
    }
}
