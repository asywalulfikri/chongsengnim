package sembako.sayunara.android.ui.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service {

    private LocationManager locationManager;
    private Location location;
    private Location oldLocation;

    private double latitude;
    private double longitude;
    private float accuracy;

    private Context context;
    private static final String TAG = "GPSTracker8";

    private String provider;
    private int valueAccuracy;
    private boolean canGetLocation;

    public GPSTracker(Context context) {
        this.context = context;
        this.valueAccuracy = ValueConfig.ACCURACY;
        this.provider = getProviderName();
        //getting location when Constructor created.
        getLocation();
    }

    public GPSTracker(Context context, int valueAccuracy) {
        this.context = context;
        this.valueAccuracy = valueAccuracy;
        this.provider = getProviderName();

        //getting location when Constructor created.
        getLocation();
    }

    public GPSTracker(Context context, int valueAccuracy, String provider) {
        this.context = context;
        this.valueAccuracy = valueAccuracy;
        this.provider = provider;

        //getting location when Constructor created.
        getLocation();
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    public Location getLocation() {
//        provider = getProviderName();
        //set provider
        setProvider(provider);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        boolean statusProvider = locationManager.isProviderEnabled(provider);
        if (locationManager != null) {
            if (statusProvider) {
                //set can get location
                setCanGetLocation(true);
                locationManager.requestLocationUpdates(provider, ValueConfig.MIN_TIME_BW_UPDATES, ValueConfig.MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(getProviderName());
                    if (location != null) {
                        setLatitude(location.getLatitude());
                        setLongitude(location.getLongitude());
                        setAccuracy(location.getAccuracy());
                    }
                }
            } else {
                Shorcut.turnGPSOn(context);
            }
        } else {
            Shorcut.turnGPSOn(context);
        }

        return location;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, provider + "|" + location.getAccuracy() + "|" + location.getLatitude() + "|" + location.getLongitude());
            //filtering location
            if (location.getAccuracy() <= valueAccuracy) {
                filteringNewLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "provider: " + provider + ", status: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "provider enable: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "provider disable: " + provider);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getProviderName() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    private void filteringNewLocation(Location location) {
        if (isBetterLocation(getOldLocation(), location)) {
            Log.e(TAG, "Get Better Location");
            setItemLocation(location);
        }
    }

    private void setItemLocation(Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        setAccuracy(location.getAccuracy());
        setOldLocation(location);
    }


    /**
     * Time difference threshold set for 30 seconds.
     */
    static final int TIME_DIFFERENCE_THRESHOLD = (1 / 2 * 60) * 1000; //

    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best location strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (!isMoreAccurate) {
            int limit = (valueAccuracy * 3 / 4);
            if (newLocation.getAccuracy() <= limit) {
                return true;
            }
        }
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            // If time difference is not greater then allowed threshold we accept it.
            return timeDifference > -TIME_DIFFERENCE_THRESHOLD;
        }

        return false;
    }


    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            Log.i(TAG, "STOP Listener");
        }
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public void setCanGetLocation(boolean canGetLocation) {
        this.canGetLocation = canGetLocation;
    }

    public Location getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(Location oldLocation) {
        this.oldLocation = oldLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
