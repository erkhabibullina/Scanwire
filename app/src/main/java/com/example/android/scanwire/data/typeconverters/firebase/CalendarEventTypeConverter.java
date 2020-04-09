package com.example.android.scanwire.data.typeconverters.firebase;

import androidx.room.TypeConverter;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.CalendarEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class CalendarEventTypeConverter {

    @TypeConverter
    public static CalendarEvent jsonToCalenderEvent(String json) {
        return new Gson().fromJson(json, new TypeToken<CalendarEvent>() {
        }.getType());
    }

    @TypeConverter
    public static String calenderEventToJson(CalendarEvent calendarEvent) {
        return new Gson().toJson(calendarEvent);
    }
}
