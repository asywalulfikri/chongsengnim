package sembako.sayunara.android.ui.component.account.address

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class Address:Serializable {

    var id: String? = null
    var firstAddress : String?  =null
    var contact : Contact? =null
    var address : Address? =null
    var createdAt : CreatedAt? =null
    var updatedAt : UpdatedAt? =null

    @Keep
    class CreatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null
    }

    @Keep
    class Contact : Serializable {
        var userId: String? = null
        var phoneNumber: String? = null
        var name: String? = null
    }

    @Keep
    class Address : Serializable {
        var city: String? = null
        var fullAddress: String? = null
        var province: String? = null
        var subDistrict: String? = null
        var village: String? = null
        var tag : String? =null
        var isChecked = false
    }

    @Keep
    class UpdatedAt : Serializable {
        var iso : String? =null
        var timestamp : Long? = null

    }

}
