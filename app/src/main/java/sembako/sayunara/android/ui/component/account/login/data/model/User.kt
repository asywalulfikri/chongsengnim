package sembako.sayunara.android.ui.component.account.login.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sembako.sayunara.android.constant.Constant
import java.io.Serializable

@Keep
class User : Serializable {

    @Keep
    @SerializedName(Constant.UserKey.locations)
    @Expose
    var locations =  Locations()

    @Keep
    @SerializedName(Constant.UserKey.createdAt)
    @Expose
    var createdAt =  CreatedAt()

    @Keep
    @SerializedName(Constant.UserKey.updatedAt)
    @Expose
    var updatedAt =  UpdatedAt()

    @Keep
    @SerializedName(Constant.UserKey.devices)
    @Expose
    var devices =  Devices()

    @Keep
    @SerializedName(Constant.UserKey.address)
    @Expose
    var address =  ArrayList<Address>()

    @Keep
    @SerializedName(Constant.UserKey.profile)
    @Expose
    var profile =  Profile()
    var isLogin = false


    @Keep
    class Devices : Serializable {
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

        @Keep
        @SerializedName(Constant.UserKey.devicesId)
        @Expose
        var devicesId : String? = null

        @Keep
        @SerializedName(Constant.UserKey.detailDevices)
        @Expose
        var detailDevices : String? = null

    }


    @Keep
    class Profile : Serializable {

        @Keep
        @SerializedName(Constant.UserKey.avatar)
        @Expose
        var avatar : String? = null

        @Keep
        @SerializedName(Constant.UserKey.email)
        @Expose
        var email : String? =null

        @Keep
        @SerializedName(Constant.UserKey.active)
        @Expose
        var active : Boolean? = null

        @Keep
        @SerializedName(Constant.UserKey.partner)
        @Expose
        var partner : Boolean? =null


        @Keep
        @SerializedName(Constant.UserKey.suspend)
        @Expose
        var suspend : Boolean? = null

        @Keep
        @SerializedName(Constant.UserKey.verified)
        @Expose
        var verified : Boolean? =null


        @Keep
        @SerializedName(Constant.UserKey.marketLocation)
        @Expose
        var marketLocation : String? =null


        @Keep
        @SerializedName(Constant.UserKey.firebaseToken)
        @Expose
        var firebaseToken : String? =null


        @Keep
        @SerializedName(Constant.UserKey.phoneNumber)
        @Expose
        var phoneNumber : String? = null


        @Keep
        @SerializedName(Constant.UserKey.storeId)
        @Expose
        var storeId : String? =null


        @Keep
        @SerializedName(Constant.UserKey.type)
        @Expose
        var type : String? =null


        @Keep
        @SerializedName(Constant.UserKey.userId)
        @Expose
        var userId : String? =null


        @Keep
        @SerializedName(Constant.UserKey.username)
        @Expose
        var username : String? = null



    }



    @Keep
    class CreatedAt : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.iso)
        @Expose
        var iso : String? =null


        @Keep
        @SerializedName(Constant.UserKey.timestamp)
        @Expose
        var timestamp : Long? = null

    }

    @Keep
    class Address : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.name)
        @Expose
        var name : String? =null

        @Keep
        @SerializedName(Constant.UserKey.latitude)
        @Expose
        var latitude : String? = null

        @Keep
        @SerializedName(Constant.UserKey.longitude)
        @Expose
        var longitude : String? = null

        @Keep
        @SerializedName(Constant.UserKey.fullAddress)
        @Expose
        var fullAddress: String? = null

        @Keep
        @SerializedName(Constant.UserKey.addressName)
        @Expose
        var addressName: String? = null


        @Keep
        @SerializedName(Constant.UserKey.phoneNumber)
        @Expose
        var phoneNumber: String? = null

    }



    @Keep
    class UpdatedAt : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.iso)
        @Expose
        var iso : String? =null


        @Keep
        @SerializedName(Constant.UserKey.timestamp)
        @Expose
        var timestamp : Long? = null

    }



    @Keep
    class Locations : Serializable {
        @Keep
        @SerializedName(Constant.UserKey.latitude)
        @Expose
        var latitude : String? =null


        @Keep
        @SerializedName(Constant.UserKey.longitude)
        @Expose
        var longitude : String? = null

        @Keep
        @SerializedName(Constant.UserKey.province)
        @Expose
        var province : String? = null


        @Keep
        @SerializedName(Constant.UserKey.city)
        @Expose
        var city : String? = null

        @Keep
        @SerializedName(Constant.UserKey.subDistrict)
        @Expose
        var subDistrict : String? = null


        @Keep
        @SerializedName(Constant.UserKey.rw)
        @Expose
        var rw : String? = null

        @Keep
        @SerializedName(Constant.UserKey.rt)
        @Expose
        var rt : String? = null

        @Keep
        @SerializedName(Constant.UserKey.postalCode)
        @Expose
        var postalCode : String? = null


    }

}


