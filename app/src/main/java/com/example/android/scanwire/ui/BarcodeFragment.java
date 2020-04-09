package com.example.android.scanwire.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.example.android.scanwire.databinding.FragmentBarcodeBinding;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.BarcodeUtil;
import com.example.android.scanwire.utils.BitmapUtil;
import com.example.android.scanwire.utils.DateTimeUtil;
import com.example.android.scanwire.viewmodels.BarcodeViewModel;
import com.example.android.scanwire.viewmodels.ViewModelProviderFactory;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class BarcodeFragment extends DaggerFragment {

    private static final String TAG = BarcodeFragment.class.getSimpleName();
    private FragmentBarcodeBinding mLayout;
    private BarcodeViewModel mViewModel;
    private Barcode mBarcode;
    private long mBarcodeID;
    private boolean mShowWifiKey;

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    @Inject
    Context applicationContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_barcode, container, false);
        mViewModel = new ViewModelProvider(this, viewModelProviderFactory).get(BarcodeViewModel.class);

        // Enable Toolbar MenuItem handling.
        setHasOptionsMenu(true);

        // Get Barcode ID
        mBarcodeID = getArguments().getLong(Constants.PARCEL_BARCODE_ID);

        // Observere Barcode
        observeBarcode(mBarcodeID);

        return mLayout.getRoot();
    }

    /**
     * Observe Barcode from DB and load data into views.
     *
     * @param id ID of Barcode to observe
     */
    @SuppressLint("WifiManagerPotentialLeak")
    private void observeBarcode(long id) {
        mViewModel.getBarcode(id).observe(getViewLifecycleOwner(), barcode -> {
            if (barcode != null) {
                mBarcode = barcode;
                mLayout.barcodeDate.setText(DateTimeUtil
                        .getDateStringFromEpoch(mBarcode.getCreationEpoch(), getActivity()));
                mLayout.barcodeWifiViewKey.setVisibility(View.GONE);
                switch (barcode.getType()) {
                    case FirebaseVisionBarcode.TYPE_EMAIL:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_email));
                        mLayout.barcodeType.setText(getString(R.string.type_email));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        FirebaseVisionBarcode.Email email = barcode.getEmail();

                        // Action: Send Email
                        setAction(1, R.drawable.ic_email, R.string.action_email, v -> {
                            sendEmail(email.getAddress(), email.getSubject(), email.getBody());
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_PHONE:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
                        mLayout.barcodeType.setText(getString(R.string.type_phone));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        // Action: Call
                        setAction(1, R.drawable.ic_phone, R.string.action_call, v -> {
                            call(barcode.getPhone().getNumber());
                        });

                        // Action: Send SMS
                        setAction(2, R.drawable.ic_sms, R.string.action_sms, v -> {
                            sendSms(barcode.getPhone().getNumber(), null);
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_SMS:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_sms));
                        mLayout.barcodeType.setText(getString(R.string.type_sms));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        // Action: Send SMS
                        setAction(1, R.drawable.ic_sms, R.string.action_sms, v -> {
                            sendSms(barcode.getSms().getPhoneNumber(), barcode.getSms().getMessage());
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_URL:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_http));
                        mLayout.barcodeType.setText(getString(R.string.type_url));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        // Action: Open
                        setAction(1, R.drawable.ic_open_in_browser, R.string.action_open, v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(barcode.getDisplayValue()));
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_WIFI:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi));
                        mLayout.barcodeType.setText(getString(R.string.type_wifi));
                        StringBuilder wifiValue = new StringBuilder();
                        wifiValue.append(barcode.getWifi().getSsid())
                                .append(" (");
                        switch (barcode.getWifi().getEncryptionType()) {
                            case FirebaseVisionBarcode.WiFi.TYPE_OPEN:
                                wifiValue.append(getString(R.string.wifi_open));
                                break;
                            case FirebaseVisionBarcode.WiFi.TYPE_WEP:
                                wifiValue.append(getString(R.string.wifi_wep));
                                mLayout.barcodeWifiViewKey.setVisibility(View.VISIBLE);
                                break;
                            case FirebaseVisionBarcode.WiFi.TYPE_WPA:
                                wifiValue.append(getString(R.string.wifi_wpa));
                                mLayout.barcodeWifiViewKey.setVisibility(View.VISIBLE);
                                break;
                        }
                        wifiValue.append(")");

                        if (mLayout.barcodeWifiViewKey.getVisibility() == View.VISIBLE) {
                            mLayout.barcodeWifiViewKey.setOnClickListener(v -> {
                                if (mShowWifiKey) {
                                    mLayout.barcodeWifiViewKey.setText(getString(R.string.wifi_view_key));
                                    mShowWifiKey = false;
                                    wifiValue.delete(wifiValue.indexOf("\n\n" +
                                            barcode.getWifi().getPassword()), wifiValue.length());
                                } else {
                                    mLayout.barcodeWifiViewKey.setText(getString(R.string.wifi_hide_key));
                                    mShowWifiKey = true;
                                    wifiValue.append("\n\n")
                                            .append(barcode.getWifi().getPassword());
                                }
                                mLayout.barcodeValue.setText(wifiValue.toString());
                            });
                        }

                        mLayout.barcodeValue.setText(wifiValue.toString());

                        // Action: Connect
                        setAction(1, R.drawable.ic_wifi, R.string.action_connect, v -> {
                            WifiConfiguration wifiConfig = new WifiConfiguration();
                            wifiConfig.SSID = String.format("\"%s\"", barcode.getWifi().getSsid());
                            wifiConfig.preSharedKey = String.format("\"%s\"", barcode.getWifi().getPassword());
                            WifiManager wifiManager = (WifiManager) applicationContext
                                    .getSystemService(Context.WIFI_SERVICE);
                            int netId = wifiManager.addNetwork(wifiConfig);
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(netId, true);
                            wifiManager.reconnect();
                            Toast.makeText(getActivity(), getString(R.string.connecting,
                                    barcode.getWifi().getSsid()),
                                    Toast.LENGTH_LONG).show();
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_GEO:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));
                        mLayout.barcodeType.setText(getString(R.string.type_geo_point));

                        StringBuilder geoValue = new StringBuilder();

                        double latitude = barcode.getGeoPoint().getLat(),
                                longitude = barcode.getGeoPoint().getLng();

                        geoValue.append(getString(R.string.geo_latitude, String.valueOf(latitude)))
                                .append(", ")
                                .append(getString(R.string.geo_longitude, String.valueOf(longitude)));

                        mLayout.barcodeValue.setText(geoValue.toString());

                        // Action: Map
                        setAction(1, R.drawable.ic_location, R.string.action_map, v -> {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                            String data = "https://www.google.com/maps/@?api=1&map_action=map&center="
                                    + barcode.getGeoPoint().getLat() + "," + barcode.getGeoPoint().getLng();
                            intent.setData(Uri.parse(data));
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        });

                        // Action: Directions
                        setAction(2, R.drawable.ic_directions, R.string.action_directions, v -> {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                            String data = "https://www.google.com/maps/dir/?api=1&destination="
                                    + barcode.getGeoPoint().getLat() + "," + barcode.getGeoPoint().getLng();
                            intent.setData(Uri.parse(data));
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_contact));
                        mLayout.barcodeType.setText(getString(R.string.type_contact_info));

                        FirebaseVisionBarcode.ContactInfo contactInfo = barcode.getContactInfo();
                        StringBuilder contactValue = new StringBuilder();
                        List<String> contactAddresses = new ArrayList<>();
                        List<String> contactPhoneNumbers = new ArrayList<>();
                        List<String> contactEmails = new ArrayList<>();

                        // Title
                        String title = contactInfo.getTitle();
                        if (title != null && !title.isEmpty()) {
                            contactValue.append(title)
                                    .append(" ");
                        }

                        // Name
                        String name = contactInfo.getName().getFormattedName();
                        if (name != null && !name.isEmpty()) {
                            contactValue.append(name)
                                    .append("\n");
                        }

                        // Organization
                        String organization = contactInfo.getOrganization();
                        if (organization != null && !organization.isEmpty()) {
                            contactValue.append(organization)
                                    .append("\n");
                        }

                        // Addresses
                        for (FirebaseVisionBarcode.Address address : contactInfo.getAddresses()) {
                            String x = Arrays.toString(address.getAddressLines()),
                                    clean = x.substring(1, x.length() - 1);
                            contactAddresses.add(clean);
                            contactValue.append(clean);
                            contactValue.append("\n");
                        }

                        // Emails
                        for (FirebaseVisionBarcode.Email address : contactInfo.getEmails()) {
                            contactEmails.add(address.getAddress());
                            contactValue.append(address.getAddress());
                            contactValue.append("\n");
                        }

                        // Phones
                        for (FirebaseVisionBarcode.Phone phone : contactInfo.getPhones()) {
                            contactPhoneNumbers.add(phone.getNumber());
                            contactValue.append(phone.getNumber());
                            contactValue.append("\n");
                        }

                        // Urls
                        if (contactInfo.getUrls() != null) {
                            for (String url : contactInfo.getUrls()) {
                                contactValue.append(url)
                                        .append("\n");
                            }
                        }

                        mLayout.barcodeValue.setText(contactValue.toString().trim());

                        // Action: Add contact
                        setAction(1, R.drawable.ic_add_contact, R.string.action_add, v -> {
                            Intent intent = new Intent(Intent.ACTION_INSERT)
                                    .setType(ContactsContract.Contacts.CONTENT_TYPE);

                            // Name
                            intent.putExtra(ContactsContract.Intents.Insert.NAME,
                                    contactInfo.getName().getFormattedName());

                            // Company
                            intent.putExtra(ContactsContract.Intents.Insert.COMPANY,
                                    contactInfo.getOrganization());

                            // Phone numbers
                            if (!contactPhoneNumbers.isEmpty()) {
                                intent.putExtra(ContactsContract.Intents.Insert.PHONE,
                                        contactPhoneNumbers.get(0));
                                if (contactPhoneNumbers.size() > 1) {
                                    intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE,
                                            contactPhoneNumbers.get(1));
                                }
                                if (contactPhoneNumbers.size() > 2) {
                                    intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE,
                                            contactPhoneNumbers.get(2));
                                }
                            }

                            // E-mails
                            if (!contactEmails.isEmpty()) {
                                intent.putExtra(ContactsContract.Intents.Insert.EMAIL,
                                        contactEmails.get(0));
                                if (contactEmails.size() > 1) {
                                    intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL,
                                            contactEmails.get(1));
                                }
                                if (contactEmails.size() > 2) {
                                    intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL,
                                            contactEmails.get(2));
                                }
                            }

                            // Address
                            if (!contactInfo.getAddresses().isEmpty()) {
                                intent.putExtra(ContactsContract.Intents.Insert.POSTAL,
                                        contactAddresses.get(0));
                            }

                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        });

                        // Action: Call
                        if (!contactPhoneNumbers.isEmpty()) {
                            setAction(2, R.drawable.ic_phone, R.string.action_call, v -> {
                                call(contactPhoneNumbers.get(0));
                            });
                        }

                        // Action: E-mail
                        if (!contactEmails.isEmpty()) {
                            if (!contactPhoneNumbers.isEmpty()) {
                                setAction(3, R.drawable.ic_email, R.string.action_email, v -> {
                                    actionEmail(contactInfo);
                                });
                            } else {
                                setAction(2, R.drawable.ic_email, R.string.action_email, v -> {
                                    actionEmail(contactInfo);
                                });
                            }
                        }

                        // Action: Map
                        if (!contactAddresses.isEmpty()) {
                            if (!contactEmails.isEmpty()) { // Column 4
                                setAction(4, R.drawable.ic_location, R.string.action_map, v -> {
                                    actionMap(contactAddresses.get(0));
                                });
                            } else if (!contactPhoneNumbers.isEmpty()) { // Column 3
                                setAction(3, R.drawable.ic_location, R.string.action_map, v -> {
                                    actionMap(contactAddresses.get(0));
                                });
                            } else { // Column 2
                                setAction(2, R.drawable.ic_location, R.string.action_map, v -> {
                                    actionMap(contactAddresses.get(0));
                                });
                            }
                        }
                        break;
                    case FirebaseVisionBarcode.TYPE_CALENDAR_EVENT:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_calendar));
                        mLayout.barcodeType.setText(getString(R.string.type_calendar_event));

                        FirebaseVisionBarcode.CalendarEvent calendarEvent = barcode.getCalendarEvent();
                        StringBuilder calendarValue = new StringBuilder();

                        // Event name
                        if (calendarEvent.getSummary() != null && !calendarEvent.getSummary().isEmpty()) {
                            calendarValue.append(calendarEvent.getSummary())
                                    .append("\n");
                        }

                        // Event location
                        if (calendarEvent.getLocation() != null && !calendarEvent.getLocation().isEmpty()) {
                            calendarValue.append(calendarEvent.getLocation())
                                    .append("\n");
                        }

                        // Event start/end
                        long eventStart = getEpochFromBarcodeDateString(calendarEvent.getStart().getRawValue()),
                                eventEnd = getEpochFromBarcodeDateString(calendarEvent.getEnd().getRawValue());
                        calendarValue.append(DateTimeUtil.getDateStringFromEpoch(eventStart, getActivity()))
                                .append(" - ")
                                .append(DateTimeUtil.getDateStringFromEpoch(eventEnd, getActivity()));

                        mLayout.barcodeValue.setText(calendarValue.toString().trim());

                        // Action: Add Calendar Event
                        setAction(1, R.drawable.ic_calendar, R.string.action_add, v -> {
                            Intent intent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.TITLE, calendarEvent.getSummary())
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, calendarEvent.getLocation());
                            if (eventStart > 0) {
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStart);
                            }
                            if (eventEnd > 0) {
                                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEnd);
                            }
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_DRIVER_LICENSE:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_idcard));
                        mLayout.barcodeType.setText(getString(R.string.type_id));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());
                        hideActions();
                        break;
                    case FirebaseVisionBarcode.TYPE_ISBN:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_book));
                        mLayout.barcodeType.setText(getString(R.string.type_isbn));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        // Action: Search
                        setAction(1, R.drawable.ic_search, R.string.action_search, v -> {
                            googleSearch(barcode.getDisplayValue());
                        });
                        break;
                    case FirebaseVisionBarcode.TYPE_TEXT:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_text));
                        mLayout.barcodeType.setText(getString(R.string.type_text));
                        mLayout.barcodeValue.setText(barcode.getDisplayValue());

                        // Action: Search
                        setAction(1, R.drawable.ic_search, R.string.action_search, v -> {
                            googleSearch(barcode.getDisplayValue());
                        });
                        break;
                    case Barcode.TYPE_GENERATED:
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_generate));
                        mLayout.barcodeType.setText(getString(R.string.type_generated));
                        mLayout.barcodeValue.setText(barcode.getRawValue());
                        hideActions();
                        break;
                    default: // UNKNOWN
                        mLayout.barcodeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknown));
                        mLayout.barcodeType.setText(getString(R.string.type_unknown));
                        mLayout.barcodeValue.setText(barcode.getRawValue());
                }
            }
        });
    }

    /**
     * Set column Action.
     */
    private void setAction(int column, int iconId, int textId, View.OnClickListener onClickListener) {
        switch (column) {
            case 1:
                mLayout.barcodeAction1.setVisibility(View.VISIBLE);
                mLayout.barcodeAction1Icon.setImageDrawable(getResources().getDrawable(iconId));
                mLayout.barcodeAction1Text.setText(getString(textId));
                mLayout.barcodeAction1.setOnClickListener(onClickListener);
                mLayout.barcodeAction1Icon.setContentDescription(getString(textId));
                break;
            case 2:
                mLayout.barcodeAction2.setVisibility(View.VISIBLE);
                mLayout.barcodeAction2Icon.setImageDrawable(getResources().getDrawable(iconId));
                mLayout.barcodeAction2Text.setText(getString(textId));
                mLayout.barcodeAction2.setOnClickListener(onClickListener);
                mLayout.barcodeAction2Icon.setContentDescription(getString(textId));
                break;
            case 3:
                mLayout.barcodeAction3.setVisibility(View.VISIBLE);
                mLayout.barcodeAction3Icon.setImageDrawable(getResources().getDrawable(iconId));
                mLayout.barcodeAction3Text.setText(getString(textId));
                mLayout.barcodeAction3.setOnClickListener(onClickListener);
                mLayout.barcodeAction3Icon.setContentDescription(getString(textId));
                break;
            case 4:
                mLayout.barcodeAction4.setVisibility(View.VISIBLE);
                mLayout.barcodeAction4Icon.setImageDrawable(getResources().getDrawable(iconId));
                mLayout.barcodeAction4Text.setText(getString(textId));
                mLayout.barcodeAction4.setOnClickListener(onClickListener);
                mLayout.barcodeAction4Icon.setContentDescription(getString(textId));
                break;
        }
    }

    /**
     * Hide all Action columns.
     */
    private void hideActions() {
        mLayout.barcodeAction1.setVisibility(View.GONE);
        mLayout.barcodeAction2.setVisibility(View.GONE);
        mLayout.barcodeAction3.setVisibility(View.GONE);
        mLayout.barcodeAction4.setVisibility(View.GONE);
    }

    /**
     * Set E-mail Action from ContactInfo.
     */
    private void actionEmail(FirebaseVisionBarcode.ContactInfo contactInfo) {
        String[] addrArray = new String[contactInfo.getEmails().size()];
        List<String> addrList = new ArrayList<>();
        for (FirebaseVisionBarcode.Email address : contactInfo.getEmails()) {
            addrList.add(address.getAddress());
        }
        addrList.toArray(addrArray);
        sendEmail(null,
                null,
                addrArray);
    }

    /**
     * Set Map Action from ContactInfo (Address).
     */
    private void actionMap(String address) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        String reformat = address.replaceAll(" ", "+");
        String data = "https://www.google.com/maps/search/?api=1&map_action=map&query="
                + reformat;
        intent.setData(Uri.parse(data));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Get Epoch from scanned Barcode date String.
     */
    private long getEpochFromBarcodeDateString(String barcodeDateString) {
        StringBuilder dateString = new StringBuilder(barcodeDateString);
        dateString.deleteCharAt(barcodeDateString.indexOf("T"));
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long epoch = 0;
        try {
            Date date = format.parse(dateString.toString());
            epoch = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epoch;
    }

    /**
     * Doesn't actually call the number directly but instead
     * opens the phone app and inserts the number.
     */
    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Open phone app to send SMS with or without pre-composed message.
     */
    private void sendSms(String number, @Nullable String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + number));
        if (message != null) {
            intent.putExtra("sms_body", message);
        }
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Send E-mail.
     */
    private void sendEmail(String subject, String body, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("*/*")
                .putExtra(Intent.EXTRA_EMAIL, addresses)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Search Google with intent, don't use Intent.ACTION_WEB_SEARCH
     * for searches because few browsers support it.
     */
    private void googleSearch(String query) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.google.com/#q=" + query));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Clipboard String and inform with Toast.
     */
    private void clipboard(String str) {
        ClipboardManager clipboardManager =
                (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(
                BarcodeUtil.getTypeString(getActivity(),
                        mBarcode.getType()), str);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getActivity(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy:
                clipboard(mLayout.barcodeValue.getText().toString());
                break;
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mLayout.barcodeValue.getText().toString());
                intent.setType("text/plain");
                Intent chooser = Intent.createChooser(intent, null);
                startActivity(chooser);
                break;
            case R.id.action_generate_qr:
                // Generate QR
                try {
                    mLayout.qrCodeLayout.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapUtil.getQrAsBitmap(mBarcode.getRawValue(), 1000, 1000);
                    mLayout.qrCode.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.w(TAG, "Could not generate QR Code");
                    mLayout.qrCodeLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.action_copy_raw:
                clipboard(mBarcode.getRawValue());
                break;
            case R.id.action_delete:
                mViewModel.deleteBarcode(mBarcode);
                Toast.makeText(applicationContext,
                        getString(R.string.barcode_deleted),
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
