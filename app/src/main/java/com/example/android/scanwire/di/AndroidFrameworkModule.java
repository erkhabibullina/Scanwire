package com.example.android.scanwire.di;

import com.example.android.scanwire.di.viewmodels.BarcodeViewModelsModel;
import com.example.android.scanwire.di.viewmodels.GenerateViewModelsModel;
import com.example.android.scanwire.di.viewmodels.HistoryViewModelsModel;
import com.example.android.scanwire.di.viewmodels.ScanViewModelsModel;
import com.example.android.scanwire.ui.BarcodeFragment;
import com.example.android.scanwire.ui.GenerateFragment;
import com.example.android.scanwire.ui.HistoryFragment;
import com.example.android.scanwire.ui.ScanFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Module for injection dependencies into Android Framework clients.
 */
@Module
public abstract class AndroidFrameworkModule {

    @ContributesAndroidInjector(
            modules = {ScanViewModelsModel.class}
    )
    abstract ScanFragment contributeScanFragment();

    @ContributesAndroidInjector(
            modules = {HistoryViewModelsModel.class}
    )
    abstract HistoryFragment contributeHistoryFragment();

    @ContributesAndroidInjector(
            modules = {BarcodeViewModelsModel.class}
    )
    abstract BarcodeFragment contributeBarcodeFragment();

    @ContributesAndroidInjector(
            modules = {GenerateViewModelsModel.class}
    )
    abstract GenerateFragment contributeGenerateFragment();
}
