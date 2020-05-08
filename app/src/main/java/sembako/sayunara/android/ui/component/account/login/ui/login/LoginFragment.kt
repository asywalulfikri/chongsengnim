package sembako.sayunara.android.ui.component.account.login.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import debt.note.android.ui.login.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.register.RegisterActivity
import sembako.sayunara.android.ui.component.main.MainMenuActivity
import java.net.UnknownHostException


class LoginFragment : BaseFragment() {

    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        viewModel.loginState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginState.Requesting -> progressBar(true)
                is LoginState.OnFailed -> {
                    progressBar(false)
                    val t = it.t
                    if (t is UnknownHostException) {
                        setToast("No Connection")
                    } else {
                        when {
                            t!!.message.toString()== Constant.CallbackResponse.EMAIL_NOT_REGISTERED -> {
                                setToast(getString(R.string.text_email_not_registered))
                            }
                            t.message.toString()==Constant.CallbackResponse.PASSWORD_IS_INVALID -> {
                                setToast(getString(R.string.text_password_not_correct))
                            }
                            else -> {
                                setToast(t.message)
                            }
                        }
                    }
                }
                is LoginState.OnSuccess -> {
                    saveUser(it.user)
                    progressBar(false)
                    startActivity(Intent(activity, MainMenuActivity::class.java))
                }

            }
        })

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
