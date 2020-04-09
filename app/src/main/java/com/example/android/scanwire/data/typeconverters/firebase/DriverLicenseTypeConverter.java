package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.DriverLicense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class DriverLicenseTypeConverter {

    @TypeConverter
    public static DriverLicense jsonToDriverLicense(String json) {
        return new Gson().fromJson(json, new TypeToken<DriverLicense>() {
        }.getType());
    }

    @TypeConverter
    public static String driverLicenseToJson(DriverLicense driverLicense) {
        return new Gson().toJson(driverLicense);
    }
}
