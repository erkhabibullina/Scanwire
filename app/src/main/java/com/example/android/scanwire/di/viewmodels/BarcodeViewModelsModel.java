package com.example.android.scanwire.di.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.android.scanwire.viewmodels.BarcodeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class BarcodeViewModelsModel {

    @Binds
    @IntoMap
    @ViewModelKey(BarcodeViewModel.class)
    public abstract ViewModel bindBarcodeFragmentViewModel(BarcodeViewModel viewModel);
}
