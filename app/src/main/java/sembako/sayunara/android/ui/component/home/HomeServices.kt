package sembako.sayunara.android.ui.component.home

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import sembako.sayunara.android.ui.component.home.model.Banner
import sembako.sayunara.android.ui.component.home.model.Menu
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

               /* Log.d("resulnya bannber1", (task.result!!.documents+"---").toString())
                Log.d("resulnya bannber2",task.result!!.metadata.toString())
                Log.d("resulnya bannber3", (task.result!!+"---").toString())*/

                for (doc in task.result!!) {

                  /*  Log.d("resulnya bannber4",doc.metadata.toString()+"---")
                    Log.d("resulnya bannber5", doc.toString()+"---")*/

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


    internal fun getMenu(bannerView: BannerView,fireBaseFireStore: FirebaseFirestore){
        val menuArrayList: ArrayList<Menu> = ArrayList()
        val collectionReference = fireBaseFireStore.collection("menu")
        val query = collectionReference
                .whereEqualTo("isActive", true)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

              /*  Log.d("resulnya 1", (task.result!!.documents+"---").toString())
                Log.d("resulnya 2",task.result!!.metadata.toString())
                Log.d("resulnya 3 ", (task.result!!+"---").toString())*/

                for (doc in task.result!!) {
                    val menu = doc.toObject(Menu::class.java)
                    menuArrayList.add(menu)
                }
                bannerView.onRequestMenuSuccess(menuArrayList)

            } else {
                bannerView.onRequestMenuFailed(task.hashCode())
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