package sembako.sayunara.android.ui.component.account.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginState
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
        val contact: DocumentReference = mFireBaseFireStore.collection("users").document(view?.getUser?.profile?.userId.toString())
        contact.update("profile."+ Constant.UserKey.username, view?.mUserName)
        val tsLong = System.currentTimeMillis() / 1000
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val dateUpdatedData: MutableMap<String, Any> = HashMap()
        dateUpdatedData["iso"] = nowAsISO
        dateUpdatedData["timestamp"] = tsLong
        contact.update("updatedAt", dateUpdatedData)
                .addOnSuccessListener {
                    val docRef = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_USER).document(view?.getUser?.profile?.userId.toString())
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val user = task.result.toObject(User::class.java)
                            onRequestSuccess()
                            view?.hideProgress()
                            view?.onRefresh(user!!)
                            Log.d("response",Gson().toJson(user))
                            saveUserPrefs(user)

                        } else {
                            Toast.makeText(App.application, App.app!!.getString(R.string.text_user_not_found), Toast.LENGTH_SHORT).show()
                        }
                    }

                }.addOnFailureListener {
                    onRequestFailed(it.message.toString())
                    view?.hideProgress()
                }

    }

    private fun saveUserPrefs(user: User?) {
        App.tinyDB!!.putObject(Constant.Session.userSession, user)
        App.tinyDB!!.putBoolean(Constant.Session.isLogin, true)
    }

    override fun onRequestSuccess() {
        view?.showErrorValidation(R.string.text_success_update_profile)
    }

    override fun onRequestFailed(message: String?, error: Int) {
        view?.showErrorValidation(R.string.text_save)
    }

    override fun onRequestFailed(message: String) {
        view?.showErrorMessage(message)
    }

}