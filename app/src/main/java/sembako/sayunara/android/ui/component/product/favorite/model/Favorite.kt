package sembako.sayunara.android.ui.component.product.favorite.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class Favorite : Serializable {


    @Keep
    @SerializedName("id")
    @Expose
    var id : String? = ""

    @Keep
    @SerializedName("isLike")
    @Expose
    var isLike : Boolean? = null

    @Keep
    @SerializedName("createdAt")
    @Expose
    var createdAt =  CreatedAt()


    @Keep
    @SerializedName("productId")
    @Expose
    var productId : String? = null

    @Keep
    @SerializedName("userId")
    @Expose
    var userId : String? = null

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

}

