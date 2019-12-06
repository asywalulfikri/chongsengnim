package sembako.sayunara.android.screen.account.profile

import sembako.sayunara.android.screen.account.profile.model.User
import sembako.sayunara.android.screen.base.BaseView


class EditProfileContract {

    interface EditProfileView : BaseView {
        val mUserName: String
        val setUser : User?
        fun onRefresh(user : User)
        fun showErrorValidation(message: Int)
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
    }
}
