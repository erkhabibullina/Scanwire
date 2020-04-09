package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class GeoPointTypeConverter {

    @TypeConverter
    public static GeoPoint jsonToGeoPoint(String json) {
        return new Gson().fromJson(json, new TypeToken<GeoPoint>() {
        }.getType());
    }

    @TypeConverter
    public static String geoPointToJson(GeoPoint geoPoint) {
        return new Gson().toJson(geoPoint);
    }
}
