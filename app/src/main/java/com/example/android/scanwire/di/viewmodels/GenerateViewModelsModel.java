package com.example.android.scanwire.di.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.android.scanwire.viewmodels.GenerateViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class GenerateViewModelsModel {

    @Binds
    @IntoMap
    @ViewModelKey(GenerateViewModel.class)
    public abstract ViewModel bindGenerateFragmentViewModel(GenerateViewModel viewModel);
}
