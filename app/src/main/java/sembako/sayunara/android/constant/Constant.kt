package sembako.sayunara.android.constant

import android.os.Build
import sembako.sayunara.constant.valueApp

class Constant {

    object Progress{
        const val DEFAULT_PROGRESS_TEXT = "Memuat permintaan"
    }

    interface Collection{
        companion object {
            const val COLLECTION_USER = "users"
            const val COLLECTION_BASKET = "basket"
            const val COLLECTION_PRODUCT = "product"
            const val COLLECTION_BANNER = "banner"
            const val COLLECTION_ARTICLES = "articles"
            const val COLLECTION_BASKET_PRODUCT_LIST = "basketProductList"
            const val COLLECTION_FAVORITE = "favorite"
            const val COLLECTION_CONFIG = "config"

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
            const val partner = "partner"
            const val storeId = "storeId"
            const val verified = "verified"
            const val suspend = "suspend"
            const val active = "active"
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
            const val firebaseToken = "firebaseToken"
            const val newFirebaseToken = "newFirebaseToken"
            const val sessionCode = "sessionCode"

            const val isSkip = "isSkip"
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
        const val ACCOUNT_DISABLED = "Maaf akun kamu telah di nonaktifkan , silakan hubungi admin untuk mengaktifkan kembali"
    }

    object Session{
        const val userSession = "users"
        const val isLogin = "isLogin"
        const val configApp = "config"
    }

    object userType{
        const val typeUser = "user"
        const val typeSeller = "seller"
        const val typeAdmin = "admin"
        const val typeSuperAdmin = "superadmin"
    }

    object Code{
        const val CODE_EDIT = 111
        const val CODE_LOAD = 123
    }


    object Key{
        const val GoogleApiKey = "AIzaSyBPQbuaV_iUmZh1b0ok-TDVI1FzzDiAuC0"
        const val ServerKeyFirebase = "key=AAAAn_CPUVE:APA91bEPh_fqJpL43QCZS0w7B44f5K3b8UIvAFuHRthkX1lliTTWhQQneUJ6lgkmedPos04jCYHsnVV2VLfaCFmwEUYOzKnsnTu6u27jyRM3T9nEtV34Od7Nv_kmRMcTuCqLPx6JoxXC"
    }

    object RemoteConfig{
        const val requiredUpdate = "requiredUpdate"
        const val versionCode = "versionCode"
    }

    object IntentExtra{
        const val product = "product"
        const val isLoad = "load"
    }

    object Topic{
        const val topicGeneral = "/topics/sayunara"
    }

    object TypeNotification{
        const val article = "article"
        const val promo = "promo"
    }

    object Url{
        const val imageEmpty = "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/image_product%2Fempty.png?alt=media&token=3a87583c-5d84-478b-b142-adde888c932a"
        const val bannerEmpty = "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/banner%2Fempty_banner.png?alt=media&token=fe4ae888-d7ce-4b87-9b9f-3fcd4d182f47"
    }

}