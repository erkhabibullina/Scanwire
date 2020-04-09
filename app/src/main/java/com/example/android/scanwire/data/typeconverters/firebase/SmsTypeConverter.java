package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.Sms;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class SmsTypeConverter {

    @TypeConverter
    public static Sms jsonToSms(String json) {
        return new Gson().fromJson(json, new TypeToken<Sms>() {
        }.getType());
    }

    @TypeConverter
    public static String smsToJson(Sms sms) {
        return new Gson().toJson(sms);
    }
}
