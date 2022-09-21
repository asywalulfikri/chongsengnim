package sembako.sayunara.android.ui.component.product.listproduct.model

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.ArrayList

@Keep
@IgnoreExtraProperties
class Product : Serializable {

    var coordinate : Coordinate? =null
    var createdAt :  CreatedAt? =null
    var id : String? = null
    var devices: PhoneDetail? =null
    var userId : String? = null
    var status : Status? = null
    var detail: Detail? = Detail()


    @Keep
    @IgnoreExtraProperties
    class Status : Serializable {
        var active : Boolean? = null
        var draft : Boolean? = null
        var highlight : Boolean? = null
        var publish : Boolean? =null
    }

    @Keep
    @IgnoreExtraProperties
    class Detail : Serializable {
        var images = ArrayList<String>()
        var discount: Long? = null
        var name : String? = null
        var price: Long? =null
        var stock: Long? = null
        var weight: Double? = null
        var description : String? = null
        var type = ArrayList<String>()
        var search = ArrayList<String>()
        var unit : String? = null
    }

    @Keep
    @IgnoreExtraProperties
    class Coordinate : Serializable {
        var latitude : String? =null
        var longitude : String? =null
    }

    @Keep
    @IgnoreExtraProperties
    class CreatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? =null
    }
    @Keep
    @IgnoreExtraProperties
    class UpdatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? =null
    }

    @Keep
    @IgnoreExtraProperties
    class PhoneDetail : Serializable {
        var appVersion : String? = null
        var androidVersion : String? = null
        var phoneName : String? = null
    }

    @Keep
    @IgnoreExtraProperties
    class Prices : Serializable {
        var marketName : String? = null
        var province : String? = null
        var price: Long = 0

    }
}

