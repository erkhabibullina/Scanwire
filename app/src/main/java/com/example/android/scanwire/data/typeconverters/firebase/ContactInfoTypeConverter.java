package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.ContactInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class ContactInfoTypeConverter {

    @TypeConverter
    public static ContactInfo jsonToContactInfo(String json) {
        return new Gson().fromJson(json, new TypeToken<ContactInfo>() {
        }.getType());
    }

    @TypeConverter
    public static String contactInfoToJson(ContactInfo contactInfo) {
        return new Gson().toJson(contactInfo);
    }
}
