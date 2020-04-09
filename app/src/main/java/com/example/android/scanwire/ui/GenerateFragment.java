package com.example.android.scanwire.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.scanwire.R;
import com.example.android.scanwire.databinding.FragmentGenerateBinding;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.BarcodeUtil;
import com.example.android.scanwire.utils.BitmapUtil;
import com.example.android.scanwire.utils.KeyboardUtil;
import com.example.android.scanwire.viewmodels.GenerateViewModel;
import com.example.android.scanwire.viewmodels.ViewModelProviderFactory;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class GenerateFragment extends DaggerFragment {

    private static final String TAG = GenerateFragment.class.getSimpleName();
    private FragmentGenerateBinding mLayout;
    private GenerateViewModel mViewModel;
    private String mLastSavedValue;
    private boolean mQrIsGenerated;

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_generate, container, false);
        mViewModel = new ViewModelProvider(this, viewModelProviderFactory).get(GenerateViewModel.class);

        // Set Title
        getActivity().setTitle(R.string.action_generate);

        // Enable Toolbar MenuItem handling.
        setHasOptionsMenu(true);

        mLayout.generateButton.setOnClickListener(v -> {
            // Generate QR
            String data = mLayout.generateInput.getText().toString();
            if (!data.isEmpty()) {
                try {
                    Bitmap bitmap = BitmapUtil.getQrAsBitmap(mLayout.generateInput.getText().toString(), 1000, 1000);
                    mLayout.qrCode.setImageBitmap(bitmap);
                    mLayout.qrCodeLayout.setVisibility(View.VISIBLE);
                    KeyboardUtil.hideKeyboard(getActivity(), mLayout.getRoot());
                    mQrIsGenerated = true;
                } catch (Exception e) {
                    Log.w(TAG, "Could not generate QR Code");
                }
            } else {
                mLayout.qrCodeLayout.setVisibility(View.GONE);
            }
        });

        return mLayout.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtil.hideKeyboard(getActivity(), mLayout.getRoot());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_generate, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String data = mLayout.generateInput.getText().toString();
                if (mQrIsGenerated && !data.equals(mLastSavedValue)) {
                    mQrIsGenerated = false;
                    if (!data.isEmpty()) {
                        mLastSavedValue = data;
                        Barcode barcode = BarcodeUtil.getBarcode(mLayout.generateInput.getText().toString());
                        long barcodeId = mViewModel.insertBarcode(barcode);
                        barcode.setId(barcodeId);
                        Snackbar.make(mLayout.getRoot(), getString(R.string.barcode_saved), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), v -> {
                                    mViewModel.deleteBarcode(barcode);
                                    mQrIsGenerated = true;
                                    mLastSavedValue = "";
                                }).show();
                    }
                } else {
                    String message;
                    if (data.equals(mLastSavedValue) || data.isEmpty()) {
                        message = getString(R.string.generate_new_value_required);
                    } else {
                        message = getString(R.string.generate_first);
                    }
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
