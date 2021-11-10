package sembako.sayunara.android.ui.component.basket

import sembako.sayunara.android.ui.component.basket.model.Basket
import java.util.ArrayList

interface BasketViewDetail {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(basketArrayList: ArrayList<Basket>)
    fun onRequestFailed(code: Int?)
    fun setupViews()
}