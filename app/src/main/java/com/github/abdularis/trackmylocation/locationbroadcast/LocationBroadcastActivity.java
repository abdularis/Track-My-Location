package com.github.abdularis.trackmylocation.locationbroadcast;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abdularis.trackmylocation.App;
import com.github.abdularis.trackmylocation.R;
import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.github.abdularis.trackmylocation.data.MyLocationProvider;
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
import io.reactivex.Notification;

public class LocationBroadcastActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private Marker mMyLocMarker;
    private GoogleMap mGoogleMap;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_curr_latlng)
    TextView mTextLatLng;
    @BindView(R.id.btn_broadcast)
    Button mBtnBroadcast;

    @Inject
    ViewModelFactory mViewModelFactory;
    LocationBroadcastViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_broadcast);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Location Broadcast");
        }

        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mViewModel.getLastLocation() != null)
            updateMarker(mViewModel.getLastLocation());
    }

    public void onBroadcastBtnClick(View view) {
        mViewModel.switchBroadcast();
    }

    private void updateMarker(Location location) {
        String latlng = location.getLatitude() + "/" + location.getLongitude();
        mTextLatLng.setText(latlng);

        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        if (mGoogleMap == null) return;
        if (mMyLocMarker == null) {
            MarkerOptions options = new MarkerOptions()
                    .title("Me")
                    .position(pos);
            mMyLocMarker = mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
        } else {
            mMyLocMarker.setPosition(pos);
        }
    }

    private void locationUpdate(Notification<Location> locNotif) {
        if (locNotif.isOnNext()) {
            updateMarker(locNotif.getValue());
        } else if (locNotif.isOnError()) {
            Toast.makeText(LocationBroadcastActivity.this, "Request location failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void locationProviderConnection(Integer connCode) {
        switch (connCode) {
            case MyLocationProvider.CONNECTION_FAILED:
                Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void isBroadcastingChange(Boolean isBroadcasting) {
        if (isBroadcasting) {
            mBtnBroadcast.setBackground(getResources().getDrawable(R.drawable.bg_btn_stop_broadcast));
            mBtnBroadcast.setText("Stop Broadcasting");
        } else {
            mBtnBroadcast.setBackground(getResources().getDrawable(R.drawable.bg_btn_start_broadcast));
            mBtnBroadcast.setText("Start Broadcasting");
        }
    }

    @SuppressLint("CheckResult")
    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LocationBroadcastViewModel.class);
        mViewModel.getLocation().subscribe(this::locationUpdate);
        mViewModel.getLocationProviderConnection().subscribe(this::locationProviderConnection);
        mViewModel.isBroadcasting().observe(this, this::isBroadcastingChange);
    }


}
