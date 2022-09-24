package sembako.sayunara.android.ui.component.account.address

import android.widget.EditText
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
        fun setColorButton(color: Int)
        fun showErrorValidation(message: Int)
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(code: Int?)
        fun setupViews()
        fun onBack()
    }

    interface PostActionListener {
        fun checkData(isEdit: Boolean)
        fun post(isEdit: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(error: Int)
    }

}