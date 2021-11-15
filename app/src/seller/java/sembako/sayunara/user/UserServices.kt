package sembako.sayunara.user

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import sembako.sayunara.android.constant.Constant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UserServices {


    @SuppressLint("SimpleDateFormat")
    internal fun editStatus(view: UserView.ViewDetail,param : String, value : Boolean, appId : String) {

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
                view.onRequestSuccess()
            }
            .addOnFailureListener { e ->
                view.onRequestFailed(e.message.toString())
            }
    }
}