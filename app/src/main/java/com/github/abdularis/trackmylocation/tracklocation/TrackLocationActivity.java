package com.github.abdularis.trackmylocation.tracklocation;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abdularis.trackmylocation.App;
import com.github.abdularis.trackmylocation.R;
import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.github.abdularis.trackmylocation.data.rxfirestore.errors.DocumentNotExistsException;
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
    @BindView(R.id.btn_track)
    Button mBtnTrack;
    @BindView(R.id.text_loc_stat)
    TextView mTextLocStat;

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
        if (mViewModel.isTracking()) {
            mViewModel.stopTracking();
            return;
        }

        String devId = mTextDevId.getText().toString();
        if (devId.isEmpty()) {
            Toast.makeText(this, "Please insert valid Device Id", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Start tracking...", Toast.LENGTH_SHORT).show();
        mViewModel.getLocationUpdate(devId)
                .subscribe(this::locationUpdated, throwable -> {
                    if (throwable instanceof DocumentNotExistsException) {
                        Toast.makeText(TrackLocationActivity.this, "Data not found/Disconnected", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void locationUpdated(SharedLocation sharedLocation) {
        LatLng pos = new LatLng(sharedLocation.getLocation().latitude, sharedLocation.getLocation().longitude);
        if (mGoogleMap != null && mMyLocMarker == null) {
            MarkerOptions options = new MarkerOptions()
                    .title(sharedLocation.getDevId())
                    .position(pos);
            mMyLocMarker = mGoogleMap.addMarker(options);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
        } else if (mMyLocMarker != null) {
            mMyLocMarker.setPosition(pos);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
        }

        mTextLocStat.setText(pos.toString());
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TrackLocationViewModel.class);
        mViewModel.getTrackingState().observe(this, tracking -> {
            if (tracking != null && tracking) {
                mTextDevId.setEnabled(false);
                mBtnTrack.setBackground(getResources().getDrawable(R.drawable.bg_btn_stop));
                mBtnTrack.setText(R.string.stop);
                mTextLocStat.setText(R.string.fetching);
            } else {
                mTextDevId.setEnabled(true);
                mBtnTrack.setBackground(getResources().getDrawable(R.drawable.bg_btn_start));
                mBtnTrack.setText(R.string.start);
                mTextLocStat.setText(R.string.disconnected);
            }
        });
    }


}
