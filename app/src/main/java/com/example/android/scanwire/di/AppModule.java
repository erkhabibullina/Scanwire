package com.example.android.scanwire.di;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.example.android.scanwire.data.ApplicationDatabase;
import com.example.android.scanwire.data.dao.BarcodeDao;
import com.example.android.scanwire.repositories.BarcodeRepository;
import com.example.android.scanwire.utils.AppExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application level module.
 */
@Module
public class AppModule {

    /**
     * Application context.
     */
    @Provides
    @Singleton
    public static Context getApplicationContext(Application application) {
        return application.getApplicationContext();
    }

    /**
     * AppExecutor.
     */
    @Provides
    @Singleton
    public static AppExecutor provideAppExecutor() {
        return new AppExecutor(
                new ScheduledThreadPoolExecutor(1),
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                new AppExecutor.MainThreadExecutor());
    }

    /**
     * Room Db instance.
     */
    @Provides
    @Singleton
    public static ApplicationDatabase provideAppDb(Application application) {
        return Room.databaseBuilder(
                application.getApplicationContext(),
                ApplicationDatabase.class,
                ApplicationDatabase.DB_NAME)
                .build();
    }

    /**
     * Barcode DAO.
     */
    @Provides
    @Singleton
    public static BarcodeDao provideBarcodeDao(ApplicationDatabase applicationDatabase) {
        return applicationDatabase.barcodeDao();
    }

    /**
     * Barcode Repository.
     */
    @Provides
    @Singleton
    public static BarcodeRepository provideBarcodeRepository(BarcodeDao barcodeDao,
                                                             AppExecutor appExecutor,
                                                             Context context) {
        return new BarcodeRepository(barcodeDao, appExecutor, context);
    }
}
