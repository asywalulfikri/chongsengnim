package sembako.sayunara.android.ui.component.account.address


import android.text.Editable
import android.view.View
import android.widget.EditText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_address.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.MultiTextWatcher
import sembako.sayunara.android.helper.OnTextWatcher
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.register.LocationGet

class AddressPresenter(private val viewAdd: AddAddressView, private val addressServices: AddressServices): OnTextWatcher {
    override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editText: EditText?, editable: Editable?) {
        validation()
    }

    var messageError = 0


    fun validation(): Boolean {
        val i: Boolean

        if (viewAdd.location.text.toString().trim().isNotEmpty() && viewAdd.placeName.text.toString().trim().isNotEmpty() && viewAdd.latLong) {
            i = true
            viewAdd.setColorButton(R.color.colorDisable)

        } else {
            viewAdd.setColorButton(R.color.color_btn_disable)
            i = false
            when {
                viewAdd.location.text.toString().trim().isEmpty() -> messageError = (R.string.text_location_null)
                !viewAdd.latLong -> messageError = (R.string.text_location_null)
            }
        }

        return i

    }

    fun isEdit(context: AddAddressActivity, user : User, position : Int){
        val address = user.address[position]

        val url   = "https://maps.googleapis.com/maps/api/staticmap?center="+address.latitude+","+address.longitude+"&zoom=17&size=1000x200&key="+ Constant.Key.GoogleApiKey
        Picasso.get()
                .load(url)
                .into(context.iv_maps)


        context.etFullAddress.setText(address.fullAddress)
        context.etPhoneNumber.setText(address.phoneNumber)
        address.name?.length?.let { context.etName.setSelection(it) }
        context.maps = true
        context.linear_choose_location.visibility = View.GONE
        context.tv_change_address.visibility = View.VISIBLE
        //context.setLocation = address.latitude?.toDouble()?.let { address.longitude?.toDouble()?.let { it1 -> context.getLocation(it, it1) } }!!
        validation()
    }

    fun showErrorValidation(){
        viewAdd.showErrorValidation(messageError)
    }

    fun addAddress(isEdit : Boolean, locationGet: LocationGet, id : String) {
        if(isEdit){
            addressServices.editAddress(viewAdd,locationGet,id)
        }else{
            addressServices.addAddress(viewAdd,locationGet)
        }
    }

    fun initViews(){
        viewAdd.setupViews()
        checkEditText()
    }

    private fun checkEditText(){
        setTextWatcher(viewAdd.location)
        setTextWatcher(viewAdd.placeName)
    }


    private fun setTextWatcher(editText: EditText){

        MultiTextWatcher()
                .setEditText(editText)
                .setOnTextWatcher(this)
    }

}