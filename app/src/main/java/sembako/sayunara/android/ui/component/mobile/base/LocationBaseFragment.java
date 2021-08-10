package sembako.sayunara.android.ui.component.mobile.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.base.BaseFragment;


@SuppressLint("Registered")
public class LocationBaseFragment extends BaseFragment {
    public BottomSheetBehavior<ConstraintLayout> sheet;
    public GoogleApiClient mGoogleApiClient;
    protected Location mLastKnownLocation;
    protected CameraPosition mCameraPosition;
    public LatLng mDefaultLocation;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1010;
    public String placeId;
    public double lat, lng;
    public String sPhone, sName,sNote,type,sPlace;
    Intent intent;
    public String fullLocation;
    public double latitude;
    public double longitude;
    public String address;

    public boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }
    }

    public void moveToLocation(GoogleMap googleMap, LatLng latLng) {
        googleMap.clear();
       /* googleMap.addMarker(new MarkerOptions().position(latLng)
                .title("Lokasi Jemput"));*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

    }


    public void getDeviceLocation(LatLng mDefaultLocation, GoogleMap googleMap) {
        ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), 16));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 16));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    public void getDeviceLocation2(GoogleMap googleMap, final  double lat, final double lng) {
        ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lat, lng), 16));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 16));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void updateLocationUI(GoogleMap googleMap) {
        if (googleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        initMap();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addApi(LocationServices.API)
                .build();

    }

}
