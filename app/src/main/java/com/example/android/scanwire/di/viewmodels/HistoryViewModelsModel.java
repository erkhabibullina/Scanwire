package com.example.android.scanwire.di.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.android.scanwire.viewmodels.HistoryViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class HistoryViewModelsModel {

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel.class)
    public abstract ViewModel bindHistoryFragmentViewModel(HistoryViewModel viewModel);
}
