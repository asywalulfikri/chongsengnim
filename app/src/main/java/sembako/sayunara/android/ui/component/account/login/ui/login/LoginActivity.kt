package sembako.sayunara.android.ui.component.account.login.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import debt.note.android.ui.login.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.register.RegisterActivity
import sembako.sayunara.main.MainActivity
import java.net.UnknownHostException


class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        viewModel.loginState.observe(this, {
            when (it) {
                is LoginState.Requesting -> progressBar(true)
                is LoginState.OnFailed -> {
                    progressBar(false)

                    when (it.message) {
                        Constant.CallbackResponse.EMAIL_NOT_REGISTERED -> {
                            setToast(getString(R.string.text_email_not_registered))
                        }
                        Constant.CallbackResponse.PASSWORD_IS_INVALID -> {
                            setToast(getString(R.string.text_password_not_correct))
                        }
                        Constant.CallbackResponse.ACCOUNT_DISABLED -> {
                            dialogSuspend()
                        }
                        else -> {
                            setToast(it.message)
                        }
                    }
                }
                is LoginState.OnSuccess -> {
                    progressBar(false)

                    if(isCustomer()){
                        saveUser(it.user)
                        onBackPressed()
                    }else{
                        val type = it.user.profile.type.toString().toLowerCase()
                        if(type==Constant.userType.typeSeller ||type==Constant.userType.typeAdmin||type==Constant.userType.typeSuperAdmin){
                            saveUser(it.user)
                            startActivity(Intent(activity, MainActivity::class.java))
                        }else{
                            Toast.makeText(App.application, App.app?.getString(R.string.text_not_seller), Toast.LENGTH_SHORT).show()
                        }

                    }

                }

            }
        })

        val email = getUsers?.profile?.email.toString()
        if(email!=""){
            etEmail.setText(email)
            etEmail.setSelection(email.length)
        }

        btnSubmit.setOnClickListener {
            if(viewModel.validation(etEmail,etPassword)){
                hideKeyboard()
                viewModel.login(etEmail.text.toString(), etPassword.text.toString())
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(activity, RegisterActivity::class.java))
        }
    }

    private fun progressBar(status : Boolean){
        if(status){
            llProgressBar.visibility = View.VISIBLE
        }else{
            llProgressBar.visibility = View.GONE
        }
    }

}
