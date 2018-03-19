package com.github.abdularis.trackmylocation.sharelocation;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abdularis.trackmylocation.App;
import com.github.abdularis.trackmylocation.R;
import com.github.abdularis.trackmylocation.common.Util;
import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class ShareLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM_LEVEL = 12f;
    private static final String TAG = "ShareLocationActivity";

    private Marker mMyLocMarker;
    private GoogleMap mGoogleMap;
    private boolean mFollowMarker;
    private Disposable mLocationUpdateDisposable;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_dev_id)
    TextView mTextDevId;
    @BindView(R.id.text_curr_latlng)
    TextView mTextLatLng;
    @BindView(R.id.btn_broadcast)
    Button mBtnBroadcast;

    @Inject
    ViewModelFactory mViewModelFactory;
    ShareLocationViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.share_location);
        }

        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.checkGooglePlayServicesAvailability(this) &&
                Util.checkLocationPermission(this)) {
            subscribeToLocationUpdate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationUpdateDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopSharingLocation();
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Util.checkLocationPermissionsResult(requestCode, permissions, grantResults)) {
            subscribeToLocationUpdate();
        } else {
            Toast.makeText(this, "Please grant permission to this app",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(marker -> {
            mFollowMarker = true;
            return false;
        });
        mGoogleMap.setOnMapClickListener(latLng -> mFollowMarker = false);
        if (mViewModel.getLastCachedLocation() != null)
            onLocationUpdated(mViewModel.getLastCachedLocation());
    }

    public void onBroadcastBtnClick(View view) {
        if (mViewModel.isSharing()) {
            mViewModel.stopSharingLocation();
        } else {
            mViewModel.startSharingLocation();
        }
    }

    private void subscribeToLocationUpdate() {
        if (mLocationUpdateDisposable != null && !mLocationUpdateDisposable.isDisposed()) return;
        mLocationUpdateDisposable = mViewModel.getLocationUpdates()
                .subscribe(this::onLocationUpdated, this::onLocationUpdateError);
    }

    private void onLocationUpdated(Location location) {
        String latlng = location.getLatitude() + "/" + location.getLongitude();
        mTextLatLng.setText(latlng);

        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        if (mGoogleMap == null) return;
        if (mMyLocMarker == null) {
            MarkerOptions options = new MarkerOptions().title("Me").position(pos);
            mMyLocMarker = mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM_LEVEL));
        } else {
            mMyLocMarker.setPosition(pos);
            if (mFollowMarker) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
            } else if (!isLatLngOnVisibleRegion(pos)) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }
    }

    private boolean isLatLngOnVisibleRegion(LatLng pos) {
        // check apakah pos berada pada posisi yg terlihat dilayar
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        return bounds.contains(pos);
    }

    private void onLocationUpdateError(Throwable t) {
        if (t instanceof SecurityException) {
            // Access to coarse or fine location are not allowed by the user
            Util.checkLocationPermission(ShareLocationActivity.this);
        }
        Log.d(TAG, "LocationUpdateErr: " + t.toString());
    }

    private void sharingStateChange(Boolean isBroadcasting) {
        if (isBroadcasting) {
            mBtnBroadcast.setBackground(getResources().getDrawable(R.drawable.bg_btn_stop));
            mBtnBroadcast.setText(R.string.stop);
        } else {
            mBtnBroadcast.setBackground(getResources().getDrawable(R.drawable.bg_btn_start));
            mBtnBroadcast.setText(R.string.start);
        }
    }

    @SuppressLint("CheckResult")
    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ShareLocationViewModel.class);
        mViewModel.getSharingStateLiveData().observe(this, this::sharingStateChange);
        mViewModel.getDeviceIdObservable().subscribe(mTextDevId::setText);
    }


}
