package sembako.sayunara.android.ui.component.articles

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

@Keep
class Articles:Serializable {

    @Keep
    @SerializedName("id")
    @Expose
    var id: String? = ""


    @Keep
    @SerializedName("type")
    @Expose
    var type: Int? = null


    @Keep
    @SerializedName("category")
    @Expose
    var category: String? = null

    @Keep
    @SerializedName("description")
    @Expose
    var description: String? = null

    @Keep
    @SerializedName("userId")
    @Expose
    var userId: String? = ""


    @Keep
    @SerializedName("title")
    @Expose
    var title: String? = ""


    @Keep
    @SerializedName("source")
    @Expose
    var source: String? = null


    @Keep
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @Keep
    @SerializedName("images")
    @Expose
    var images = ArrayList<String>()

}
