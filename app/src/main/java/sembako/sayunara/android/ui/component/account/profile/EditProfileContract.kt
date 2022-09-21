package sembako.sayunara.android.ui.component.account.profile

import sembako.sayunara.android.ui.base.BaseView
import sembako.sayunara.android.ui.component.account.login.data.model.User


class EditProfileContract {

    interface EditProfileView : BaseView {
        val mUserName: String
        val getUser : User?
        fun onRefresh(user : User)
        fun showErrorValidation(message: Int)
        fun showErrorMessage(message : String)
        fun showProgress()
        fun hideProgress()
        fun onRequestSuccess()
        fun onUploadAvatarSuccess(url : String)
        fun onAvatarEditSuccess(url : String)
        fun onAvatarEditFailed(code: Int)
        fun onUploadAvatarFailed()
        fun onRequestFailed(code: Int?)
        fun setupViews()


    }
    interface ActionListener {
        fun checkData()
        fun editUser()
        fun onRequestSuccess()
        fun onRequestFailed(message: String?,error: Int)
        fun onRequestFailed(message : String)
    }
}
