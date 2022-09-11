package sembako.sayunara.android.ui.component.banner

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class Banner : Serializable {

    var id : String? = null
    var createdAt :  CreatedAt? =null
    var updatedAt :  UpdatedAt? =null
    var detail : Detail? =null
    var status : Status? =null

    @Keep
    class CreatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? =null
    }

    @Keep
    class UpdatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? =null
    }

    @Keep
    class Detail : Serializable {
        var title : String? = null
        var timestamp : Long? =null
        var image : String? =null
        var description : String? =null
    }
    @Keep
    class Status : Serializable {
        var active : Boolean? =null
        var draft : Boolean? =null

    }

}

