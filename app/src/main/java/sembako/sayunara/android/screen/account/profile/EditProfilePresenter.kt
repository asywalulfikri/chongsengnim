package sembako.sayunara.android.screen.account.profile

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.screen.account.profile.model.User
import sembako.sayunara.android.screen.base.BasePresenter
import java.lang.Exception
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

    override fun editUser() {
        view?.loadingIndicator(true)
        val contact: DocumentReference = mFireBaseFireStore.collection("users").document(view?.setUser!!.userId)
        contact.update(Constant.USER_KEY.username, view?.mUserName)
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()
        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        contact.update("updatedAt", dateUpdatedData)
                .addOnSuccessListener {
                    val docRef: Query = mFireBaseFireStore.collection(Constant.COLLECTION_USER).whereEqualTo(Constant.USER_KEY.userId, view!!.setUser!!.userId)
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (doc in task.result!!) {
                                view?.loadingIndicator(false)
                                val user = User()
                                user.avatar = doc.getString(Constant.USER_KEY.avatar)
                                user.isLogin = true
                                user.email = doc.getString(Constant.USER_KEY.email)
                                user.type = doc.getString(Constant.USER_KEY.type)
                                user.phoneNumber = doc.getString(Constant.USER_KEY.phoneNumber)
                                user.userId = doc.getString(Constant.USER_KEY.userId)
                                user.username = doc.getString(Constant.USER_KEY.username)
                                user.isActive = doc.getBoolean(Constant.USER_KEY.isActive)!!
                                user.storeId = doc.getString(Constant.USER_KEY.storeId)
                                user.isPartner = doc.getBoolean(Constant.USER_KEY.isPartner)!!
                                user.isVerified = doc.getBoolean(Constant.USER_KEY.isVerfied)!!

                                view!!.onRefresh(user)
                            }
                        } else {
                            view?.loadingIndicator(false)
                            onRequestFailed(task.exception?.message,task.exception.hashCode())
                        }
                    }
                    onRequestSuccess()
                    view?.loadingIndicator(false)

                }.addOnFailureListener {
                    onRequestFailed(it.message,it.hashCode())
                    view?.loadingIndicator(false)
                }

    }

    override fun onRequestSuccess() {
        view?.showErrorValidation(R.string.text_success_update_profile)
    }

    override fun onRequestFailed(message: String?, error: Int) {
        view?.showErrorValidation(R.string.text_save)
    }

}