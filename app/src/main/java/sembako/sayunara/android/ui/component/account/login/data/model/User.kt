package sembako.sayunara.android.ui.component.account.login.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sembako.sayunara.android.constant.Constant
import java.io.Serializable

@Keep
class User : Serializable {

    @Keep
    @SerializedName(Constant.UserKey.userId)
    @Expose
    var userId : String? =null


    @Keep
    @SerializedName(Constant.UserKey.type)
    @Expose
    var type : String? =null

    @Keep
    @SerializedName(Constant.UserKey.email)
    @Expose
    var email : String? =null

    @Keep
    @SerializedName(Constant.UserKey.username)
    @Expose
    var username : String? = null

    @Keep
    @SerializedName(Constant.UserKey.phoneNumber)
    @Expose
    var phoneNumber : String? = null

    @Keep
    @SerializedName(Constant.UserKey.avatar)
    @Expose
    var avatar : String? = null

    @Keep
    @SerializedName(Constant.UserKey.isPartner)
    @Expose
    var isPartner : Boolean = false


    @Keep
    @SerializedName(Constant.UserKey.isActive)
    @Expose
    var isActive : Boolean = false


    @Keep
    @SerializedName(Constant.UserKey.isVerified)
    @Expose
    var isVerified : Boolean = false


    @Keep
    @SerializedName(Constant.UserKey.applicationInfo)
    @Expose
    var applicationInfo =  ApplicationInfo()


    var isLogin = false



    @Keep
    class ApplicationInfo : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.versionName)
        @Expose
        var versionName : String? =null

        @Keep
        @SerializedName(Constant.UserKey.versionCode)
        @Expose
        var versionCode : Int? =null

        @Keep
        @SerializedName(Constant.UserKey.versionAndroid)
        @Expose
        var versionAndroid : String? = null

    }


    @Keep
    class DateTime : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.createdAt)
        @Expose
        var createdAt : String? =null


        @Keep
        @SerializedName(Constant.UserKey.updatedAt)
        @Expose
        var updateAt : String? = null

    }
}


