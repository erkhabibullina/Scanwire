package com.example.android.scanwire.di.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.android.scanwire.viewmodels.ScanViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ScanViewModelsModel {

    @Binds
    @IntoMap
    @ViewModelKey(ScanViewModel.class)
    public abstract ViewModel bindScanFragmentViewModel(ScanViewModel viewModel);
}
