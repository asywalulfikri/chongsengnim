package sembako.sayunara.android.constant

import android.os.Build

class Constant {

    object Progress{
        const val DEFAULT_PROGRESS_TEXT = "Memuat permintaan"
    }

    interface Collection{
        companion object {
            const val COLLECTION_USER = "users"
            const val COLLECTION_BASKET = "basket"
            const val COLLECTION_PRODUCT = "product"
            const val COLLECTION_ARTICLES = "articles"

        }
    }

    interface UserKey {
        companion object {
            const val userId = "userId"
            const val username = "username"
            const val type = "type"
            const val name = "name"
            const val avatar = "avatar"
            const val email = "email"
            const val isPartner = "isPartner"
            const val storeId = "storeId"
            const val isVerified = "isVerified"
            const val isActive = "isActive"
            const val phoneNumber = "phoneNumber"
            const val marketLocation = "marketLocation"
            const val isLogin = "isLogin"
            const val profile = "profile"
            const val userTypeUser = "user"
            const val userTypeAdmin = "admin"
            const val userTypeMitra = "mitra"
            var informationDevices =  (Build.MANUFACTURER + " " + Build.MODEL + " , Version OS :" + Build.VERSION.RELEASE)
            const val id = "id"
            const val versionName = "versionName"
            const val createdAt = "createdAt"
            const val updatedAt = "updatedAt"
            const val versionCode = "versionCode"
            const val devicesId = "deviceId"
            const val versionAndroid = "versionAndroid"
            const val detailDevices = "detailDevices"
            const val devices = "devices"
            const val address = "address"
            const val debtTotal = "debtTotal"
            const val debtDate = "debtDate"
            const val applicationInfo = "applicationInfo"
            const val dateTime = "dateTime"
            const val latitude = "latitude"
            const val longitude = "longitude"
            const val fullAddress = "fullAddress"
            const val addressName = "addressName"
            const val province = "province"
            const val city = "city"
            const val subDistrict = "subDistrict"
            const val rt = "rt"
            const val rw = "rw"
            const val iso ="iso"
            const val timestamp = "timestamp"
            const val postalCode = "postalCode"
            const val coordinate = "coordinate"
            const val locations = "locations"
            const val requiredUpdate = "requiredUpdate"
            var DEFAULT_AVATAR = ""
        }
    }

    interface ProductKey{
        companion object{
            const val productName = "name"

        }
    }

    object CallbackResponse{
        const val EMAIL_ALREADY_REGISTERED = "The email address is already in use by another account."
        const val EMAIL_NOT_REGISTERED = "There is no user record corresponding to this identifier. The user may have been deleted."
        const val PASSWORD_IS_INVALID = "The password is invalid or the user does not have a password."
    }

    object Session{
        const val userSession = "users"
        const val isLogin = "isLogin"
        const val userTypeAdmin = "admin"
    }

    object Code{
        const val CODE_EDIT = 111
        const val CODE_LOAD = 123
    }

    object Key{
        const val GoogleApiKey = "AIzaSyBPQbuaV_iUmZh1b0ok-TDVI1FzzDiAuC0"
    }

    object RemoteConfig{
        const val requiredUpdate = "requiredUpdate"
        const val versionCode = "versionCode"
    }

    object IntentExtra{
        const val product = "product"
        const val isLoad = "load"
    }
}