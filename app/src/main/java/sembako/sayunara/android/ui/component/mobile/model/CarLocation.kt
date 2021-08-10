package sembako.sayunara.android.ui.component.mobile.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

@Keep
class CarLocation : Serializable {

    @Keep
    @SerializedName("address")
    @Expose
    var address: String? = null


    @Keep
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null


    @Keep
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @Keep
    @SerializedName("userId")
    @Expose
    var userId: String? = null
}
