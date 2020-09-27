package sembako.sayunara.android.ui.component.basket

import java.util.ArrayList

interface BasketView {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(basketArrayList: ArrayList<Basket>)
    fun onRequestFailed(code: Int?)
    fun setupViews()
}