package sembako.sayunara.android.ui.component.product.editProduct.model
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ImageData(
    val id: Long,
    val name: String,
    val path: String,
    val uri : Uri,
    val isUpload : Boolean
) : Parcelable
