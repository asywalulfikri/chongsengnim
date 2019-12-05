package sembako.sayunara.android.screen.account.register

import sembako.sayunara.android.helper.CostumEditText
import sembako.sayunara.android.helper.PasswordView
import sembako.sayunara.android.screen.base.BaseView

/**
 * Created by asywalulfikri
 * Android Developer
 * Sayunara
 */

class RegisterContract {

    interface SignUpView :BaseView {
        val mEtUserName: CostumEditText
        val mEtEmail: CostumEditText
        val mEtPassword: PasswordView
        val mEtConfirmPassword :PasswordView
        val mEtPhoneNumber: CostumEditText
        val mLatitude:String?
        val mLongitude:String?
        val mDevicesId : String?
        fun showErrorValidation(message: Int)
        fun loadingIndicator(isLoading: Boolean)
        fun setColorButton(color: Int)


    }

    interface PostActionListener {
        fun checkData()
        fun registerUser()
        fun onRequestSuccess()
        fun onRequestFailed(message: String?,error: Int)
    }
}