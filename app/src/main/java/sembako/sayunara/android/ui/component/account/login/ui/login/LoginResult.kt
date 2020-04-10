package sembako.sayunara.android.ui.component.account.login.ui.login

import sembako.sayunara.android.ui.component.account.login.ui.login.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
)
