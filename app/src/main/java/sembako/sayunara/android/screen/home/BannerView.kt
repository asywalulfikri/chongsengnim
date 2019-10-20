package sembako.sayunara.android.screen.home

import sembako.sayunara.android.screen.home.model.Banner
import sembako.sayunara.android.screen.product.listproduct.model.Product
import java.util.ArrayList

interface BannerView {
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(bannerArrayList: ArrayList<Banner>)
    fun onRequestFailed(code: Int?)
    fun onRequestProductSuccess(productArrayList: ArrayList<Product>)
    fun onRequestProductFailed(code: Int?)
    fun setupViews()
}