package sembako.sayunara.android.ui.component.account.address

import android.widget.EditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import sembako.sayunara.android.ui.base.BaseView
import sembako.sayunara.android.ui.component.account.login.data.model.User

interface AddressView {


    interface AddAddressView : BaseView  {
        val etFullName : EditText
        val etPhoneNumber : EditText
        val tvProvince : String
        val tvRegency : String
        val tvDistrict : String
        val tvSubDistrict : String
        val etDetailAddress : EditText
        val getUser : User?
        val tags : String
        val isChecked : Boolean
        val idAddress : String
        val addressEdit : Address?
        fun setColorButton(color: Int)
        fun showErrorValidation(message: Int)
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(code: Int?)
        fun setupViews()
        fun onBack()
    }

    interface ViewList {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(querySnapshot: QuerySnapshot)
        fun onRequestFailed(message: String)
    }

    interface DetailList {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestDetailSuccess(querySnapshot: DocumentSnapshot)
        fun onRequestDetailFailed(message: String)
    }

    interface PostActionListener {
        fun checkData(isEdit: Boolean)
        fun post(isEdit: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(error: Int)
    }

}