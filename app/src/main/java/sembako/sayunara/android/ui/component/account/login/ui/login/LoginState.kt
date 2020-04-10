package debt.note.android.ui.login.ui.login

import sembako.sayunara.android.ui.component.account.login.data.model.User

sealed class LoginState {
    object Requesting : LoginState()
    data class OnSuccess(val user: User) : LoginState()
    data class OnFailed(val t: java.lang.Exception?): LoginState()

}