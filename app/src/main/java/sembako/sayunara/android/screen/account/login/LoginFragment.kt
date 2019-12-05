package sembako.sayunara.android.screen.account.login

/**
 * Description: Login Fragment
 * Date: Wednesday 2019/12/06
 *
 * @author asywalulfikri@gmail.com
 */

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_login.*
import sembako.sayunara.android.R
import sembako.sayunara.android.helper.ConnectionFragment
import sembako.sayunara.android.screen.account.register.RegisterActivity
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.base.BasePresenter
import sembako.sayunara.android.screen.mainmenu.MainMenuActivity
import sembako.sayunara.android.screen.account.profile.model.User

class LoginFragment : ConnectionFragment(),LoginContract.LoginView {

    private lateinit var loginPresenter: LoginPresenter
    override val mEmail: String
        get() = etEmail.text.toString().trim()
    override val mPassword: String
        get() = etPassword.text.toString().trim()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPresenter = LoginPresenter()
        loginPresenter.attachView(this)
        setupViews()
    }

    private fun setupViews(){
        btnSubmit.setOnClickListener {
            loginPresenter.checkData()
        }
        tvRegister.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    override fun showErrorValidation(message: Int) {
        setToast(message)
    }

    override fun loadingIndicator(isLoading: Boolean) {
        //setDialog(isLoading,getString(R.string.text_login))
    }

    override fun onRefresh(user: User) {
        (activity as BaseActivity?)!!.saveUser(user)
        val intent = Intent(activity, MainMenuActivity::class.java)
        startActivity(intent)
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }


}

