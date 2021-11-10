package sembako.sayunara.android.ui.component.splashcreen.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ConfigApp : Serializable {

    @Keep
    @SerializedName("appName")
    @Expose
    var appName: String? = null

    @Keep
    @SerializedName("versionCode")
    @Expose
    var versionCode: Long? = null

    @Keep
    @SerializedName("requiredUpdate")
    @Expose
    var requiredUpdate = false

    @Keep
    @SerializedName("versionName")
    @Expose
    var versionName: String? = null
}