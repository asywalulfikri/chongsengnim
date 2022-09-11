package sembako.sayunara.android.ui.component.banner

import android.annotation.SuppressLint
import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import sembako.sayunara.android.constant.Constant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import sembako.sayunara.android.ui.component.account.login.data.model.User


class BannerServices {


    internal fun getList(view: BannerView.List,mLastQueriedDocument : DocumentSnapshot?,user : User?){
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER)
        val query: Query

        if(user?.profile?.type == Constant.userType.typeAdmin || user?.profile?.type == Constant.userType.typeSuperAdmin){
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
        }else{
            query = if(mLastQueriedDocument!=null){
                collectionReference
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .whereEqualTo("status.draft",false)
                    .whereEqualTo("status.active",true)
                    .startAfter(mLastQueriedDocument)
            }else{
                collectionReference
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("status.draft",false)
                    .whereEqualTo("status.active",true)
                    .limit(10)
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


    internal fun getDetail(view: BannerView.ViewDetail , id : String){
        view.loadingIndicator(true)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER).document(id)

        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                val banner = task.result.toObject(Banner::class.java)
                view.onRequestDetailSuccess(banner)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }


    @SuppressLint("SimpleDateFormat")
    internal fun addBanner(view: BannerView.Post,title : String, content : String, image : String, draft : Boolean,isEdit : Boolean, id : String?) {
        val bannerId : String
        view.loadingIndicator(true)

        val uuid = UUID.randomUUID().toString()

        bannerId = if(isEdit){
            id.toString()
        }else{
            uuid
        }

        val obj: MutableMap<String?, Any?> = HashMap()
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()

        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        obj["updatedAt"] = dateUpdatedData

        if(!isEdit){
            val dateCreatedData: MutableMap<String, Any> = HashMap()
            dateCreatedData["iso"] = nowAsISO
            dateCreatedData["timestamp"] = tsLong
            obj["createdAt"] = dateUpdatedData
        }

        val detail: MutableMap<String, Any> = HashMap()
        detail["title"] = title
        detail["description"] = content
        detail["image"] = image

        obj["detail"] = detail


        val status: MutableMap<String, Any> = HashMap()
        status["active"] = true
        status["draft"] = draft
        obj["status"] = status


        obj["id"] = bannerId



        if(isEdit){
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER).document(bannerId)
                .update(obj)
                .addOnSuccessListener {
                    view.loadingIndicator(false)
                    view.onRequestSuccess()
                }
                .addOnFailureListener { e ->
                    view.loadingIndicator(false)
                    view.onRequestFailed(e.message.toString())
                }
        }else{
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER).document(bannerId)
                .set(obj)
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

    @SuppressLint("SimpleDateFormat")
    internal fun editStatus(view: BannerView.ViewDetail,param : String,active : Boolean, id : String) {
        view.loadingIndicator(true)
        val obj: MutableMap<String?, Any?> = HashMap()
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()

        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        obj["updatedAt"] = dateUpdatedData

        val config: MutableMap<String, Boolean> = HashMap()
        config[param] = active
        obj["status"] = config

        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER).document(id)
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


    internal fun deleteBanner(view: BannerView.ViewDetail,id: String) {
        view.loadingIndicator(true)
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BANNER).document(id)
            .delete()
            .addOnSuccessListener {
                view.loadingIndicator(false)
                view.onDeleteSuccess("Berhasil menghapus banner")
            }
            .addOnFailureListener { e ->
                view.loadingIndicator(false)
                view.onRequestFailed(e.message.toString())
            }
    }


    fun uploadImage(view: BannerView.Post,filePath : Uri?) {
        view.loadingIndicator(true)
        if (filePath != null) {
            val ref: StorageReference = FirebaseStorage.getInstance().reference.child(
                "banner/" + UUID.randomUUID().toString()
            )

            ref.putFile(filePath)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadPhotoUrl ->
                        view.onRequestImageSuccess(downloadPhotoUrl.toString())
                    }
                }
                .addOnFailureListener {
                    view.onRequestFailed(it.message.toString())
                    view.loadingIndicator(false)
                }
                .addOnProgressListener { taskSnapshot ->
                    /*val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Uploading...")
                    progressDialog.show()
                    val progress: Double = ((100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount))
                    progressDialog.setMessage(("Uploaded " + progress.toInt() + "%"))*/

                }

        }
    }


}