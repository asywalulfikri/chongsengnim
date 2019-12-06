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
import kotlinx.android.synthetic.main.content_login.*
import sembako.sayunara.android.R
import sembako.sayunara.android.admin.mainmenuadmin.MainMenuAdminActivity
import sembako.sayunara.android.screen.account.register.RegisterActivity
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.base.BasePresenter
import sembako.sayunara.android.screen.mainmenu.MainMenuActivity
import sembako.sayunara.android.screen.account.profile.model.User
import sembako.sayunara.android.screen.base.BaseFragment

class LoginFragment : BaseFragment(),LoginContract.LoginView {

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
            hideKeyboard()
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

    override fun showProgress() {
        llProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        llProgressBar.visibility = View.GONE
    }

    override fun onRefresh(user: User) {
        (activity as BaseActivity?)!!.saveUser(user)

        if(user.type=="admin"){
            val intent = Intent(activity, MainMenuAdminActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(activity, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }


}

