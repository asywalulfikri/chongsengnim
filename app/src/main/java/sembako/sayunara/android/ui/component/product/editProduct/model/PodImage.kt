package sembako.sayunara.android.ui.component.product.editProduct.model

import android.net.Uri
import java.io.Serializable

class PodImage : Serializable {
    var source: Uri? = null
    var isSignature = false
}