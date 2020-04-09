package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.WiFi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class WifiTypeConverter {

    @TypeConverter
    public static WiFi jsonToWifi(String json) {
        return new Gson().fromJson(json, new TypeToken<WiFi>() {
        }.getType());
    }

    @TypeConverter
    public static String wifiToJson(WiFi wifi) {
        return new Gson().toJson(wifi);
    }
}
