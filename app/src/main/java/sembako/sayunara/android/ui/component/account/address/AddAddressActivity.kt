package eightvillages.screens.order.address.addAddress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_address.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.ConnectionActivity
import sembako.sayunara.android.ui.component.account.address.AddAddressView
import sembako.sayunara.android.ui.component.account.address.AddressPresenter
import sembako.sayunara.android.ui.component.account.address.AddressServices
import sembako.sayunara.android.ui.component.account.address.mapaddress.MapsPickAddressActivity
import sembako.sayunara.android.ui.component.account.register.LocationGet

class AddAddressActivity : ConnectionActivity() , AddAddressView {
    override val mPhoneNumber: String
        get() = etPhoneNumber.text.toString().trim()
    override val mReceiver: String
        get() = etName.text.toString().trim()
    override val latLong: Boolean
        get() = maps

    @SuppressLint("NewApi")
    override fun setColorButton(color: Int) {
        setColorTintButton(btnSubmit, getColor(color))
    }

    override val location: EditText
        get() = etFullAddress
    override val placeID: String?
        get() = placeId
    override val placeName: EditText
        get() = etAddressName

    override fun showErrorValidation(message: Int) {
        setToast(getString(message))
    }

    override fun loadingIndicator(isLoading: Boolean) {
        setDialog(isLoading)
    }

    override fun onRequestSuccess() {
        load = true
        onBackPressed()

    }

    override fun onRequestFailed(code: Int?) {
       setToast(getString(R.string.text_add_data_failed))
    }

    private var place : String? = null
    private var placeId : String? = null
    private var load : Boolean = false
    private var edit : Boolean = false
    var maps : Boolean = false
    private var id : String = "1"
    lateinit var setLocation: LocationGet

    override fun setupViews() {

        linear_choose_location.setOnClickListener {
            val intent = Intent(activity, MapsPickAddressActivity::class.java)
            startActivityForResult(intent,1313)
        }

        btnSubmit.setOnClickListener {
            if(addressPresenter.validation()){
                addressPresenter.addAddress(edit,setLocation,id)
            }
        }
    }

    private lateinit var addressPresenter: AddressPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        addressPresenter = AddressPresenter(this, AddressServices())
        addressPresenter.initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
           onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {

            val choose = data?.getBooleanExtra("choose",false)
            if(choose!!){
                val lat = data.getDoubleExtra("latitude",0.0)
                val long = data.getDoubleExtra("longitude",0.0)
                placeId = data.getStringExtra("placeId")
                place = data.getStringExtra("location")
                maps = true

                val url   = "https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+long+"&zoom=17&size=1000x200&key="+ Constant.Key.GoogleApiKey
                Picasso.get()
                        .load(url)
                        .into(iv_maps)

                etFullAddress.setText(place)
                etFullAddress.setSelection(etFullAddress.text.toString().length)
                linear_choose_location.visibility = View.GONE
                tv_change_address.visibility = View.VISIBLE
                setLocation = this!!.setLocation(lat, long)!!
            }

        }
    }


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}