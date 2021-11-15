package sembako.sayunara.apk

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.constant.valueApp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ApkServices {

    internal fun getList(view: ApkView.ViewList,fireBaseFireStore: FirebaseFirestore){

        val arrayList: ArrayList<ConfigSetup> = ArrayList()
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_CONFIG)
        val query = collectionReference
            .orderBy("updatedAt.timestamp", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var basket: ConfigSetup
                for (doc in task.result!!) {
                    basket = doc.toObject(ConfigSetup::class.java)
                    Log.d("response",Gson().toJson(basket))
                    arrayList.add(basket)
                }
                view.onRequestSuccess(arrayList)

            } else {
                view.onRequestFailed(task.hashCode())
            }
        }

    }



    @SuppressLint("SimpleDateFormat")
    internal fun editStatus(view: ApkView.ViewDetail,param : String, value : Boolean, appId : String) {

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

        obj["config"] = config

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_CONFIG).document(appId)
            .set(obj, SetOptions.merge())
            .addOnSuccessListener {
                view.onRequestSuccess()
            }
            .addOnFailureListener { e ->
                view.onRequestFailed(e.message.toString())
            }
    }
}