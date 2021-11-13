package sembako.sayunara.android.ui.component.product.favorite

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.product.favorite.model.Favorite
import java.util.*

class FavoriteServices {

    internal fun getFavorite(view: FavoriteView,fireBaseFireStore: FirebaseFirestore,userId : String){

        val favoriteArrayList: ArrayList<Favorite> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_FAVORITE)
        var query = collectionReference
            .whereEqualTo(Constant.UserKey.userId, userId)
            .limit(10)
            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var favorite: Favorite
                for (doc in task.result!!) {
                    favorite = doc.toObject(Favorite::class.java)
                    Log.d("responsexz",Gson().toJson(favorite))
                    favoriteArrayList.add(favorite)
                }
                view.onRequestSuccess(favoriteArrayList)

            } else {
                view.onRequestFailed(task.hashCode())
            }
        }

    }
}