package com.example.android.scanwire.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.android.scanwire.R;
import com.example.android.scanwire.models.Barcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.*;

public abstract class BarcodeUtil {

    private static final String TAG = BarcodeUtil.class.getSimpleName();

    /**
     * Get Barcode Object which can be stored in Room Db from FirebaseVisionBarcode.
     */
    public static Barcode getBarcode(@NonNull FirebaseVisionBarcode scannedBarcode) {
        String rawValue = scannedBarcode.getRawValue();
        String displayValue = scannedBarcode.getDisplayValue();
        int type = scannedBarcode.getValueType();
        Email email = null;
        Phone phone = null;
        Sms sms = null;
        UrlBookmark url = null;
        WiFi wifi = null;
        GeoPoint geoPoint = null;
        ContactInfo contactInfo = null;
        CalendarEvent calendarEvent = null;
        DriverLicense driverLicense = null;

        String logType = "Barcode Type: ";
        switch (type) {
            case TYPE_EMAIL:
                Log.d(TAG, logType + "EMAIL");
                email = scannedBarcode.getEmail();
                break;
            case TYPE_PHONE:
                Log.d(TAG, logType + "PHONE");
                phone = scannedBarcode.getPhone();
                break;
            case TYPE_SMS:
                Log.d(TAG, logType + "SMS");
                sms = scannedBarcode.getSms();
                break;
            case TYPE_URL:
                Log.d(TAG, logType + "URL");
                url = scannedBarcode.getUrl();
                break;
            case TYPE_WIFI:
                Log.d(TAG, logType + "WIFI");
                wifi = scannedBarcode.getWifi();
                break;
            case TYPE_GEO:
                Log.d(TAG, logType + "GEO POINT");
                geoPoint = scannedBarcode.getGeoPoint();
                break;
            case TYPE_CONTACT_INFO:
                Log.d(TAG, logType + "CONTACT INFO");
                contactInfo = scannedBarcode.getContactInfo();
                break;
            case TYPE_CALENDAR_EVENT:
                Log.d(TAG, logType + "CALENDAR EVENT");
                calendarEvent = scannedBarcode.getCalendarEvent();
                break;
            case TYPE_DRIVER_LICENSE:
                Log.d(TAG, logType + "DRIVER LICENSE");
                driverLicense = scannedBarcode.getDriverLicense();
                break;
            case TYPE_ISBN:
                Log.d(TAG, logType + "ISBN");
                break;
            case TYPE_TEXT:
                Log.d(TAG, logType + "TEXT");
                break;
            default: // UNKNOWN
                Log.d(TAG, logType + "UNKNOWN");
        }

        return new Barcode(new Date().getTime(), false, rawValue, displayValue, type,
                email, phone, sms, url, wifi, geoPoint, contactInfo, calendarEvent, driverLicense);
    }

    /**
     * Get Barcode Object which can be stored in Room Db from rawValue.
     * Used for user generated QR codes.
     */
    public static Barcode getBarcode(@NonNull String rawValue) {
        return new Barcode(new Date().getTime(), true, rawValue, rawValue, Barcode.TYPE_GENERATED,
                null, null, null, null, null, null, null, null, null);
    }

    /**
     * Get String representation of FirebaseVisionBarcode type.
     *
     * @param type FireBaseVisionBarcode.TYPE_?
     */
    public static String getTypeString(Context context, int type) {
        switch (type) {
            case TYPE_EMAIL:
                return context.getString(R.string.type_email);
            case TYPE_PHONE:
                return context.getString(R.string.type_phone);
            case TYPE_SMS:
                return context.getString(R.string.type_sms);
            case TYPE_URL:
                return context.getString(R.string.type_url);
            case TYPE_WIFI:
                return context.getString(R.string.type_wifi);
            case TYPE_GEO:
                return context.getString(R.string.type_geo_point);
            case TYPE_CONTACT_INFO:
                return context.getString(R.string.type_contact_info);
            case TYPE_CALENDAR_EVENT:
                return context.getString(R.string.type_calendar_event);
            case TYPE_DRIVER_LICENSE:
                return context.getString(R.string.type_id);
            case TYPE_ISBN:
                return context.getString(R.string.type_isbn);
            case TYPE_TEXT:
                return context.getString(R.string.type_text);
            default: // UNKNOWN
                return context.getString(R.string.type_unknown);
        }
    }

    /**
     * Get List of Integer representations of Barcode type query.
     */
    public static List<Integer> getQueryTypes(Context context, String query) {
        List<Integer> queryMatches = new ArrayList<>();
        if (context.getString(R.string.type_email).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_EMAIL);
        }
        if (context.getString(R.string.type_phone).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_PHONE);
        }
        if (context.getString(R.string.type_sms).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_SMS);
        }
        if (context.getString(R.string.type_url).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_URL);
        }
        if (context.getString(R.string.type_wifi).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_WIFI);
        }
        if (context.getString(R.string.type_geo_point).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_GEO);
        }
        if (context.getString(R.string.type_contact_info).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_CONTACT_INFO);
        }
        if (context.getString(R.string.type_calendar_event).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_CALENDAR_EVENT);
        }
        if (context.getString(R.string.type_id).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_DRIVER_LICENSE);
        }
        if (context.getString(R.string.type_isbn).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_ISBN);
        }
        if (context.getString(R.string.type_text).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_TEXT);
        }
        if (context.getString(R.string.type_unknown).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(TYPE_UNKNOWN);
        }
        if (context.getString(R.string.type_generated).toLowerCase().contains(query.toLowerCase())) {
            queryMatches.add(Barcode.TYPE_GENERATED);
        }
        return queryMatches;
    }
}
