package com.example.android.scanwire.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.experimental.UseExperimental;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.example.android.scanwire.databinding.FragmentScanBinding;
import com.example.android.scanwire.models.Barcode;
import com.example.android.scanwire.utils.AppExecutor;
import com.example.android.scanwire.utils.BarcodeUtil;
import com.example.android.scanwire.viewmodels.ScanViewModel;
import com.example.android.scanwire.viewmodels.ViewModelProviderFactory;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import static android.content.Context.VIBRATOR_SERVICE;

public class ScanFragment extends DaggerFragment implements ImageAnalysis.Analyzer,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = ScanFragment.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private FragmentScanBinding mLayout;
    private ScanViewModel mViewModel;
    private Camera mCamera;
    private ProcessCameraProvider mProcessCameraProvider;
    private CameraSelector mCameraSelector;
    private ImageAnalysis mImageAnalysis;
    private MenuItem mTorchItem;
    private SharedPreferences mSharedPreferences;
    private List<Barcode> mScannedBarcodes;
    private boolean mTorch;

    @Inject
    AppExecutor appExecutor;

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLayout = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false);
        mViewModel = new ViewModelProvider(this, viewModelProviderFactory).get(ScanViewModel.class);

        // Set Title
        getActivity().setTitle(R.string.action_scan);

        // Enable Toolbar MenuItem handling.
        setHasOptionsMenu(true);

        // Get SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Initialize CameraX
        mCameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        mCameraProviderFuture.addListener(() -> {
            try {
                mProcessCameraProvider = mCameraProviderFuture.get();
                if (cameraPermissionGranted(getActivity())) {
                    bindPreview(mProcessCameraProvider);
                } else {
                    requestPermissions(PERMISSIONS, CAMERA_REQUEST_CODE);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, appExecutor.mainThread());

        return mLayout.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProcessCameraProvider != null && mImageAnalysis != null) {
            mProcessCameraProvider.bindToLifecycle(this, mCameraSelector, mImageAnalysis);
        }
        mScannedBarcodes = new ArrayList<>(); // Reset
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProcessCameraProvider != null && mImageAnalysis != null) {
            mProcessCameraProvider.unbind(mImageAnalysis);
        }
    }

    /**
     * Binds the CameraX instance to Preview View.
     */
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(mLayout.previewView.getPreviewSurfaceProvider());

        mCameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        mImageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(mLayout.previewView.getWidth(), mLayout.previewView.getHeight()))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        mImageAnalysis.setAnalyzer(appExecutor.analyzerThread(), this);

        mCamera = cameraProvider.bindToLifecycle(this, mCameraSelector, preview, mImageAnalysis);

        // Observe Torch state
        observeTorch();
    }

    @Override
    @UseExperimental(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    public void analyze(@NonNull ImageProxy image) {
        if (image == null || image.getImage() == null) {
            return;
        }

        Image mediaImage = image.getImage();
        FirebaseVisionImage visionImage =
                FirebaseVisionImage.fromMediaImage(mediaImage, getFirebaseRotation(getActivity()));

        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .build();
        FirebaseVisionBarcodeDetector detector =
                FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        detector.detectInImage(visionImage)
                .addOnSuccessListener(appExecutor.detectorThread(), barcodes -> {
                    if (!barcodes.isEmpty()) {
                        for (FirebaseVisionBarcode barcode : barcodes) {
                            Barcode convertedBarcode = BarcodeUtil.getBarcode(barcode);
                            if (!mScannedBarcodes.contains(convertedBarcode)) {
                                Log.d(TAG, "New Barcode detected, adding..");
                                mScannedBarcodes.add(convertedBarcode);
                                if (mScannedBarcodes.size() == 1) { // Execute on first scan
                                    scheduleDbExecution();
                                }
                            }
                        }
                    }
                    image.close();
                });
    }

    /**
     * Schedule DB insertion with delay.
     * This should only run once when first barcode is identified, the delay then
     * adds time for easier scanning of other barcodes in the same image analysis.
     */
    private void scheduleDbExecution() {
        appExecutor.scheduled().schedule(() ->
                appExecutor.mainThread().execute(() -> {
                    feedback();
                    Barcode[] barcodeArray = new Barcode[mScannedBarcodes.size()];
                    mScannedBarcodes.toArray(barcodeArray);
                    long[] barcodeIds = mViewModel.insertBarcodes(barcodeArray);
                    Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                    intent.putExtra(Constants.PARCEL_BARCODE_IDS, barcodeIds);
                    startActivity(intent);
                }), 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Sends beep and vibration feedback.
     */
    private void feedback() {
        if (mSharedPreferences.getBoolean(getString(R.string.beep_key),
                getResources().getBoolean(R.bool.default_beep))) {
            ToneGenerator toneGenerator =
                    new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        }
        if (mSharedPreferences.getBoolean(getString(R.string.vibration_key),
                getResources().getBoolean(R.bool.default_vibration))) {
            try {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(350);
            } catch (Exception e) {
                Log.d(TAG, "Couldn't initialize vibration.", e);
            }
        }
    }

    /**
     * Convert real rotational degrees to correct Firebase parameter.
     */
    private int getFirebaseRotation(Context context) {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case Surface.ROTATION_90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case Surface.ROTATION_180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case Surface.ROTATION_270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Invalid rotation value."
                );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (cameraPermissionGranted(getActivity())) { // Permission granted
                mLayout.cameraRequired.setVisibility(View.GONE);
                try {
                    ProcessCameraProvider cameraProvider = mCameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else { // Permission denied
                mLayout.cameraRequired.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Check for Camera permission.
     */
    private boolean cameraPermissionGranted(Context context) {
        boolean cameraPermission = ContextCompat.checkSelfPermission(context, PERMISSIONS[0])
                == PackageManager.PERMISSION_GRANTED;
        if (cameraPermission) {
            Log.d(TAG, "Camera Permission: Granted");
        } else {
            Log.d(TAG, "Camera Permission: Denied");
        }
        return cameraPermission;
    }

    /**
     * Observe on/off status of the Camera Torch (Flashlight).
     */
    private void observeTorch() {
        if (mCamera.getCameraInfo().hasFlashUnit()) {
            mCamera.getCameraInfo().getTorchState().observe(getViewLifecycleOwner(), integer -> {
                if (integer == TorchState.ON) {
                    mTorch = true;
                    if (mTorchItem != null) {
                        mTorchItem.setIcon(getResources().getDrawable(R.drawable.ic_torch_on));
                    }
                } else {
                    mTorch = false;
                    if (mTorchItem != null) {
                        mTorchItem.setIcon(getResources().getDrawable(R.drawable.ic_torch_off));
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scan, menu);
        mTorchItem = menu.findItem(R.id.action_torch);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_torch:
                if (mCamera.getCameraInfo().hasFlashUnit()) {
                    if (mTorch) {
                        mCamera.getCameraControl().enableTorch(false);
                    } else {
                        mCamera.getCameraControl().enableTorch(true);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
