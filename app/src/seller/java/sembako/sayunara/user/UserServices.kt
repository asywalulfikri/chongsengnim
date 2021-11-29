package sembako.sayunara.user

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import sembako.sayunara.android.constant.Constant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UserServices {


    internal fun getList(view: UserView.List,mLastQueriedDocument : DocumentSnapshot?){
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER)
        val query: Query = if(mLastQueriedDocument!=null){
            collectionReference
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .startAfter(mLastQueriedDocument)
        }else{
            collectionReference
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .limit(10)
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }

    internal fun getListByType(view: UserView.List,mLastQueriedDocument : DocumentSnapshot?,type : String){
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER)
        val query: Query = if(mLastQueriedDocument!=null){
            collectionReference
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("profile.type",type)
                .startAfter(mLastQueriedDocument)
        }else{
            collectionReference
                .whereEqualTo("profile.type",type)
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .limit(10)
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }


    internal fun getListSearchUser(view: UserView.List,mLastQueriedDocument : DocumentSnapshot?,keyword : String){
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER)
        val query: Query = if(mLastQueriedDocument!=null){
            collectionReference
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("profile.type","user")
                .whereArrayContains("profile.search",keyword)
                .startAfter(mLastQueriedDocument)
        }else{
            collectionReference
                .whereEqualTo("profile.type","user")
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                .whereArrayContains("profile.search",keyword)
                .limit(10)
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    internal fun editStatus(view: UserView.ViewDetail,param : String, value : Any, appId : String) {
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
        config[param] = value

        obj[Constant.UserKey.profile] = config

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER).document(appId)
            .set(obj, SetOptions.merge())
            .addOnSuccessListener {
                view.loadingIndicator(false)
                if(param=="avatar"){
                    view.onAvatarSuccess()
                }else{
                    view.onRequestSuccess()
                }
            }
            .addOnFailureListener { e ->
                view.loadingIndicator(false)
                view.onRequestFailed(e.message.toString())
            }
    }


    @SuppressLint("SimpleDateFormat")
    internal fun deleteAccount(view: UserView.ViewDetail,userId: String) {
        view.loadingIndicator(true)
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER).document(userId)
            .delete()
            .addOnSuccessListener {
                view.loadingIndicator(false)
                view.onDeleteSuccess("Berhasil menghapus user")
            }
            .addOnFailureListener { e ->
                view.loadingIndicator(false)
                view.onRequestFailed(e.message.toString())
            }
    }


    @SuppressLint("SimpleDateFormat")
    internal fun generateSearch(view : UserView.ViewDetail,id : String,arraySearch: List<String>) {
        view.loadingIndicator(true)
        val obj: MutableMap<String?, Any?> = HashMap()
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()

        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        obj["updatedAt"] = dateUpdatedData

        obj["profile.search"] = arraySearch

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER).document(id)
            .update(obj)
            .addOnSuccessListener {
                view.onAvatarSuccess()
                view.loadingIndicator(false)
            }
            .addOnFailureListener { e ->
                view.onRequestFailed(e.message.toString())
                view.loadingIndicator(false)
            }
    }

}