package sembako.sayunara.android.ui.component.account.address

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.databinding.ActivityAddAddressBinding
import sembako.sayunara.android.service.CityService
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.component.account.address.mapaddress.MapsPickAddressActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.register.LocationGet
import sembako.sayunara.android.ui.component.product.editProduct.model.City

class AddAddressActivity : BaseActivity() , AddressView.AddAddressView {
    override val etFullName: EditText
        get() = binding.etName
    override val etPhoneNumber: EditText
        get() = binding.etPhoneNumber
    override val tvProvince: String
        get() = "Sumatera Barat"
    override val tvRegency: String
        get() = binding.tvCity.text.toString()
    override val tvDistrict: String
        get() = binding.tvDistrict.text.toString()
    override val tvSubDistrict: String
        get() = binding.tvSubDistrict.text.toString()
    override val etDetailAddress: EditText
        get() = binding.etDetailAddress

    override val getUser: User?
        get() = getUsers

    @SuppressLint("NewApi")
    override fun setColorButton(color: Int) {
        setColorTintButton(binding.btnSubmit, getColor(color))
    }

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

    private var regencyList: List<City>? = ArrayList()
    private var districtList: List<City>? = ArrayList()
    private var subDistrictList: List<City>? = ArrayList()

    var arrayRegency =  ArrayList<String>()
    var arrayDistrict = ArrayList<String>()
    var arrayVillages = ArrayList<String>()

    var idDistrict = ""
    var idRegency = ""

    private lateinit var binding: ActivityAddAddressBinding


    override fun setupViews() {

        binding.linearChooseLocation.setOnClickListener {
            val intent = Intent(activity, MapsPickAddressActivity::class.java)
            startActivityForResult(intent,1313)
        }


        binding.tvCity.setOnClickListener{
            getListCity(isShow = true, type = "regencies",id = "13")
        }


        binding.tvDistrict.setOnClickListener {
            if(binding.tvCity.text.toString().isNotEmpty()){
                getListCity(isShow = true, type = "districts",idRegency)
            }else{
                setToast("Pilih Kota/Kabupaten terlebih dahulu")
            }
        }

        binding.tvSubDistrict.setOnClickListener {
            if(binding.tvCity.text.toString().isNotEmpty()){
                getListCity(isShow = true, type = "villages",idDistrict)
            }else{
                setToast("Pilih Kecamatan terlebih dahulu")
            }
        }

        binding.btnSubmit.setOnClickListener {
            addressPresenter.checkData(false)
        }
    }

    override fun onBack() {
       // TODO("Not yet implemented")
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }

    private lateinit var addressPresenter: AdressPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        addressPresenter = AdressPresenter()
        addressPresenter.attachView(this)
        setupViews()

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
        /*if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {

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
                    .into(binding.ivMaps)

                binding.etFullAddress.setText(place)
                binding.etFullAddress.setSelection(binding.etFullAddress.text.toString().length)
                binding.linearChooseLocation.visibility = View.GONE
                binding.tvChangeAddress.visibility = View.VISIBLE
                setLocation = this.setLocation(lat, long)!!
            }

        }*/
    }

    private fun getListCity(isShow : Boolean, type : String, id : String){

        if(isShow){
            showLoadingDialog()
        }
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val retro = Retrofit.Builder()
            .baseUrl("https://asywalulfikri.github.io")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()


        val service = retro.create(CityService::class.java)
        val countryRequest = service.getListLocation(type,id)

        countryRequest?.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                if(response.isSuccessful){
                    val responseList = response.body();
                    if (responseList != null) {
                        for (i in responseList.indices) {
                            val locationName = responseList[i].getName().toString()
                            val locationNameWithFormat = setUppercaseFirstLetter(locationName)

                            when (type) {
                                Constant.Address.regency -> {
                                    arrayRegency.add(locationNameWithFormat.toString())
                                    idRegency = responseList[i].getId().toString()
                                }
                                Constant.Address.districts -> {
                                    arrayDistrict.add(locationNameWithFormat.toString())
                                    idDistrict = responseList[i].getId().toString()
                                }
                                else -> {
                                    arrayVillages.add(locationNameWithFormat.toString())
                                }
                            }
                            hideLoadingDialog()
                        }
                    }
                    if(isShow){
                        showLocationDialog(type)
                        hideLoadingDialog()
                    }
                }else{
                    hideLoadingDialog()
                    Log.d("error", response.message().toString()+".")
                }
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                Log.d("error", t.message.toString()+".")
            }

        })
    }

    private fun showLocationDialog(type: String) {

        val types = when (type) {
            Constant.Address.regency -> {
                arrayRegency.toTypedArray()
            }
            Constant.Address.districts -> {
                arrayDistrict.toTypedArray()
            }
            else -> {
                arrayVillages.toTypedArray()
            }
        }

        val builder: android.app.AlertDialog.Builder = getBuilder(this)
        builder.setTitle("Pilih Lokasi")
            .setItems(types) { _, which ->
                run {
                    when (type) {
                        Constant.Address.regency -> {
                            binding.tvCity.text = ((types[which]))
                        }
                        Constant.Address.districts -> {
                            binding.tvDistrict.text = ((types[which]))
                        }
                        else -> {
                            binding.tvSubDistrict.text = ((types[which]))
                        }
                    }

                }
                addressPresenter.validation()
            }
        builder.create().show()
    }


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}