package sembako.sayunara.android.ui.component.mobile

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_maps.*
import sembako.sayunara.android.ui.component.mobile.base.setToolbar
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.mobile.base.LocationBaseActivity
import sembako.sayunara.android.ui.component.mobile.model.CarLocation
import sembako.sayunara.android.ui.component.mobile.model.DirectionFinder
import sembako.sayunara.android.ui.component.mobile.model.DirectionFinderListener
import sembako.sayunara.android.ui.component.mobile.model.Route
import java.io.UnsupportedEncodingException


class SayunaraMobilActivity : LocationBaseActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener, DirectionFinderListener {

    private lateinit var mMap: GoogleMap
    var coordinate = LatLng(latitude, longitude)
    private var progressDialog: ProgressDialog? = null

    private var originMarkers: List<Marker> = ArrayList()
    private var destinationMarkers: List<Marker> = ArrayList()
    private var polylinePaths: List<Polyline> = ArrayList()
    private var isActiveCar : Boolean? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setToolbar(toolbars)

        initializationLocation()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    private fun sendRequest() {
        // String origin = String.valueOf(latitude)+","+String.valueOf(longitude);
        //-6.2685184,106.8793856
        var latorigin = "-6.2685184"
        var lotorigin = "106.8793856"
        val origin: String = latorigin + "," + lotorigin
        Log.e("orininya", origin)
        val destination = "$latitude,$longitude"
        Log.e("Dest", destination)
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show()
            return
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            DirectionFinder(this, origin, destination).execute()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun getCarLocation(){
        val collectionReference = FirebaseFirestore.getInstance().collection("car")
        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result!!) {
                    val carLocation = doc.toObject(CarLocation::class.java)

                    latitude = carLocation.latitude?.toDouble()!!
                    longitude = carLocation.longitude?.toDouble()!!
                    address = "Mobil Sayunara ada di "+ carLocation.address
                    isActiveCar = carLocation.isActive

                    if(isActiveCar == true){
                        btn_car.text = "Mobil Sayunara Sedang Aktif"
                    }else{
                        btn_car.text = "Mobil Sayunara Tidak Aktif"
                    }

                }
                coordinate = LatLng(latitude, longitude)
                //getDeviceLocation(this.coordinate, mMap)
                moveToLocation(this.mMap,coordinate)
                spin_kit.visibility = View.GONE

                btn_car.setOnClickListener {
                    //sendRequest()
                    //moveToLocation(this.mMap,coordinate)
                }

                mMap.addMarker(MarkerOptions()
                        .position(coordinate)
                        .title(address)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car))
                )



            } else {
                Toast.makeText(this, "Gagal mengambil lokasi", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun initializationLocation(){
        getLocationPermission()
        buildGoogleApiClient()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mDefaultLocation = mMap.cameraPosition.target
        updateLocationUI(googleMap)
        getDeviceLocation(this.mDefaultLocation, mMap)
        getCarLocation()
    }

    override fun onCameraIdle() {

        // mDefaultLocation = mMap.cameraPosition.target

    }

    override fun onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(
                this, "Please wait.",
                "Finding direction..!", true
        )

        if (originMarkers != null) {
            for (marker in originMarkers) {
                marker.remove()
            }
        }

        if (destinationMarkers != null) {
            for (marker in destinationMarkers) {
                marker.remove()
            }
        }

        if (polylinePaths != null) {
            for (polyline in polylinePaths) {
                polyline.remove()
            }
        }
    }

    override fun onDirectionFinderSuccess(routes: MutableList<Route>?) {
        progressDialog!!.dismiss()
        polylinePaths = ArrayList()
        originMarkers = ArrayList()
        destinationMarkers = ArrayList()

        for (route in routes!!) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16f))
            // (findViewById<View>(R.id.tvDuration) as TextView).text = route.duration.text
            // (findViewById<View>(R.id.tvDistance) as TextView).setText(route.distance.text)
            (originMarkers as ArrayList<Marker>).add(
                    mMap.addMarker(
                            MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car))
                                    .title(route.startAddress)
                                    .position(route.startLocation)
                    )
            )
            /*(destinationMarkers as ArrayList<Marker>).add(
                mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_b))
                        .title(route.endAddress)
                        .position(route.endLocation)
                )
            )*/
            val polylineOptions = PolylineOptions().geodesic(true).color(Color.BLUE).width(10f)
            for (i in route.points.indices) polylineOptions.add(route.points[i])
            (polylinePaths as ArrayList<Polyline>).add(mMap.addPolyline(polylineOptions))
        }
    }
}