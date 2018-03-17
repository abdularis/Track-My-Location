package com.github.abdularis.trackmylocation.tracklocation;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.abdularis.trackmylocation.App;
import com.github.abdularis.trackmylocation.R;
import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.github.abdularis.trackmylocation.model.SharedLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Marker mMyLocMarker;
    private GoogleMap mGoogleMap;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_dev_id)
    EditText mTextDevId;

    @Inject
    ViewModelFactory mViewModelFactory;
    TrackLocationViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.track_location);
        }

        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopTracking();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public void onStartTrackClick(View view) {
        String devId = mTextDevId.getText().toString();
        if (devId.isEmpty()) {
            Toast.makeText(this, "Please insert valid Device Id", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Start tracking...", Toast.LENGTH_SHORT).show();
        mViewModel.startTracking(devId);
    }

    private void locationUpdated(SharedLocation sharedLocation) {
        LatLng pos = new LatLng(sharedLocation.getLocation().latitude, sharedLocation.getLocation().longitude);
        if (mGoogleMap != null && mMyLocMarker == null) {
            MarkerOptions options = new MarkerOptions()
                    .title(sharedLocation.getDevId())
                    .position(pos);
            mMyLocMarker = mGoogleMap.addMarker(options);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
        }

        if (mMyLocMarker != null) {
            mMyLocMarker.setPosition(pos);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TrackLocationViewModel.class);
        mViewModel.getTrackedLocationUpdate().subscribe(this::locationUpdated);
    }


}
