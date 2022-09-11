package sembako.sayunara.home

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import sembako.sayunara.home.model.Menu
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*

class HomeServices {


    internal fun getBanner(bannerView: BannerView) {
        val collectionReference = FirebaseFirestore.getInstance().collection("banner")
        val query = collectionReference
            .limit(10)
            .whereEqualTo("status.draft",false)
            .whereEqualTo("status.active",true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                bannerView.onRequestSuccess(task.result)
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

        val productArrayList: ArrayList<Product?> = ArrayList()
        val collectionReference = fireBaseFireStore.collection("product")
        val query = collectionReference
            .whereEqualTo("status.active", true)
            .whereEqualTo("status.highlight", true)
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