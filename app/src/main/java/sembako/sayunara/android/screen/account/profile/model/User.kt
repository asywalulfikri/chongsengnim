package sembako.sayunara.android.screen.account.profile.model

import java.io.Serializable

class User : Serializable {

    var isLogin = false
    var password: String? = null
    var userId: String? = null
    var username: String? = null
    var type: String? = null
    var avatar: String? = null
    var email: String? = null
    var isActive = false
    var phoneNumber: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var storeId: String? = null
    var isPartner = false
    var isVerified = false
}