package sembako.sayunara.android.ui.component.home.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class Menu : Serializable {

    @Keep
    @SerializedName("id")
    @Expose
    var id : String? = ""

    @Keep
    @SerializedName("description")
    @Expose
    var description : String? = ""

    @Keep
    @SerializedName("image")
    @Expose
    var image : String? = null

    @Keep
    @SerializedName("isActive")
    @Expose
    var isActive : Boolean? = null


    @Keep
    @SerializedName("name")
    @Expose
    var name : String? = ""

    @Keep
    @SerializedName("type")
    @Expose
    var type : String? = ""

}

