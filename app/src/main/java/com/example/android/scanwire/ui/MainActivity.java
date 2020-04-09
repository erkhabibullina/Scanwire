package com.example.android.scanwire.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.example.android.scanwire.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding mLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set Toolbar
        setSupportActionBar(mLayout.toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mLayout.drawerLayout, mLayout.toolbar,
                R.string.cont_nav_drawer_open,
                R.string.cont_nav_drawer_close);
        mLayout.drawerLayout.addDrawerListener(mDrawerToggle);
        mLayout.navView.setNavigationItemSelectedListener(this);


        // Load ScanFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new ScanFragment(), Constants.FRAGMENT_SCAN)
                .commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        switch (item.getItemId()) {
            case R.id.nav_scan:
                Fragment scanFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_SCAN);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, !fragments.contains(scanFragment) ? new ScanFragment() :
                                fragments.get(fragments.indexOf(scanFragment)), Constants.FRAGMENT_SCAN)
                        .commit();
                break;
            case R.id.nav_generate:
                Fragment generateFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_GENERATE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, !fragments.contains(generateFragment) ? new GenerateFragment() :
                                fragments.get(fragments.indexOf(generateFragment)), Constants.FRAGMENT_GENERATE)
                        .commit();
                break;
            case R.id.nav_history:
                Fragment historyFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_HISTORY);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, !fragments.contains(historyFragment) ? new HistoryFragment() :
                                fragments.get(fragments.indexOf(historyFragment)), Constants.FRAGMENT_HISTORY)
                        .commit();
                break;
            case R.id.nav_settings:
                Fragment settingsFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_SETTINGS);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, !fragments.contains(settingsFragment) ? new SettingsFragment() :
                                fragments.get(fragments.indexOf(settingsFragment)), Constants.FRAGMENT_SETTINGS)
                        .commit();
                break;
        }
        mLayout.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
