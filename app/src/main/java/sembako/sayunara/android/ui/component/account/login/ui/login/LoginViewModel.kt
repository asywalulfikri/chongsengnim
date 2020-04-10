package sembako.sayunara.android.ui.component.account.login.ui.login

import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import debt.note.android.ui.login.ui.login.LoginState
import sembako.sayunara.android.ui.component.account.login.data.LoginRepository
import sembako.sayunara.android.ui.component.account.login.data.model.User
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private var emailValidator = EmailValidator()
    val loginState = MutableLiveData<LoginState>()

    fun login(email: String, password: String) {
        // can be launched in a separate asynchronous job

        loginState.postValue(LoginState.Requesting)
        val mFireBaseAuth = FirebaseAuth.getInstance()
        val mFireBaseFireStore = FirebaseFirestore.getInstance()
        mFireBaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { Task ->
            if (Task.isSuccessful) {
                val docRef: Query = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_USER).whereEqualTo(Constant.UserKey.email,email)
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (doc in task.result!!) {
                            val user = doc.toObject(User::class.java)
                            loginState.postValue(LoginState.OnSuccess(user))
                        }
                    } else {
                        Toast.makeText(App.application, App.app!!.getString(R.string.text_user_not_found),Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                loginState.postValue(LoginState.OnFailed(Task.exception))
            }
        }
    }


    // A placeholder username validation check
    fun validation(etEmail : EditText,etPassword: EditText):Boolean{
        if(!emailValidator.isValid(etEmail.text.toString())){
            etEmail.error = App.app!!.getString(R.string.invalid_email)
            return false
        }else if(etPassword.text.toString().isEmpty()){
            etPassword.error = App.app!!.getString(R.string.text_error_field_empty)
            return false
        }else if(!isPasswordValid(etPassword.text.toString())){
            etPassword.error = App.app!!.getString(R.string.text_password_to_short)
            return false
        }
        return true
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
