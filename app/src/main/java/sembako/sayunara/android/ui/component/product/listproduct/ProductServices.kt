package sembako.sayunara.android.ui.component.product.listproduct

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.constant.valueApp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductServices {

    internal fun getListGeneral(view : ProductView.List, user : User?, mLastQueriedDocument : DocumentSnapshot?) {
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT)
        val query: Query?

        when (user?.profile?.type) {
            Constant.userType.typeSuperAdmin -> {
                query = if(mLastQueriedDocument!=null){
                    collectionReference
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    collectionReference
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }
            }
            Constant.userType.typeAdmin -> {
                query = if(mLastQueriedDocument!=null){
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }

            }
            Constant.userType.typeSeller -> {

                query = if(mLastQueriedDocument!=null){
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo(Constant.UserKey.userId, user.profile.userId)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo(Constant.UserKey.userId, user.profile.userId)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }
            }
            else -> {
                query = if(mLastQueriedDocument!=null){
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.draft",false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.draft",false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }
            }
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }



    internal fun getListByType(view : ProductView.List, user : User?, mLastQueriedDocument : DocumentSnapshot?, type : String) {
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT)
        val query: Query?

        when (user?.profile?.type) {
            Constant.userType.typeSuperAdmin -> {

                if(isCustomer()){

                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .whereArrayContains("type",type)
                            .whereEqualTo("status.draft",false)
                            .limit(10)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo("status.draft",false)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }

                }else{

                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .whereArrayContains("type",type)
                            .limit(10)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereArrayContains("type",type)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }

                }

            }
            Constant.userType.typeAdmin -> {

                if(isCustomer()){
                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo("status.draft",false)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo("status.draft",false)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }
                }else{
                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }
                }


            }
            Constant.userType.typeSeller -> {

                if(isCustomer()){
                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                            .whereEqualTo("status.draft",false)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo("status.draft",false)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }
                }else{
                    query = if(mLastQueriedDocument!=null){
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo(Constant.UserKey.userId, user.profile.userId)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                            .startAfter(mLastQueriedDocument)
                    }else{
                        collectionReference
                            .whereEqualTo("status.active", true)
                            .whereArrayContains("type",type)
                            .whereEqualTo(Constant.UserKey.userId, user.profile.userId)
                            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                            .limit(10)
                    }
                }

            }
            else -> {
                query = if(mLastQueriedDocument!=null){
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.publish",true)
                        .whereArrayContains("detail.type",type)
                        .whereEqualTo("status.draft",false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.publish",true)
                        .whereArrayContains("detail.type",type)
                        .whereEqualTo("status.draft",false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }
            }
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }


    @SuppressLint("SimpleDateFormat")
    internal fun editStatus(position : Int,view: ProductView.List, isActive : String, active : Boolean,isPublish : String, publish : Boolean, id : String) {
        view.loadingIndicator(true)
        val obj: MutableMap<String?, Any?> = HashMap()
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()

        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        obj["updatedAt"] = dateUpdatedData

        val config: MutableMap<String, Any> = HashMap()
        config[isActive] = active
        config[isPublish] = publish

        obj["status"] = config

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT).document(id)
            .set(obj, SetOptions.merge())
            .addOnSuccessListener {
                view.onStatusChange(position,isActive,active,isPublish,publish)
                view.loadingIndicator(false)
            }
            .addOnFailureListener { e ->
                view.onRequestFailed(e.message.toString())
                view.loadingIndicator(false)
            }
    }


    internal fun getDetail(view : ProductView.Detail, id : String) {
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(id)

        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                val product = task.result.toObject(Product::class.java)
                view.onRequestSuccess(product)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }

    private fun isCustomer(): Boolean {
        return valueApp.AppInfo.applicationId == "sembako.sayunara.android"
    }
}