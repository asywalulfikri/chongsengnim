package sembako.sayunara.android.ui.component.home

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import sembako.sayunara.android.ui.component.home.model.Banner
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*

class HomeServices {


    internal fun getBanner(bannerView: BannerView,fireBaseFireStore: FirebaseFirestore) {

        val bannerArrayList: ArrayList<Banner> = ArrayList()
        val collectionReference = fireBaseFireStore.collection("banner")
        val query = collectionReference
                .limit(10)
                .orderBy("createdAt", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result!!) {
                    val banner = Banner()
                    banner.bannerId = doc.getString("bannerId")
                    banner.url = doc.getString("url")
                    bannerArrayList.add(banner)
                }
                bannerView.onRequestSuccess(bannerArrayList)
            } else {
                bannerView.onRequestFailed(task.hashCode())
            }
        }
    }


    internal fun getList(bannerView: BannerView,fireBaseFireStore: FirebaseFirestore) {

        val productArrayList: ArrayList<Product> = ArrayList()
        val collectionReference = fireBaseFireStore.collection("product")
        val query = collectionReference
                .whereEqualTo("isActive", true)
                .whereEqualTo("isHighLight", true)
                .limit(10)
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result!!) {
                    val product = doc.toObject(Product::class.java)
                    productArrayList.add(product)
                }
                bannerView.onRequestProductSuccess(productArrayList)

            } else {
                bannerView.onRequestProductFailed(task.hashCode())
            }
        }


    }
}