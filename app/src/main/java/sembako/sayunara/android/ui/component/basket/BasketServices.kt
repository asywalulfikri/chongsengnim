package sembako.sayunara.android.ui.component.basket

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import sembako.sayunara.android.ui.component.product.favorite.model.Favorite
import java.util.*

class BasketServices {

    internal fun getBasket(basketView: BasketView,fireBaseFireStore: FirebaseFirestore,userId : String){

        val basketArrayList: ArrayList<ListBasket> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET)
        val query = collectionReference
            .whereEqualTo(Constant.UserKey.userId, userId)
            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var basket: ListBasket
                for (doc in task.result!!) {
                    basket = doc.toObject(ListBasket::class.java)
                    Log.d("response",Gson().toJson(basket))
                    basketArrayList.add(basket)
                }
                basketView.onRequestSuccess(basketArrayList)

            } else {
                basketView.onRequestFailed(task.hashCode())
            }
        }

    }


    internal fun getFavorite(basketView: BasketView,fireBaseFireStore: FirebaseFirestore,userId : String){

        val favoriteArrayList: ArrayList<Favorite> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_FAVORITE)
        val query = collectionReference
            .whereEqualTo(Constant.UserKey.userId, userId)
            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var favorite: Favorite
                for (doc in task.result!!) {
                    favorite = doc.toObject(Favorite::class.java)
                    Log.d("response",Gson().toJson(favorite))
                    favoriteArrayList.add(favorite)
                }
               // basketView.onRequestSuccess(basketArrayList)

            } else {
                basketView.onRequestFailed(task.hashCode())
            }
        }

    }

    internal fun getBasketDetail(basketView: BasketViewDetail,fireBaseFireStore: FirebaseFirestore,userId : String, id : String){
        val basketArrayList: ArrayList<Basket> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET)
            .document(id)
            .collection(Constant.Collection.COLLECTION_BASKET_PRODUCT_LIST)

        val query = collectionReference
            .whereEqualTo(Constant.UserKey.userId, userId)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var basket: Basket

                for (doc in task.result!!) {
                    basket = doc.toObject(Basket::class.java)
                    basketArrayList.add(basket)
                }
                basketView.onRequestSuccess(basketArrayList)

            } else {
                basketView.onRequestFailed(task.hashCode())
            }
        }

    }



    /* internal fun getMenu(bannerView: BasketView, fireBaseFireStore: FirebaseFirestore, userId : String){
         val menuArrayList: ArrayList<Basket> = ArrayList()
         val collectionReference = fireBaseFireStore.collection("basket")
         val query = collectionReference
                 .whereEqualTo("userId",userId )
         query.get().addOnCompleteListener { task ->
             if (task.isSuccessful) {

                 *//*  Log.d("resulnya 1", (task.result!!.documents+"---").toString())
                  Log.d("resulnya 2",task.result!!.metadata.toString())
                  Log.d("resulnya 3 ", (task.result!!+"---").toString())*//*

                for (doc in task.result!!) {
                    val menu = doc.toObject(Basket::class.java)
                    menuArrayList.add(menu)
                }
                bannerView.onRequestSuccess(menuArrayList)

            } else {
                bannerView.onRequestFailed(task.hashCode())
            }
        }

    }*/

}