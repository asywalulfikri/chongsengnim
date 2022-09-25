package sembako.sayunara.android.ui.component.account.address

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.layout_empty.*
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
import sembako.sayunara.android.ui.component.product.listproduct.model.Product


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
    override val tags: String
        get() = mTags
    override val isChecked: Boolean
        get() = binding.btnSwitch.isChecked
    override val idAddress: String
        get() = addressId
    override val addressEdit: Address?
        get() = address

    var office = false
    var house = false
    var toastMessage = "Alamat Berhasil Ditambah"

    @SuppressLint("NewApi")
    override fun setColorButton(color: Int) {
        setColorTintButton(binding.btnSubmit, getColor(color))
    }

    override fun showErrorValidation(message: Int) {
        setToast(getString(message))
    }

    override fun loadingIndicator(isLoading: Boolean) {
        if(isLoading){
            showLoadingDialog()
        }else{
            hideLoadingDialog()
        }
    }

    override fun onRequestSuccess() {
        setToast(toastMessage)
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

    val listRegencies = java.util.ArrayList<String>()
    val listDistrict = java.util.ArrayList<String>()


    var arrayRegency =  ArrayList<City>()
    var arrayDistrict = ArrayList<City>()
    var arrayVillages = ArrayList<String>()

    var idDistrict = ""
    var idRegency = ""
    var mTags = ""
    var addressId = ""

    private lateinit var binding: ActivityAddAddressBinding


    override fun setupViews() {

        binding.linearChooseLocation.setOnClickListener {
            val intent = Intent(activity, MapsPickAddressActivity::class.java)
            startActivityForResult(intent,1313)
        }


        binding.tvCity.setOnClickListener{
            if(listRegencies.isEmpty()){
                getListCity(isShow = true, type = "regencies",id = "13")
            }else{
                dialogList("regencies")
            }
        }


        binding.tvDistrict.setOnClickListener {
            if(binding.tvCity.text.toString().isNotEmpty()){
                if(listDistrict.size==0){
                    getListCity(isShow = true, type = "districts",idRegency)
                }else{
                    dialogList("districts")
                }
            }else{
                setToast("Pilih Kota/Kabupaten terlebih dahulu")
            }
        }

        binding.tvSubDistrict.setOnClickListener {
            if(binding.tvCity.text.toString().isNotEmpty()){
                if(arrayVillages.size==0){
                    getListCity(isShow = true, type = "villages",idDistrict)
                }else{
                    dialogList("villages")
                }
            }else{
                setToast("Pilih Kecamatan terlebih dahulu")
            }
        }

        binding.btnSubmit.setOnClickListener {
            addressPresenter.checkData(false)
        }

        binding.btnOffice.setOnClickListener{
            if(office){
                setColor(binding.btnOffice,R.color.bg_form)
                binding.btnOffice.setTextColor(Color.parseColor("#828282"))
                mTags = ""
            }else{
                setColor(binding.btnOffice,R.color.colorPrimary)
                setColor(binding.btnHouse,R.color.bg_form)
                binding.btnOffice.setTextColor(Color.WHITE)
                binding.btnHouse.setTextColor(Color.parseColor("#828282"))
                mTags = "Kantor"
                house = false
                office = true
            }
        }

        binding.btnHouse.setOnClickListener {
            if(house){
                setColor(binding.btnHouse,R.color.bg_form)
                binding.btnHouse.setTextColor(Color.parseColor("#828282"))
                binding.btnHouse.setTextColor(Color.parseColor("#828282"))
                mTags = ""
            }else{
                setColor(binding.btnHouse,R.color.colorPrimary)
                setColor(binding.btnOffice,R.color.bg_form)
                binding.btnHouse.setTextColor(Color.WHITE)
                binding.btnOffice.setTextColor(Color.parseColor("#828282"))
                mTags = "Rumah"
                office = false
                house = true
            }
        }
    }

    @SuppressLint("NewApi")
    fun setColor(button : Button, color : Int){
        setColorTintButton(button, getColor(color))
    }

    override fun onBack() {
        load= true
       onBackPressed()
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }

    private lateinit var addressPresenter: AddressPresenter
    private var address : Address? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        addressPresenter = AddressPresenter()
        addressPresenter.attachView(this)
        setupViews()

        if(intent.hasExtra("data")){
            address = intent.getSerializableExtra("data") as Address
            updateView()
        }

    }


    private fun updateView(){
        binding.etName.setText(address?.contact?.name.toString())
        binding.etName.setSelection(binding.etName.text.length)
        binding.etPhoneNumber.setText(address?.contact?.phoneNumber.toString())
        binding.tvCity.text = address?.address?.province.toString()
        binding.tvDistrict.text = address?.address?.subDistrict.toString()
        binding.tvSubDistrict.text = address?.address?.village.toString()
        binding.etDetailAddress.setText(address?.address?.fullAddress.toString())
        binding.btnSwitch.isChecked = address?.address?.isChecked == true
        addressId = address?.id.toString()
       // setToast(addressId)

        if(address?.address?.tag.toString()=="Rumah"){
            setColor(binding.btnHouse,R.color.colorPrimary)
            setColor(binding.btnOffice,R.color.bg_form)
            binding.btnHouse.setTextColor(Color.WHITE)
            binding.btnOffice.setTextColor(Color.parseColor("#828282"))
            mTags = "Rumah"
            office = false
            house = true
        }else if(address?.address?.tag.toString()=="Kantor"){
            setColor(binding.btnOffice,R.color.colorPrimary)
            setColor(binding.btnHouse,R.color.bg_form)
            binding.btnOffice.setTextColor(Color.WHITE)
            binding.btnHouse.setTextColor(Color.parseColor("#828282"))
            mTags = "Kantor"
            house = false
            office = true
        }

        toastMessage = "Alamat Berhasil Diperbarui"
        binding.btnDelete.visibility =View.VISIBLE
        binding.btnDelete.setOnClickListener{
            dialogDelete(address)
        }


    }

    private fun dialogDelete(address: Address?){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete_address))
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                deleteProduct(address)
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }


    private fun deleteProduct(address: Address?){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ADDRESS).document(getUser?.profile?.userId.toString()).collection(Constant.Collection.COLLECTION_ADDRESS_LIST).document(address?.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Alamat Berhasil di Hapus", Toast.LENGTH_LONG).show()
                load = true
                onBackPressed()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Gagal menghapus Alamat " + e.message, Toast.LENGTH_LONG).show()
            }
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
                            if(responseList.isNotEmpty()){
                                val locationName = responseList[i].getName().toString()
                                val locationId = responseList[i].getId().toString()
                                val locationNameWithFormat = wordCapitalize(locationName)

                                when (type) {
                                    Constant.Address.regency -> {
                                        val city = City()
                                        val name = locationName.toLowerCase()
                                        if(name.contains("kota padang")||name.contains("kota solok")||name.contains("pasaman barat")||name.contains("tanah datar")|| name.contains("padang panjang")){
                                            city.setName(locationNameWithFormat)
                                            city.setId(locationId)
                                            arrayRegency.add(city)
                                            listRegencies.add(city.getName().toString())
                                        }

                                    }
                                    Constant.Address.districts -> {
                                        val city = City()
                                        city.setName(locationNameWithFormat)
                                        city.setId(locationId)
                                        arrayDistrict.add(city)
                                        listDistrict.add(city.getName().toString())
                                    }
                                    else -> {
                                        arrayVillages.add(locationNameWithFormat.toString())
                                    }
                                }
                            }

                            hideLoadingDialog()
                        }
                    }
                    if(isShow){
                        dialogList(type)
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


    private fun dialogList(type: String) {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_list_dialog, null)
        var adapter: ArrayAdapter<*>? = null
        var value = ""
        val dialog = BottomSheetDialog(this,R.style.AlertDialogCustom)
        dialog.setContentView(view)



        val btnSubmit: MaterialButton = view.findViewById(R.id.btnSubmit)
        val listView: ListView = view.findViewById(R.id.list_view)
        val title: TextView = view.findViewById(R.id.title)
        val ivClose : ImageView = view.findViewById(R.id.ivClose)

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isDraggable = false


        title.text = getString(R.string.text_take_location)

        listView.choiceMode = AbsListView.CHOICE_MODE_SINGLE

        adapter = when (type) {
            Constant.Address.regency -> {
                ArrayAdapter<Any?>(this, R.layout.simple_list_item_single_choice1, listRegencies as List<Any?>)
            }
            Constant.Address.districts -> {
                ArrayAdapter<Any?>(this, R.layout.simple_list_item_single_choice1, listDistrict as List<Any?>)
            }
            else -> {
                ArrayAdapter<Any?>(this, R.layout.simple_list_item_single_choice1, arrayVillages as List<Any?>)
            }
        }

        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            when (type) {
                Constant.Address.regency -> {
                    val name: String = arrayRegency[i].getName().toString()
                    idRegency = arrayRegency[i].getId().toString()
                    value = name
                }
                Constant.Address.districts -> {
                    val name: String = arrayDistrict[i].getName().toString()
                    idDistrict = arrayDistrict[i].getId().toString()
                    value = name
                }
                else -> {
                    val name: String = arrayVillages[i]
                    value = name
                }
            }
            btnSubmit.isEnabled = true
        }

        btnSubmit.text = "Pilih"

        if (value == "") {
            btnSubmit.isEnabled = false
        } else {
            btnSubmit.isEnabled = true
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
        }


        btnSubmit.setOnClickListener {
            if (value == "") {
                //Toast.makeText(this, "Pilih " + type + " terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                when (type) {
                    Constant.Address.regency -> {
                        if(binding.tvCity.text.toString()!=value){
                            listDistrict.clear()
                            arrayDistrict.clear()
                            binding.tvDistrict.text = ""
                            binding.tvSubDistrict.text = ""
                        }
                        binding.tvCity.text = (value)
                    }
                    Constant.Address.districts -> {
                        if(binding.tvDistrict.text.toString()!=value){
                            binding.tvSubDistrict.text = ""
                            arrayVillages.clear()
                        }
                        binding.tvDistrict.text = (value)
                    }
                    else -> {
                        binding.tvSubDistrict.text = (value)
                    }
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}