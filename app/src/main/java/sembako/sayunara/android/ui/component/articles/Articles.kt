package sembako.sayunara.android.ui.component.articles

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
class Articles:Serializable {

    var id: String? = null
    var category = ArrayList<String>()
    var content: String? = null
    var html: String? = null
    var userId: String? = null
    var title: String? = null
    var source: String? = null
    var createdAt:  CreatedAt? =null
    var updatedAt: UpdatedAt? =null
    var viewType: Long? =null
    var status:  Status? =null
    var images = ArrayList<String>()

    @Keep
    class CreatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null
    }

    @Keep
    class Status : Serializable {
        var moderation : Boolean? = null
        var active : Boolean? = null
        var draft : Boolean? = null
        var publish : Boolean? = null
        var highlight : Boolean? = null
        var notification : Boolean? = null
    }

    @Keep
    class UpdatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null

    }

}
