package com.example.android.scanwire.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.scanwire.BarcodeFragmentPagerAdapter;
import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.example.android.scanwire.databinding.ActivityBarcodesBinding;

import java.util.ArrayList;
import java.util.List;

public class BarcodeActivity extends AppCompatActivity {

    private static final String TAG = BarcodeActivity.class.getSimpleName();
    private ActivityBarcodesBinding mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Barcode IDs
        long[] barcodeIDs = getIntent().getLongArrayExtra(Constants.PARCEL_BARCODE_IDS);
        if (barcodeIDs.length > 1) { // Multiple Barcodes
            setContentView(R.layout.activity_barcodes);
            mLayout = DataBindingUtil.setContentView(this, R.layout.activity_barcodes);

            // Setup Fragments
            List<Fragment> fragments = new ArrayList<>();
            for (long l : barcodeIDs) {
                fragments.add(getBarcodeFragment(l));
            }

            // Launch ViewPager
            mLayout.viewpager.setAdapter(new BarcodeFragmentPagerAdapter(
                    getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                    fragments));
            mLayout.tabLayout.setupWithViewPager(mLayout.viewpager);

        } else { // Single Barcode
            setContentView(R.layout.activity_barcode);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_barcode, getBarcodeFragment(barcodeIDs[0]), null)
                    .commit();
        }

        // Set Home menu item
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Get BarcodeFragment with bundled bardcode ID.
     */
    private Fragment getBarcodeFragment(long barcodeId) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.PARCEL_BARCODE_ID, barcodeId);
        BarcodeFragment barcodeFragment = new BarcodeFragment();
        barcodeFragment.setArguments(bundle);
        return barcodeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_barcode, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
