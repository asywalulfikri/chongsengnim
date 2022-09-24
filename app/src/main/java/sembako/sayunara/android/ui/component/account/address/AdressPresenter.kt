package sembako.sayunara.android.ui.component.account.address

import android.text.Editable
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.OnTextWatcher
import sembako.sayunara.android.ui.base.BasePresenter
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AdressPresenter : BasePresenter<AddressView.AddAddressView>(),
    AddressView.PostActionListener,OnTextWatcher{

    private var messageError = 0

    override fun checkData(isEdit : Boolean) {
        if(validation()){
            post(isEdit)
        }else{
            view?.showErrorValidation(messageError)
        }

    }

    override fun post(isEdit: Boolean) {
        view?.loadingIndicator(true)
        val uuid = UUID.randomUUID().toString()
        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss")
        val currentDate = sdf.format(Date())

        //val generateUUID = "$currentDate-$uuid"
        val mFireBaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val obj: MutableMap<String?, Any?> = HashMap()
        val tsLong = System.currentTimeMillis() / 1000

        val tz = TimeZone.getTimeZone("GMT+7")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val timestamp = Timestamp(System.currentTimeMillis())
        df.timeZone = tz
        val nowAsISO = df.format(Date())


        obj[Constant.UserKey.id] = UUID.randomUUID().toString()
        obj[Constant.UserKey.userId] = view?.getUser?.profile?.userId

        obj[Constant.UserKey.name] = view?.etFullName?.text.toString()
        obj[Constant.UserKey.phoneNumber] = view?.etPhoneNumber?.text.toString()
        obj[Constant.UserKey.province] = view?.tvProvince.toString()
        obj[Constant.UserKey.district] = view?.tvRegency.toString()
        obj[Constant.UserKey.subDistrict] = view?.tvDistrict.toString()
        obj[Constant.UserKey.villages] = view?.tvSubDistrict.toString()
        obj[Constant.UserKey.fullAddress] = view?.etDetailAddress?.text.toString()


        val dateUpdatedData: MutableMap<String, Any> = HashMap()
        dateUpdatedData[Constant.UserKey.iso] = nowAsISO
        dateUpdatedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateUpdatedData


        val dateCreatedData: MutableMap<String, Any> = HashMap()
        dateCreatedData[Constant.UserKey.iso] = nowAsISO
        dateCreatedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.createdAt] = dateCreatedData

        Log.d("isinya",obj.toString()+"---")


        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_ADDRESS).document(uuid)
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



    fun validation(): Boolean {

        val i: Boolean

        if (view?.etFullName?.text.toString().trim().isNotEmpty() &&
            view?.etPhoneNumber?.text.toString().trim().isNotEmpty() &&
            view?.tvRegency?.isNotEmpty() == true &&
            view?.tvDistrict?.isNotEmpty() == true &&
            view?.tvSubDistrict?.isNotEmpty() == true
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