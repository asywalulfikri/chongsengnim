package sembako.sayunara.android.ui.component.splashcreen.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class ConfigApp : Serializable {

    @Keep
    @SerializedName("appId")
    @Expose
    var appId: String? = null

    @Keep
    @SerializedName("sessionCode")
    @Expose
    var cessionCode: String? = null


    @Keep
    @SerializedName("appName")
    @Expose
    var appName: String? = null

    @Keep
    @SerializedName("versionCode")
    @Expose
    var versionCode: Long? = null

    @Keep
    @SerializedName("forceUpdate")
    @Expose
    var forceUpdate : Boolean? =null


    @Keep
    @SerializedName("isMaintenance")
    @Expose
    var isMaintenance : Boolean? =null



    @Keep
    @SerializedName("icon")
    @Expose
    var icon : String? =null

    @Keep
    @SerializedName("versionName")
    @Expose
    var versionName: String? = null
}