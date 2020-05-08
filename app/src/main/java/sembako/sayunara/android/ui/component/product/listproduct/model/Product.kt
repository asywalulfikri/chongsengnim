package sembako.sayunara.android.ui.component.product.listproduct.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

@Keep
class Product : Serializable {

    @Keep
    @SerializedName("coordinate")
    @Expose
    var coordinate =  Coordinate()

    @Keep
    @SerializedName("createdAt")
    @Expose
    var createdAt =  CreatedAt()

    @Keep
    @SerializedName("description")
    @Expose
    var description : String? = ""

    @Keep
    @SerializedName("discount")
    @Expose
    var discount: Long? = null


    @Keep
    @SerializedName("id")
    @Expose
    var id : String? = ""

    @Keep
    @SerializedName("images")
    @Expose
    var images = ArrayList<String>()

    @Keep
    @SerializedName("isActive")
    @Expose
    var isActive : Boolean? = null

    @Keep
    @SerializedName("isHighLight")
    @Expose
    var isHighLight = false


    @Keep
    @SerializedName("name")
    @Expose
    var name : String? = ""


    @Keep
    @SerializedName("phoneDetail")
    @Expose
    var phoneDetail=  PhoneDetail()

    @Keep
    @SerializedName("price")
    @Expose
    var price: Long = 0

    @Keep
    @SerializedName("stock")
    @Expose
    var stock: Long = 0


    @Keep
    @SerializedName("type")
    @Expose
    var type = ArrayList<String>()


    @Keep
    @SerializedName("unit")
    @Expose
    var unit : String? = ""

    @Keep
    @SerializedName("userId")
    @Expose
    var userId : String? = ""

    @Keep
    @SerializedName("weight")
    @Expose
    var weight: Double? = null



    @Keep
    class Coordinate : Serializable {
        @Keep
        @SerializedName("latitude")
        @Expose
        var latitude : String? =null

        @Keep
        @SerializedName("longitude")
        @Expose
        var longitude : String? =null


    }

    @Keep
    class CreatedAt : Serializable {

        @Keep
        @SerializedName("iso")
        @Expose
        var iso : String? =null


        @Keep
        @SerializedName("timestamp")
        @Expose
        var timestamp : Long? =null

    }


    @Keep
    class UpdatedAt : Serializable {

        @Keep
        @SerializedName("iso")
        @Expose
        var iso : String? =null


        @Keep
        @SerializedName("timestamp")
        @Expose
        var timestamp : Long? =null

    }


    @Keep
    class PhoneDetail : Serializable {
        @Keep
        @SerializedName("appVersion")
        @Expose
        var appVersion : String? = null

        @Keep
        @SerializedName("androidVersion")
        @Expose
        var androidVersion : String? = null

        @Keep
        @SerializedName("phoneName")
        @Expose
        var phoneName : String? = null


    }

}

