package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.Phone;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class PhoneTypeConverter {

    @TypeConverter
    public static Phone jsonToPhone(String json) {
        return new Gson().fromJson(json, new TypeToken<Phone>() {
        }.getType());
    }

    @TypeConverter
    public static String phoneToJson(Phone phone) {
        return new Gson().toJson(phone);
    }
}
