package sembako.sayunara.android.ui.component.account.address

import android.annotation.SuppressLint
import android.text.Editable
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.OnTextWatcher
import sembako.sayunara.android.ui.base.BasePresenter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AddressPresenter : BasePresenter<AddressView.AddAddressView>(),
    AddressView.PostActionListener,OnTextWatcher{

    private var messageError = 0

    override fun checkData(isEdit : Boolean) {
        if(validation()){
            post(isEdit)
        }else{
            view?.showErrorValidation(messageError)
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun post(isEdit: Boolean) {
        view?.loadingIndicator(true)
        val uuid = UUID.randomUUID().toString()
        val mFireBaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val tsLong = System.currentTimeMillis() / 1000

        val tz = TimeZone.getTimeZone("GMT+7")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        df.timeZone = tz
        val nowAsISO = df.format(Date())

        val userId = view?.getUser?.profile?.userId.toString()
        var id: String


        val obj1: MutableMap<String?, Any?> = HashMap()
        if(view?.isChecked==true){

            if(view?.idAddress==""){
                id = uuid
                obj1[Constant.UserKey.firstAddress] = uuid

            }else{
                id = view?.idAddress.toString()
                obj1[Constant.UserKey.firstAddress] = id
            }

        }
        val obj: MutableMap<String?, Any?> = HashMap()


        if(view?.idAddress==""){
            id = uuid
            obj[Constant.UserKey.id] = uuid

        }else{
            id = view?.idAddress.toString()
            obj[Constant.UserKey.id] = id
        }



        val contact: MutableMap<String, Any> = HashMap()
        contact[Constant.UserKey.userId] = view?.getUser?.profile?.userId.toString()
        contact[Constant.UserKey.name] = view?.etFullName?.text.toString()
        contact[Constant.UserKey.phoneNumber] = view?.etPhoneNumber?.text.toString()
        obj[Constant.UserKey.contact] = contact

        val address: MutableMap<String, Any> = HashMap()
        address[Constant.UserKey.province] = view?.tvProvince.toString()
        address[Constant.UserKey.district] = view?.tvRegency.toString()
        address[Constant.UserKey.subDistrict] = view?.tvDistrict.toString()
        address[Constant.UserKey.villages] = view?.tvSubDistrict.toString()
        address[Constant.UserKey.fullAddress] = view?.etDetailAddress?.text.toString()
        address["tag"] = view?.tags.toString()
        obj[Constant.UserKey.address] = address


        val dateUpdatedData: MutableMap<String, Any> = HashMap()
        dateUpdatedData[Constant.UserKey.iso] = nowAsISO
        dateUpdatedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateUpdatedData




        if(view?.idAddress==""){
            id = uuid
            val dateCreatedData: MutableMap<String, Any> = HashMap()
            dateCreatedData[Constant.UserKey.iso] = nowAsISO
            dateCreatedData[Constant.UserKey.timestamp] = tsLong
            obj[Constant.UserKey.createdAt] = dateCreatedData

        }else{
            id = view?.idAddress.toString()
            val dateCreatedData: MutableMap<String, Any> = HashMap()
            dateCreatedData[Constant.UserKey.iso] = view?.addressEdit?.createdAt?.iso.toString()
            dateCreatedData[Constant.UserKey.timestamp] = view?.addressEdit?.createdAt?.timestamp!!.toLong()
            obj[Constant.UserKey.createdAt] = dateCreatedData
        }


        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_ADDRESS).document(userId)
            .set(obj1)
            .addOnSuccessListener {
                mFireBaseFireStore.collection(Constant.Collection.COLLECTION_ADDRESS).document(userId).collection(Constant.Collection.COLLECTION_ADDRESS_LIST).document(id)
                    .set(obj)
                    .addOnSuccessListener {
                        view?.loadingIndicator(false)
                        onRequestSuccess()
                    }
                    .addOnFailureListener { e ->
                        view?.loadingIndicator(false)
                        onRequestFailed(e.hashCode())
                    }
            }
            .addOnFailureListener { e ->
                view?.loadingIndicator(false)
                onRequestFailed(e.hashCode())
            }

    }



    private fun validation(): Boolean {

        val i: Boolean

        if (view?.etFullName?.text.toString().trim().isNotEmpty() &&
            view?.etPhoneNumber?.text.toString().trim().isNotEmpty() &&
            view?.tvRegency?.isNotEmpty() == true &&
            view?.tvDistrict?.isNotEmpty() == true &&
            view?.tvSubDistrict?.isNotEmpty() == true &&
            view?.etDetailAddress?.text.toString().isNotEmpty()
        ) {
            i = true
            view?.setColorButton(R.color.color_btn_disable)

        } else {
            view?.setColorButton(R.color.colorDisable)
            i = false
            when {
                view?.etFullName?.text.toString().trim().isEmpty() -> messageError = (R.string.text_user_name_empty)
                view?.etPhoneNumber?.text.toString().trim().isEmpty() -> messageError = (R.string.text_phone_number_empty)
                view?.tvRegency?.isEmpty() == true -> messageError = (R.string.text_location_null)
                view?.tvDistrict?.isEmpty() == true -> messageError = (R.string.text_location_null)
                view?.tvSubDistrict?.isEmpty() == true -> messageError = (R.string.text_location_null)
                view?.etDetailAddress?.text?.isEmpty()==true -> messageError = (R.string.text_location_null)
            }
        }

        return i

    }




    override fun onRequestSuccess() {
        view?.onBack()
    }

    override fun onRequestFailed(error: Int) {

    }

    override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(editText: EditText?, editable: Editable?) {

    }

}