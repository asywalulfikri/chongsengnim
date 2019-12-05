package sembako.sayunara.android.screen.account.login

import sembako.sayunara.android.screen.base.BaseView
import sembako.sayunara.android.screen.account.profile.model.User


interface LoginContract {

    interface LoginView:BaseView {
        val mEmail: String
        val mPassword: String
        fun showErrorValidation(message: Int)
        fun loadingIndicator(isLoading: Boolean)
        fun onRefresh(user: User)
    }

    interface ActionListener {
        fun checkData()
        fun loginUser()
        fun onRequestSuccess(user: User)
        fun showErrorResponse(message: String?)
        fun onRequestFailed(message: String?,error: Int)
    }
}