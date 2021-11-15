package sembako.sayunara.android.ui.component.account.register

import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.yayandroid.locationmanager.configuration.Configurations
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.content_register.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.helper.CostumeEditText
import sembako.sayunara.android.helper.PasswordView
import sembako.sayunara.android.ui.base.BasePresenter

class RegisterActivity : LocationBaseActivity(),RegisterContract.SignUpView {

    override val mEtUserName: CostumeEditText
        get() = etUsername
    override val mEtMarketLocation: CostumeEditText
        get() = etMarketLocation
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
    override val mLocationGet: LocationGet
        get() = getLocation()


    override fun showErrorValidation(message: Int) {
        setToast(message)
    }

    override fun showProgress() {
        llProgressBar.visibility = View.VISIBLE
    }

    override fun onRegisterSuccess() {
        onBackPressed()
    }

    override fun hideProgress() {
        llProgressBar.visibility = View.GONE
    }

    override fun setColorButton(color: Int) {
        setColorTintButton(btnSubmit,getColors(this,color))
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }


    override fun onLocationChanged(location: Location) {
        setLocation(location)
    }

    override fun onLocationFailed(type: Int) {

    }

    private lateinit var registerPresenter: RegisterPresenter

    override val locationConfiguration: LocationConfiguration?
        get() = Configurations.defaultConfiguration("Gimme the permission!", "Would you mind to turn GPS on?")

    private var locationGet = LocationGet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerPresenter = RegisterPresenter()
        registerPresenter.attachView(this)
        registerPresenter.checkEditText()

        location
        setupViews()
    }


    override fun onResume() {
        super.onResume()
        if (locationManager!!.isWaitingForLocation
                && !locationManager!!.isAnyDialogShowing) {
        }
    }

    private fun showUnitDialog() {
        val type = arrayOf("Solok", "Pariaman", "Bukittingi", "Padang", "Padang Panjang", "Agam")
        val builder: android.app.AlertDialog.Builder = getBuilder(this)
        builder.setTitle(getString(R.string.text_market_location))
                .setItems(type) { _, which -> etMarketLocation.setText(type[which]) }
        builder.create().show()
    }

    private fun setupViews(){

        setupToolbar(toolbar, getString(R.string.text_register))

        etMarketLocation.setOnClickListener {
            hideKeyboard()
            showUnitDialog()
        }
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