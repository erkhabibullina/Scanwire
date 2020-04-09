package com.example.android.scanwire.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.android.scanwire.data.typeconverters.firebase.CalendarEventTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.ContactInfoTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.DriverLicenseTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.EmailTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.GeoPointTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.PhoneTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.SmsTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.UrlTypeConverter;
import com.example.android.scanwire.data.typeconverters.firebase.WifiTypeConverter;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

@Entity(tableName = "barcodes")
public class Barcode {

    public static final int TYPE_GENERATED = 999;

    public static final String idColumn = "_id";
    public static final String creationEpochColumn = "creation_epoch";
    public static final String generatedColumn = "generated";
    public static final String rawValueColumn = "raw_value";
    public static final String displayValueColumn = "display_value";
    public static final String typeColumn = "type";
    public static final String emailColumn = "email";
    public static final String phoneColumn = "phone";
    public static final String smsColumn = "sms";
    public static final String urlColumn = "url";
    public static final String wifiColumn = "wifi";
    public static final String geoPointColumn = "geo_point";
    public static final String contactInfoColumn = "contact_info";
    public static final String calendarEventColumn = "calendar_event";
    public static final String driverLicenseColumn = "driver_license";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = idColumn)
    private long _id;

    @ColumnInfo(name = creationEpochColumn)
    private long creationEpoch;

    @ColumnInfo(name = generatedColumn)
    private boolean generated;

    @ColumnInfo(name = rawValueColumn)
    private String rawValue;

    @ColumnInfo(name = displayValueColumn)
    private String displayValue;

    @ColumnInfo(name = typeColumn)
    private int type;

    @Nullable
    @ColumnInfo(name = emailColumn)
    @TypeConverters(EmailTypeConverter.class)
    private FirebaseVisionBarcode.Email email;

    @Nullable
    @ColumnInfo(name = phoneColumn)
    @TypeConverters(PhoneTypeConverter.class)
    private FirebaseVisionBarcode.Phone phone;

    @Nullable
    @ColumnInfo(name = smsColumn)
    @TypeConverters(SmsTypeConverter.class)
    private FirebaseVisionBarcode.Sms sms;

    @Nullable
    @ColumnInfo(name = urlColumn)
    @TypeConverters(UrlTypeConverter.class)
    private FirebaseVisionBarcode.UrlBookmark url;

    @Nullable
    @ColumnInfo(name = wifiColumn)
    @TypeConverters(WifiTypeConverter.class)
    private FirebaseVisionBarcode.WiFi wifi;

    @Nullable
    @ColumnInfo(name = geoPointColumn)
    @TypeConverters(GeoPointTypeConverter.class)
    private FirebaseVisionBarcode.GeoPoint geoPoint;

    @Nullable
    @ColumnInfo(name = contactInfoColumn)
    @TypeConverters(ContactInfoTypeConverter.class)
    private FirebaseVisionBarcode.ContactInfo contactInfo;

    @Nullable
    @ColumnInfo(name = calendarEventColumn)
    @TypeConverters(CalendarEventTypeConverter.class)
    private FirebaseVisionBarcode.CalendarEvent calendarEvent;

    @Nullable
    @ColumnInfo(name = driverLicenseColumn)
    @TypeConverters(DriverLicenseTypeConverter.class)
    private FirebaseVisionBarcode.DriverLicense driverLicense;

    /**
     * Constructor for new Barcode Objects.
     */
    @Ignore
    public Barcode(long creationEpoch, boolean generated,
                   String rawValue, String displayValue, int type,
                   @Nullable FirebaseVisionBarcode.Email email,
                   @Nullable FirebaseVisionBarcode.Phone phone,
                   @Nullable FirebaseVisionBarcode.Sms sms,
                   @Nullable FirebaseVisionBarcode.UrlBookmark url,
                   @Nullable FirebaseVisionBarcode.WiFi wifi,
                   @Nullable FirebaseVisionBarcode.GeoPoint geoPoint,
                   @Nullable FirebaseVisionBarcode.ContactInfo contactInfo,
                   @Nullable FirebaseVisionBarcode.CalendarEvent calendarEvent,
                   @Nullable FirebaseVisionBarcode.DriverLicense driverLicense) {
        this.creationEpoch = creationEpoch;
        this.generated = generated;
        this.rawValue = rawValue;
        this.displayValue = displayValue;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.sms = sms;
        this.url = url;
        this.wifi = wifi;
        this.geoPoint = geoPoint;
        this.contactInfo = contactInfo;
        this.calendarEvent = calendarEvent;
        this.driverLicense = driverLicense;
    }

    /**
     * Room Constructor.
     */
    public Barcode(long _id, long creationEpoch, boolean generated,
                   String rawValue, String displayValue, int type,
                   @Nullable FirebaseVisionBarcode.Email email,
                   @Nullable FirebaseVisionBarcode.Phone phone,
                   @Nullable FirebaseVisionBarcode.Sms sms,
                   @Nullable FirebaseVisionBarcode.UrlBookmark url,
                   @Nullable FirebaseVisionBarcode.WiFi wifi,
                   @Nullable FirebaseVisionBarcode.GeoPoint geoPoint,
                   @Nullable FirebaseVisionBarcode.ContactInfo contactInfo,
                   @Nullable FirebaseVisionBarcode.CalendarEvent calendarEvent,
                   @Nullable FirebaseVisionBarcode.DriverLicense driverLicense) {
        this._id = _id;
        this.creationEpoch = creationEpoch;
        this.generated = generated;
        this.rawValue = rawValue;
        this.displayValue = displayValue;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.sms = sms;
        this.url = url;
        this.wifi = wifi;
        this.geoPoint = geoPoint;
        this.contactInfo = contactInfo;
        this.calendarEvent = calendarEvent;
        this.driverLicense = driverLicense;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    public FirebaseVisionBarcode.Email getEmail() {
        return email;
    }

    public void setEmail(@Nullable FirebaseVisionBarcode.Email email) {
        this.email = email;
    }

    @Nullable
    public FirebaseVisionBarcode.Phone getPhone() {
        return phone;
    }

    public void setPhone(@Nullable FirebaseVisionBarcode.Phone phone) {
        this.phone = phone;
    }

    @Nullable
    public FirebaseVisionBarcode.Sms getSms() {
        return sms;
    }

    public void setSms(@Nullable FirebaseVisionBarcode.Sms sms) {
        this.sms = sms;
    }

    @Nullable
    public FirebaseVisionBarcode.UrlBookmark getUrl() {
        return url;
    }

    public void setUrl(@Nullable FirebaseVisionBarcode.UrlBookmark url) {
        this.url = url;
    }

    @Nullable
    public FirebaseVisionBarcode.WiFi getWifi() {
        return wifi;
    }

    public void setWifi(@Nullable FirebaseVisionBarcode.WiFi wifi) {
        this.wifi = wifi;
    }

    @Nullable
    public FirebaseVisionBarcode.GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(@Nullable FirebaseVisionBarcode.GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Nullable
    public FirebaseVisionBarcode.ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(@Nullable FirebaseVisionBarcode.ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Nullable
    public FirebaseVisionBarcode.CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }

    public void setCalendarEvent(@Nullable FirebaseVisionBarcode.CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    @Nullable
    public FirebaseVisionBarcode.DriverLicense getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(@Nullable FirebaseVisionBarcode.DriverLicense driverLicense) {
        this.driverLicense = driverLicense;
    }

    /**
     * Used for comparing barcodes on scan so that
     * identical ones only will be added once.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Barcode barcode = (Barcode) obj;
        return type == barcode.getType() &&
                rawValue.equals(barcode.getRawValue()) &&
                displayValue.equals(barcode.getDisplayValue());
    }

    /**
     * Compare contents for diff check in Adapter, determines whether to update the item.
     * This should essentially be all the data which is displayed in a list item.
     *
     * @param barcode Barcode to compare to this
     * @return true when contents are the same, false = Adapter will update
     */
    public boolean diff(@NonNull Barcode barcode) {
        return type == barcode.getType() &&
                rawValue.equals(barcode.getRawValue()) &&
                displayValue.equals(barcode.getDisplayValue()) &&
                creationEpoch == barcode.getCreationEpoch();
    }

    @Override
    public String toString() {
        return "Barcode{" +
                "_id=" + _id +
                ", creationEpoch=" + creationEpoch +
                ", generated=" + generated +
                ", rawValue='" + rawValue + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", type=" + type +
                ", email=" + email +
                ", phone=" + phone +
                ", sms=" + sms +
                ", url=" + url +
                ", wifi=" + wifi +
                ", geoPoint=" + geoPoint +
                ", contactInfo=" + contactInfo +
                ", calendarEvent=" + calendarEvent +
                ", driverLicense=" + driverLicense +
                '}';
    }
}
