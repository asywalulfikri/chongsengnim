package sembako.sayunara.android.ui.component.basket.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.io.Serializable

@Keep
class ListBasket:Serializable {

    @Keep
    @SerializedName("id")
    @Expose
    var id: String? = null


    @Keep
    @SerializedName("userId")
    @Expose
    var userId: String? = null


    @Keep
    @SerializedName("basketName")
    @Expose
    var basketName: String? = null


    @Keep
    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null


    @Keep
    @SerializedName("createdAt")
    @Expose
    var createdAt = Product.CreatedAt()


    @Keep
    @SerializedName("updatedAt")
    @Expose
    var updatedAt = Product.UpdatedAt()



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
