package sembako.sayunara.android.ui.component.basket.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class Basket:Serializable {

    @Keep
    @SerializedName("id")
    @Expose
    var id: String? = ""


    @Keep
    @SerializedName("userId")
    @Expose
    var userId: String? = ""


    @Keep
    @SerializedName("productId")
    @Expose
    var productId: String? = ""


    @Keep
    @SerializedName("quantity")
    @Expose
    var quantity: Double? = null


}
