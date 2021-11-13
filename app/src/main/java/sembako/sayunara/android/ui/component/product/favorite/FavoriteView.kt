package sembako.sayunara.android.ui.component.product.favorite

import sembako.sayunara.android.ui.component.product.favorite.model.Favorite
import java.util.ArrayList

interface FavoriteView {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(arrayList: ArrayList<Favorite>)
    fun onRequestFailed(code: Int?)
    fun setupViews()
}