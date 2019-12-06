package sembako.sayunara.android.screen.account.register

import sembako.sayunara.android.helper.CostumeEditText
import sembako.sayunara.android.helper.PasswordView
import sembako.sayunara.android.screen.base.BaseView

/**
 * Created by Asywalul Fikri
 * Android Developer
 *
 */

class RegisterContract {

    interface SignUpView :BaseView {
        val mEtUserName: CostumeEditText
        val mEtEmail: CostumeEditText
        val mEtPassword: PasswordView
        val mEtConfirmPassword :PasswordView
        val mEtPhoneNumber: CostumeEditText
        val mLatitude:String?
        val mLongitude:String?
        val mDevicesId : String?
        fun showErrorValidation(message: Int)
        fun showProgress()
        fun hideProgress()
        fun setColorButton(color: Int)


    }

    interface PostActionListener {
        fun checkData()
        fun registerUser()
        fun onRequestSuccess()
        fun onRequestFailed(message: String?,error: Int)
    }
}