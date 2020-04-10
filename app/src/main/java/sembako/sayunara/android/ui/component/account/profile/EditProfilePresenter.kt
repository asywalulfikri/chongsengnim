package sembako.sayunara.android.ui.component.account.profile

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.component.account.login.data.model.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditProfilePresenter: BasePresenter<EditProfileContract.EditProfileView>(), EditProfileContract.ActionListener {

    private var emailValidator = EmailValidator()
    private var mFireBaseAuth = FirebaseAuth.getInstance()
    private val mFireBaseFireStore = FirebaseFirestore.getInstance()


    override fun checkData() {
        when {
            view?.mUserName?.isEmpty()!! -> {
                view?.showErrorValidation(R.string.text_user_name_empty)
            }
            view?.mUserName?.length!! <3 -> {
                view?.showErrorValidation(R.string.text_user_name_to_short)

            }else -> {
                editUser()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun editUser() {
        view?.showProgress()
        val contact: DocumentReference = mFireBaseFireStore.collection("users").document(view?.setUser!!.userId!!)
        contact.update(Constant.UserKey.username, view?.mUserName)
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()
        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        contact.update("updatedAt", dateUpdatedData)
                .addOnSuccessListener {
                    val docRef: Query = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_USER).whereEqualTo(Constant.UserKey.userId, view!!.setUser!!.userId)
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (doc in task.result!!) {
                                view?.hideProgress()
                                val user = User()
                                user.avatar = doc.getString(Constant.UserKey.avatar)
                                user.isLogin = true
                                user.email = doc.getString(Constant.UserKey.email)
                                user.type = doc.getString(Constant.UserKey.type)
                                user.phoneNumber = doc.getString(Constant.UserKey.phoneNumber)
                                user.userId = doc.getString(Constant.UserKey.userId)
                                user.username = doc.getString(Constant.UserKey.username)
                                user.isActive = doc.getBoolean(Constant.UserKey.isActive)!!
                                //user.storeId = doc.getString(Constant.UserKey.storeId)
                                user.isPartner = doc.getBoolean(Constant.UserKey.isPartner)!!
                                user.isVerified = doc.getBoolean(Constant.UserKey.isVerified)!!

                                view!!.onRefresh(user)
                            }
                        } else {
                            view?.hideProgress()
                            onRequestFailed(task.exception?.message,task.exception.hashCode())
                        }
                    }
                    onRequestSuccess()
                    view?.hideProgress()

                }.addOnFailureListener {
                    onRequestFailed(it.message,it.hashCode())
                    view?.hideProgress()
                }

    }

    override fun onRequestSuccess() {
        view?.showErrorValidation(R.string.text_success_update_profile)
    }

    override fun onRequestFailed(message: String?, error: Int) {
        view?.showErrorValidation(R.string.text_save)
    }

}