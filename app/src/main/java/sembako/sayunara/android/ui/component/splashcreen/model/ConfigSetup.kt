package sembako.sayunara.android.ui.component.splashcreen.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.io.Serializable

@Keep
class ConfigSetup : Serializable {
    var appId: String? = null
    var appName: String? = null
    var updatedAt : UpdatedAt ? = null
    var icon : String? = null
    var config : Config ? = null

    @Keep
    class UpdatedAt {
        var iso : String? =null
        var timestamp : Long? =null

    }

    @Keep
    class Config {
        var versionCode: Long? = null
        var forceUpdate : Boolean? =null
        var versionName: String? = null
        var newSession: String? = null
        var status: Boolean? = null

    }
}