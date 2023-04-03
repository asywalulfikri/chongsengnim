package sembako.sayunara.home

import com.google.firebase.firestore.QuerySnapshot
import sembako.sayunara.home.model.Menu
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.ArrayList

interface BannerView {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(querySnapshot: QuerySnapshot)
    fun onRequestFailed(code: Int?)
    fun onRequestProductSuccess(productArrayList: ArrayList<Product>)
    fun onRequestProductFailed(code: Int?)
    fun onRequestMenuSuccess(menuArrayList: ArrayList<Menu>)
    fun onRequestMenuFailed(code: Int?)
    fun setLoading(loading : Boolean)
    fun setupViews()
}