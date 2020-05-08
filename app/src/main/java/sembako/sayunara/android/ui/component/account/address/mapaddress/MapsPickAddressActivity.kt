package sembako.sayunara.android.ui.component.account.address.mapaddress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.synthetic.main.activity_maps_pick_address.*
import org.json.JSONException
import org.json.JSONObject
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.account.address.mapaddress.adapter.PlacesAutoCompleteAdapter
import java.io.IOException
import java.util.*

class MapsPickAddressActivity: LocationBaseActivityMaps(), VlogsView, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, PlacesAutoCompleteAdapter.ClickListener{

    override fun click(place: Place, address: String) {
        isIdle = false
        hideKeyboard()
        choose = true
        et_places!!.setText(address)
        et_places!!.setSelection(et_places!!.text.length)
        tv_result.text = address
        placeId = place.id
        showList(false)
        image_delete!!.visibility = View.VISIBLE
        image_search!!.visibility = View.GONE
        lat = Objects.requireNonNull<LatLng>(place.latLng).latitude
        lng = place.latLng!!.longitude
       // city = cekCity(lat, lng)
       // iv_location.visibility =View.GONE
        moveToLocation(map, place.latLng)
    }
    private var address : Address? = null
    private var choose = false
    var map:GoogleMap? = null
    private var mLastKnownLocation:Location? = null
    internal var mCameraPosition:CameraPosition? = null
    private  lateinit var jsonObject:JSONObject
    val centerCamera:LatLng get() = map!!.cameraPosition.target
    var isIdle = true

    //SearchLocation
    private var mAutoCompleteAdapter: PlacesAutoCompleteAdapter? = null
    private val filterTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.toString() != "") {
                mAutoCompleteAdapter!!.filter.filter(s.toString())
                if (recyclerView!!.visibility == View.GONE) {
                    showList(true)
                }
            } else {
                if (recyclerView!!.visibility == View.VISIBLE) {
                    showList(false)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState:Bundle?) {
        setStatusBarTranslucent()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_pick_address)


        if(intent.hasExtra("isEdit")){
            address = intent.getSerializableExtra("address") as Address
        }


        Places.initialize(this, getString(R.string.google_api_key))
        et_places!!.addTextChangedListener(filterTextWatcher)

        mAutoCompleteAdapter = PlacesAutoCompleteAdapter(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mAutoCompleteAdapter!!.setClickListener(this)
        recyclerView!!.adapter = mAutoCompleteAdapter
        mAutoCompleteAdapter!!.notifyDataSetChanged()
        buildGoogleApiClient()



        image_delete!!.setOnClickListener {
            et_places!!.setText("")
            choose = false
            image_search!!.visibility = View.VISIBLE
            image_delete!!.visibility = View.INVISIBLE
            btn_submit!!.visibility = View.VISIBLE
        }

    }

    fun showList(status: Boolean) {
        if (status) {
            image_delete!!.visibility = View.VISIBLE
            image_search!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
        } else {
            recyclerView!!.visibility = View.GONE
        }
    }

    private fun setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("choose", false)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun onMapReady(googleMap:GoogleMap) {
        map = googleMap
        map!!.setOnCameraMoveListener(this)
        map!!.setOnCameraIdleListener(this)

        updateLocationUI(googleMap)
        if(intent.hasExtra("isEdit")){
            val location = LatLng(address?.latitude!!.toDouble(), address?.longitude!!.toDouble())
            moveToLocation(googleMap, location)
        }else{
            getDeviceLocation(mDefaultLocation,googleMap)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCameraIdle() {
        if(isIdle){
            mDefaultLocation = map!!.cameraPosition.target
            val latLng = map!!.cameraPosition.target

            val geocode = Geocoder(activity)

            try
            {
                val addressList = geocode.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val locality = addressList[0].getAddressLine(0)
                    latitude = addressList[0].latitude.toString()
                    longitude = addressList[0].longitude.toString()

                    geocode.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)


                    if (locality.isNotEmpty())
                        tv_result!!.text = locality
                    et_places.setText("")
                    /*btn_submit!!.setOnClickListener {
                        mapServices.getLocation(latitude, longitude) }*/
                }

            }
            catch (e:IOException) {
                e.printStackTrace()
            }


        }
        isIdle  = true
    }

    private fun nextPage(){
        intent = Intent()
        intent.putExtra("location", tv_result!!.text.toString().trim())
        intent.putExtra("choose", true)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        intent.putExtra("placeId", placeId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun loadingIndicator(isLoading: Boolean) {
       setDialog(isLoading)
    }

    override fun onRequestSuccess(`object`: Any, type : String) {
        try {
            val jsonObject = JSONObject(`object`.toString())
            val jsonArray = jsonObject.getJSONArray("results")
            if(jsonArray.length()>0){
                val jsonObject1 = jsonArray.getJSONObject(0)
                placeId = jsonObject1.getString("place_id")
                nextPage()
            }else{
                setToast(getString(R.string.text_location_not_available))
            }

        } catch (err: JSONException) {
        }
    }

    override fun onRequestFailed(code: Int?, type : String) {
        setToast(getString(R.string.text_location_not_available))
    }

    override fun onNetworkError(code: Int?) {
        setToast(getString(R.string.text_something_wrong))
    }

    override fun onCameraMove() {

    }

}