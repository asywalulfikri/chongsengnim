package sembako.sayunara.home.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Banner:Serializable {

    @SerializedName("bannerId")
    @Expose
    var bannerId: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null

}
