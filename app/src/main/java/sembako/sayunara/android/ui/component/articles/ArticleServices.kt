package sembako.sayunara.android.ui.component.articles

import android.annotation.SuppressLint
import android.util.Log
import com.firebase.client.core.operation.Merge
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.account.login.data.model.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ArticleServices {


    @SuppressLint("SimpleDateFormat")
    internal fun addArticle(view: ArticleView.ViewArticle, title : String, content : String, source : String, image : String, userId : String, category: String,moderation : Boolean,draft : Boolean, isEdit : Boolean,id : String) {

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

        if(!isEdit){
            obj["id"] = uuid
        }
        obj["title"] = title
        obj["content"] = content
        obj["source"] = source
        obj["image"] = image
        obj["userId"] = userId
        obj["active"] = true
        obj["moderation"] = moderation
        obj["draft"] = draft
        obj["html"] = ""

        val arrayType: List<String>
        val type = category.toLowerCase()
        arrayType = type.split(",")

        obj["category"] = arrayType


        if(!isEdit){
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(uuid)
                .set(obj)
                .addOnSuccessListener {
                    view.loadingIndicator(false)
                    view.onRequestSuccess()
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
                    view.onRequestSuccess()
                }
                .addOnFailureListener { e ->
                    view.loadingIndicator(false)
                    view.onRequestFailed(e.message.toString())
                }
        }
    }


    internal fun getListArticle(view : ArticleView.ViewList, fireBaseFireStore: FirebaseFirestore, user : User?) {

        val arrayList: ArrayList<Articles> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_ARTICLES)
        val query: Query?

        when (user?.profile?.type) {
            Constant.userType.typeSuperAdmin -> {
                query = collectionReference
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)

            }
            Constant.userType.typeAdmin -> {
                query = collectionReference
                    .whereEqualTo("status.active", true)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)

            }
            Constant.userType.typeSeller -> {
                query = collectionReference
                    .whereEqualTo("status.active", true)
                    .whereEqualTo(Constant.UserKey.userId, user.profile.userId)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
            }
            else -> {
                query =collectionReference
                    .whereEqualTo("status.active", true)
                    .whereEqualTo("status.publish", true)
                    .whereEqualTo("status.moderation", false)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
            }
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var articles: Articles
                for (doc in task.result) {
                    articles = doc.toObject(Articles::class.java)
                    Log.d("responseA", Gson().toJson(articles))
                    arrayList.add(articles)
                }
                view.onRequestSuccess(arrayList)

            } else {
                view.onRequestFailed(task.hashCode())
            }
        }

    }
}