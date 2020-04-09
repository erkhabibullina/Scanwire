package com.example.android.scanwire.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.scanwire.R;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.DateTimeUtil;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

/**
 * Adapter for displaying Barcodes in History Fragment.
 */
public class BarcodeAdapter extends PagedListAdapter<Barcode, BarcodeAdapter.ViewHolder> {

    private static DiffUtil.ItemCallback<Barcode> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Barcode>() {
                @Override
                public boolean areItemsTheSame(@NonNull Barcode oldItem, @NonNull Barcode newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Barcode oldItem, @NonNull Barcode newItem) {
                    return oldItem.diff(newItem);
                }
            };
    private Callback mCallback;
    private Context mContext;

    public BarcodeAdapter(Callback mCallback, Context mContext) {
        super(DIFF_CALLBACK);
        this.mCallback = mCallback;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BarcodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_barcode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeAdapter.ViewHolder holder, int position) {
        Barcode barcode = getItem(position);

        holder.mValue.setText(barcode.getRawValue());
        holder.mDate.setText(DateTimeUtil.getDateStringFromEpoch(barcode.getCreationEpoch(), mContext));

        switch (barcode.getType()) {
            case FirebaseVisionBarcode.TYPE_EMAIL:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_email));
                holder.mType.setText(mContext.getString(R.string.type_email));
                break;
            case FirebaseVisionBarcode.TYPE_PHONE:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_phone));
                holder.mType.setText(mContext.getString(R.string.type_phone));
                break;
            case FirebaseVisionBarcode.TYPE_SMS:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_sms));
                holder.mType.setText(mContext.getString(R.string.type_sms));
                break;
            case FirebaseVisionBarcode.TYPE_URL:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_http));
                holder.mType.setText(mContext.getString(R.string.type_url));
                break;
            case FirebaseVisionBarcode.TYPE_WIFI:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_wifi));
                holder.mType.setText(mContext.getString(R.string.type_wifi));
                holder.mValue.setText(barcode.getRawValue()
                        .substring(0, barcode.getRawValue().indexOf(";P:")));
                break;
            case FirebaseVisionBarcode.TYPE_GEO:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_location));
                holder.mType.setText(mContext.getString(R.string.type_geo_point));
                break;
            case FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_contact));
                holder.mType.setText(mContext.getString(R.string.type_contact_info));
                break;
            case FirebaseVisionBarcode.TYPE_CALENDAR_EVENT:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_calendar));
                holder.mType.setText(mContext.getString(R.string.type_calendar_event));
                break;
            case FirebaseVisionBarcode.TYPE_DRIVER_LICENSE:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_idcard));
                holder.mType.setText(mContext.getString(R.string.type_id));
                break;
            case FirebaseVisionBarcode.TYPE_ISBN:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_book));
                holder.mType.setText(mContext.getString(R.string.type_isbn));
                break;
            case FirebaseVisionBarcode.TYPE_TEXT:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_text));
                holder.mType.setText(mContext.getString(R.string.type_text));
                break;
            case Barcode.TYPE_GENERATED:
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_generate));
                holder.mType.setText(mContext.getString(R.string.type_generated));
                break;
            default: // UNKNOWN
                holder.mIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_unknown));
                holder.mType.setText(mContext.getString(R.string.type_unknown));
        }
    }

    public interface Callback {
        void onBarcodeClick(int index);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIcon;
        private TextView mType, mValue, mDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.list_barcode_icon);
            mType = itemView.findViewById(R.id.list_barcode_type);
            mValue = itemView.findViewById(R.id.list_barcode_value);
            mDate = itemView.findViewById(R.id.list_barcode_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onBarcodeClick(getAdapterPosition());
        }
    }
}
