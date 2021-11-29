package sembako.sayunara.android.ui.component.account.login.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sembako.sayunara.android.constant.Constant
import java.io.Serializable

@Keep
class User : Serializable {

    var locations =  Locations()
    var createdAt =  CreatedAt()
    var updatedAt =  UpdatedAt()
    var devices =  Devices()
    var address =  ArrayList<Address>()
    var profile =  Profile()
    var isLogin = false


    @Keep
    class Devices : Serializable {
        var versionName : String? =null
        var versionCode : Int? =null
        var versionAndroid : String? = null
        var devicesId : String? = null
        var detailDevices : String? = null

    }


    @Keep
    class Profile : Serializable {
        var avatar : String? = null
        var email : String? =null
        var active : Boolean? = null
        var partner : Boolean? =null
        var suspend : Boolean? = null
        var verified : Boolean? =null
        var marketLocation : String? =null
        var firebaseToken : String? =null
        var phoneNumber : String? = null
        var storeId : String? =null
        var type : String? =null
        var userId : String? =null
        var username : String? = null
        var search = ArrayList<String>()

    }


    @Keep
    class CreatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null

    }

    @Keep
    class Address : Serializable {
        var name : String? =null
        var latitude : String? = null
        var longitude : String? = null
        var fullAddress: String? = null
        var addressName: String? = null
        var phoneNumber: String? = null

    }



    @Keep
    class UpdatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null

    }



    @Keep
    class Locations : Serializable {
        var latitude : String? =null
        var longitude : String? = null
        var province : String? = null
        var city : String? = null
        var subDistrict : String? = null
        var rw : String? = null
        var rt : String? = null
        var postalCode : String? = null


    }

}


