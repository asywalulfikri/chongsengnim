package sembako.sayunara.android.ui.component.basket

import sembako.sayunara.android.ui.component.basket.model.ListBasket
import java.util.ArrayList

interface BasketView {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(basketArrayList: ArrayList<ListBasket>)
    fun onRequestFailed(code: Int?)
    fun setupViews()
}