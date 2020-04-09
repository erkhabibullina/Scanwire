package com.example.android.scanwire.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.example.android.scanwire.adapters.BarcodeAdapter;
import com.example.android.scanwire.databinding.FragmentHistoryBinding;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.DensityUtil;
import com.example.android.scanwire.utils.RecyclerViewUtil;
import com.example.android.scanwire.viewmodels.HistoryViewModel;
import com.example.android.scanwire.viewmodels.ViewModelProviderFactory;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class HistoryFragment extends DaggerFragment implements BarcodeAdapter.Callback {

    private static final String TAG = HistoryFragment.class.getSimpleName();
    private FragmentHistoryBinding mLayout;
    private HistoryViewModel mViewModel;
    private BarcodeAdapter mBarcodeAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback;

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        mViewModel = new ViewModelProvider(this, viewModelProviderFactory).get(HistoryViewModel.class);

        // Set Title
        getActivity().setTitle(R.string.action_history);

        // Enable Toolbar MenuItem handling.
        setHasOptionsMenu(true);

        // Set Adapter / LayoutManager / Decoration
        mBarcodeAdapter = new BarcodeAdapter(this, getActivity());
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(getActivity());
        mLayout.recyclerView.setAdapter(mBarcodeAdapter);
        mLayout.recyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mLayout.recyclerView);

        // Set Swipe action
        mItemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Barcode barcode = mBarcodeAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                        mViewModel.deleteBarcode(barcode);
                        Snackbar.make(mLayout.recyclerView,
                                getString(R.string.barcode_deleted),
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), (View v) -> {
                                    mViewModel.insertBarcode(barcode);

                                    // Restore selector
                                    viewHolder.itemView.setBackground(ResourcesCompat.
                                            getDrawable(getResources(), R.color.selector_listitem, null));
                                }).show();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        drawChildCanvas(getActivity(), viewHolder, c, actionState, dX);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mLayout.recyclerView);

        // Subscribe Observables
        mViewModel.setBarcodes(null);
        observeBarcodes();

        return mLayout.getRoot();
    }

    /**
     * Used to draw canvas for ItemTouchHelperCallback onChildDraw.
     */
    private void drawChildCanvas(Context context, RecyclerView.ViewHolder viewHolder,
                                 Canvas canvas, int actionState, float dX) {
        View itemView = viewHolder.itemView;
        Paint paint = new Paint();

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            paint.setTextSize(DensityUtil.convertDpToPx(context, 18));
            paint.setFakeBoldText(true);
            float y = itemView.getBottom() + DensityUtil.convertDpToPx(context, 6)
                    + ((itemView.getTop() - itemView.getBottom()) / 2f);
            float xMargin = DensityUtil.convertDpToPx(context, 24);
            if (dX < 0) { // SWIPE LEFT
                paint.setARGB(255, 255, 0, 0);
                canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                paint.setARGB(255, 255, 255, 255);
                paint.setTextAlign(Paint.Align.RIGHT);
                float x = (float) itemView.getRight() - xMargin;
                String message = getResources().getString(R.string.delete).toUpperCase();
                canvas.drawText(message, x, y, paint);
            }
        }

        itemView.setBackgroundColor(context.getResources().getColor(R.color.list_item));
        if (dX == 0) {
            itemView.setBackground(ContextCompat.getDrawable(getContext(), R.color.selector_listitem));
            //context.getResources().getDrawable(R.color.selector_listitem));
        }
    }

    @Override
    public void onBarcodeClick(int index) {
        Intent intent = new Intent(getActivity(), BarcodeActivity.class);
        intent.putExtra(Constants.PARCEL_BARCODE_IDS,
                new long[]{mBarcodeAdapter.getCurrentList().get(index).getId()});
        startActivity(intent);
    }

    /**
     * Observe Barcodes in Db.
     */
    private void observeBarcodes() {
        mViewModel.getBarcodes().observe(getViewLifecycleOwner(), barcodes -> {
            mBarcodeAdapter.submitList(barcodes);
            if (barcodes.isEmpty()) {
                mLayout.emptyInformer.setVisibility(View.VISIBLE);
            } else {
                mLayout.emptyInformer.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Refresh Observable Barcodes.
     */
    private void refreshObserver(@Nullable String query) {
        mViewModel.getBarcodes().removeObservers(getViewLifecycleOwner());
        mViewModel.setBarcodes(query);
        observeBarcodes();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);

        // Get SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set Query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void setResults(String query) {
                if (query != null && !query.isEmpty()) {
                    mLayout.emptyInformer.setText(getString(R.string.empty_search_result));
                } else {
                    mLayout.emptyInformer.setText(getString(R.string.history_is_empty));
                }
                refreshObserver(query);
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                setResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setResults(newText);
                return true;
            }
        });

        // Set Close Behaviour
        searchView.setOnCloseListener(() -> {
            mLayout.emptyInformer.setText(getString(R.string.history_is_empty));
            refreshObserver(null);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history_clear:
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mViewModel.deleteAllBarcodes();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.action_history_clear))
                        .setMessage(getString(R.string.barcode_delete_all_message))
                        .setPositiveButton(getString(R.string.accept), dialogClickListener)
                        .setNegativeButton(getString(R.string.cancel), dialogClickListener)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
