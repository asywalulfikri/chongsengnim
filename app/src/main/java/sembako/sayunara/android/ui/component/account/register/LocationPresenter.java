package sembako.sayunara.android.ui.component.account.register;

import android.location.Location;

import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProcessType;


public class LocationPresenter {

    private SampleView sampleView;

    public LocationPresenter(SampleView view) {
        this.sampleView = view;
    }

    public void destroy() {
        sampleView = null;
    }

    public void onLocationChanged(Location location) {
        sampleView.dismissProgress();
        setLocation(location);
    }

    public void onLocationFailed(@FailType int failType) {
        sampleView.dismissProgress();

        switch (failType) {
            case FailType.TIMEOUT: {
                sampleView.setToast("Couldn't get location, and timeout!");
                break;
            }
            case FailType.PERMISSION_DENIED: {
                sampleView.setToast("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                sampleView.setToast("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE: {
                sampleView.setToast("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL: {
                sampleView.setToast("Couldn't get location, because Google Play Services connection failed!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG: {
                sampleView.setToast("Couldn't display settingsApi dialog!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED: {
                sampleView.setToast("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.VIEW_DETACHED: {
                sampleView.setToast("Couldn't get location, because in the process view was detached!");
                break;
            }
            case FailType.VIEW_NOT_REQUIRED_TYPE: {
                sampleView.setToast("Couldn't get location, "
                        + "because view wasn't sufficient enough to fulfill given configuration!");
                break;
            }
            case FailType.UNKNOWN: {
                sampleView.setToast("Ops! Something went wrong!");
                break;
            }
        }
    }

    public void onProcessTypeChanged(@ProcessType int newProcess) {
        switch (newProcess) {
            case ProcessType.GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES: {
                sampleView.updateProgress("Getting Location from Google Play Services...");
                break;
            }
            case ProcessType.GETTING_LOCATION_FROM_GPS_PROVIDER: {
                sampleView.updateProgress("Getting Location from GPS...");
                break;
            }
            case ProcessType.GETTING_LOCATION_FROM_NETWORK_PROVIDER: {
                sampleView.updateProgress("Getting Location from Network...");
                break;
            }
            case ProcessType.ASKING_PERMISSIONS:
            case ProcessType.GETTING_LOCATION_FROM_CUSTOM_PROVIDER:
                // Ignored
                break;
        }
    }

    private void setLocation(Location location) {
        sampleView.setLocation(location);
    }

    public interface SampleView {

        void setToast(String message);

        void setLocation(Location location);

        void updateProgress(String text);

        void dismissProgress();

    }

}

