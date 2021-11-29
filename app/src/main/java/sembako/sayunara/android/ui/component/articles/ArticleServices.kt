package sembako.sayunara.android.ui.component.articles

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.account.login.data.model.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ArticleServices {


    @SuppressLint("SimpleDateFormat")
    internal fun addArticle(view: ArticleView.ViewArticle, title : String, content : String, source : String, image : String, userId : String, category: String,moderation : Boolean,draft : Boolean, isEdit : Boolean,id : String, highLight : Boolean, notification : Boolean) {

        view.loadingIndicator(true)
        val uuid = UUID.randomUUID().toString()
        val obj: MutableMap<String?, Any?> = HashMap()


        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()



        dateUpdatedData[Constant.UserKey.iso] = nowAsISO
        dateUpdatedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateUpdatedData

        val dateCreatedData: MutableMap<String, Any> = HashMap()

        dateCreatedData[Constant.UserKey.iso] = nowAsISO
        dateCreatedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.createdAt] = dateCreatedData

        val status: MutableMap<String, Any> = HashMap()
        status["draft"] = draft
        status["active"] = true
        status["moderation"] = moderation
        status["highlight"] = highLight
        status["publish"] = true
        status["notification"] = notification

        obj["status"] = status

        if(!isEdit){
            obj["id"] = uuid
        }
        obj["title"] = title
        obj["content"] = content
        obj["source"] = source
        obj["userId"] = userId
        obj["draft"] = draft
        obj["html"] = ""

        val arrayType: List<String>
        val type = category.lowercase(Locale.getDefault())
        arrayType = type.split(",")
        obj["category"] = arrayType


        val arrayImages: List<String> = image.split(",")
        obj["images"] = arrayImages


        if(!isEdit){
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(uuid)
                .set(obj)
                .addOnSuccessListener {
                    view.loadingIndicator(false)
                    if(draft){
                        view.onRequestSuccess(App.application!!.getString(R.string.text_draft_success),draft,uuid)
                    }else{
                        view.onRequestSuccess(App.application!!.getString(R.string.text_success),draft,uuid)
                    }
                }
                .addOnFailureListener { e ->
                    view.loadingIndicator(false)
                    view.onRequestFailed(e.message.toString())
                }
        }else{
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(id)
                .set(obj, SetOptions.merge())
                .addOnSuccessListener {
                    view.loadingIndicator(false)
                    if(draft){
                        view.onRequestSuccess(App.application!!.getString(R.string.text_draft_success),draft,id)
                    }else{
                        view.onRequestSuccess(App.application!!.getString(R.string.text_success),draft,id)
                    }
                }
                .addOnFailureListener { e ->
                    view.loadingIndicator(false)
                    view.onRequestFailed(e.message.toString())
                }
        }
    }


    internal fun getListArticle(view : ArticleView.ViewList, fireBaseFireStore: FirebaseFirestore, user : User?, mLastQueriedDocument : DocumentSnapshot?) {
        view.loadingIndicator(true)
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_ARTICLES)
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
                if(mLastQueriedDocument!=null){
                    query =collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.publish", true)
                        .whereEqualTo("status.moderation", false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument)
                }else{
                    query =collectionReference
                        .whereEqualTo("status.active", true)
                        .whereEqualTo("status.publish", true)
                        .whereEqualTo("status.moderation", false)
                        .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                        .limit(10)
                }
            }
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
    internal fun editStatus(position : Int,view: ArticleView.ViewList, param : String, value : Boolean, id : String) {
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

        obj["status"] = config

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(id)
            .set(obj, SetOptions.merge())
            .addOnSuccessListener {
                view.onStatusChange(param,position,value)
                view.loadingIndicator(false)
            }
            .addOnFailureListener { e ->
                view.onRequestFailed(e.message.toString())
                view.loadingIndicator(false)
            }
    }


    internal fun getDetailArticle(view : ArticleView.DetailArticle, id : String) {
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(id)

        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                val articles = task.result.toObject(Articles::class.java)
                view.onRequestSuccess(articles)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }

}