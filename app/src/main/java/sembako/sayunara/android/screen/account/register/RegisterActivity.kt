package sembako.sayunara.android.screen.account.register

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.content_register.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.helper.CostumeEditText
import sembako.sayunara.android.helper.PasswordView
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.base.BasePresenter


class RegisterActivity :BaseActivity(),RegisterContract.SignUpView {

    override val mEtUserName: CostumeEditText
        get() = etUserName
    override val mEtEmail: CostumeEditText
        get() = etEmail
    override val mEtPassword: PasswordView
        get() = etPassword
    override val mEtConfirmPassword: PasswordView
        get() = etConfirmPassword
    override val mEtPhoneNumber: CostumeEditText
        get() = etPhoneNumber
    override val mLatitude: String?
        get() = latitude
    override val mLongitude: String?
        get() = longitude
    override val mDevicesId: String?
        get() = getDevicesId()

    override fun showErrorValidation(message: Int) {
        setToast(message)
    }

    override fun showProgress() {
        llProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        llProgressBar.visibility = View.GONE
    }

    override fun setColorButton(color: Int) {
        setColorTintButton(btnSubmit,getColors(this,color))
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }

    private lateinit var registerPresenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerPresenter = RegisterPresenter()
        registerPresenter.attachView(this)
        registerPresenter.checkEditText()

        setupViews()
    }

    private fun setupViews(){
        setToolbar(toolbar, getString(R.string.text_register))
        btnSubmit.setOnClickListener {
            hideKeyboard()
            registerPresenter.checkData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}