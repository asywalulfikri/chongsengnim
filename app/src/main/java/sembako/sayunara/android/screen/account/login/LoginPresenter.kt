package sembako.sayunara.android.screen.account.login

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.screen.base.BasePresenter
import sembako.sayunara.android.screen.account.profile.model.User

class LoginPresenter: BasePresenter<LoginContract.LoginView>(), LoginContract.ActionListener {

    private var emailValidator = EmailValidator()
    private var mFireBaseAuth = FirebaseAuth.getInstance()
    private val mFireBaseFireStore = FirebaseFirestore.getInstance()


    override fun checkData() {
        if (view?.mEmail?.isEmpty()!!) {
            view?.showErrorValidation(R.string.text_email_empty)
        }
        else if(view?.mPassword?.length!! <6){
            view?.showErrorValidation(R.string.text_password_to_short)

        } else if (view?.mPassword?.isEmpty()!!) {
            view?.showErrorValidation(R.string.text_password_empty)

        } else if (!emailValidator.isValid(view?.mEmail)){
            view?.showErrorValidation(R.string.text_email_invalid)

        } else{
            loginUser()
        }
    }

    override fun loginUser() {
        view?.loadingIndicator(true)
        mFireBaseAuth.signInWithEmailAndPassword(view!!.mEmail, view!!.mPassword).addOnCompleteListener { Task ->
            if (Task.isSuccessful) {
                val docRef: Query = mFireBaseFireStore.collection(Constant.COLLECTION_USER).whereEqualTo("email", view!!.mEmail)
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
                            onRequestSuccess(user)

                        }
                    } else {
                        view?.loadingIndicator(false)
                        showErrorResponse(task.exception?.message)
                        onRequestFailed(task.exception?.message,task.exception.hashCode())
                    }
                }
            } else {
                view?.loadingIndicator(false)
                showErrorResponse(Task.exception?.message)
                onRequestFailed(Task.exception?.message,Task.exception.hashCode())
            }
        }
    }

    override fun showErrorResponse(message: String?) {
        if(message== App.getApp().getString(R.string.text_response_email_not_axis)){
            view?.showErrorValidation(R.string.text_email_not_axis)
        }else if(message==App.getApp().getString(R.string.text_response_password_not_axis)){
            view?.showErrorValidation(R.string.text_password_wrong)
        }
    }

    override fun onRequestSuccess(user: User) {
       view?.onRefresh(user)
    }

    override fun onRequestFailed(message: String?, error: Int) {
         Log.d("error", "$message $error")
    }

}