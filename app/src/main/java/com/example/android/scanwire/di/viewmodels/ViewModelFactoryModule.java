package com.example.android.scanwire.di.viewmodels;

import androidx.lifecycle.ViewModelProvider;

import com.example.android.scanwire.viewmodels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;


/**
 * Module for dependency injection workaround in ViewModels.
 */
@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);
}
