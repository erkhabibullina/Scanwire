package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.Email;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class EmailTypeConverter {

    @TypeConverter
    public static Email jsonToEmail(String json) {
        return new Gson().fromJson(json, new TypeToken<Email>() {
        }.getType());
    }

    @TypeConverter
    public static String emailToJson(Email email) {
        return new Gson().toJson(email);
    }
}
