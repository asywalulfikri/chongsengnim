package sembako.sayunara.android.ui.component.account.register

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.constants.ProcessType
import com.yayandroid.locationmanager.listener.LocationListener
import sembako.sayunara.android.ui.base.BaseActivity
import java.util.*

abstract class LocationBaseActivity : BaseActivity(), LocationListener {
    protected var locationManager: LocationManager? = null
        private set
    abstract val locationConfiguration: LocationConfiguration?
    private var locationGet = LocationGet()

    protected val location: Unit
        protected get() {
            if (locationManager != null) {
                locationManager!!.get()
            } else {
                throw IllegalStateException("locationManager is null. "
                        + "Make sure you call super.initialize before attempting to getLocation")
            }
        }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = LocationManager.Builder(applicationContext)
                .configuration(locationConfiguration!!)
                .activity(this)
                .notify(this)
                .build()
    }

    @CallSuper
    override fun onDestroy() {
        locationManager!!.onDestroy()
        super.onDestroy()
    }

    @CallSuper
    override fun onPause() {
        locationManager!!.onPause()
        super.onPause()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        locationManager!!.onResume()
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationManager!!.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onProcessTypeChanged(@ProcessType processType: Int) {
        // override if needed
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean) {
        // override if needed
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // override if needed
    }

    override fun onProviderEnabled(provider: String) {
        // override if needed
    }

    override fun onProviderDisabled(provider: String) {
        // override if needed
    }

    fun setLocation(location: Location) {
        val addresses: List<Address>?
        val geoCoder = Geocoder(this, Locale.getDefault())
        if (location.latitude != null) {
            addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                locationGet.province = addresses[0].adminArea
                locationGet.city = addresses[0].subAdminArea
                locationGet.district = addresses[0].locality
                if (addresses[0] != null) {
                    locationGet.address = addresses[0].getAddressLine(0).replace("Unnamed Road,", "").trim()
                } else {
                    locationGet.address = ""
                }
                locationGet.latitude = location.latitude.toString()
                locationGet.longitude = location.longitude.toString()
                if (addresses[0].getAddressLine(0).toString().contains("RT")) {
                    locationGet.rt = addresses[0].getAddressLine(0).substringAfter("RT.").substringBefore("/RW")
                } else {
                    locationGet.rt = "-"
                }
                if (addresses[0].getAddressLine(0).toString().contains("RW")) {
                    locationGet.rw = addresses[0].getAddressLine(0).substringAfter("RW.").substringBefore(",")
                } else {
                    locationGet.rw = "-"
                }

                locationGet.zipCode = addresses[0].postalCode
                locationGet.subDistrict = addresses[0].subLocality
            } else {
                locationGet.province = ""
            }
        } else {
            locationGet.province = ""
        }

    }

    fun getLocation() : LocationGet{
        return locationGet
    }


}