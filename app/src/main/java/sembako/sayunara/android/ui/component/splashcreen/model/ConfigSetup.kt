package sembako.sayunara.android.ui.component.splashcreen.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class ConfigSetup : Serializable {
    var appId: String? = null
    var cessionCode: String? = null
    var appName: String? = null
    var versionCode: Long? = null
    var forceUpdate : Boolean? =null
    var isMaintenance : Boolean? =null
    var icon : String? =null
    var versionName: String? = null
}