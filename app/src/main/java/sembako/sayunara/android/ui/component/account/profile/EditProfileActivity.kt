package sembako.sayunara.android.ui.component.account.profile
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.content_edit_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.component.account.login.data.model.User

class EditProfileActivity : BaseActivity(),EditProfileContract.EditProfileView {
    override val mUserName: String
        get() = etUserName.text.toString().trim()

    override val setUser : User?
        get() = getUsers

    override fun onRefresh(user: User) {
        (activity as BaseActivity?)!!.saveUser(user)
        intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun onRequestFailed(code: Int?) {

    }

    private lateinit var editProfilePresenter : EditProfilePresenter

    override fun setupViews() {
        setupToolbar(toolbar, getString(R.string.text_edit_profile))
        etUserName.setText(getUsers!!.profile.username)
        etUserName.setSelection(getUsers!!.profile.username!!.length)
        etEmail.setText(getUsers!!.profile.email)
        etPhoneNumber.setText(getUsers!!.profile.phoneNumber)

        if(getUsers!!.profile.avatar!!.isNotEmpty()){
            Picasso.get()
                    .load(getUsers!!.profile.avatar)
                    .into(ivAvatar)
        }

        tvSave.setOnClickListener {
            hideKeyboard()
            editProfilePresenter.checkData()
        }
    }

    override fun setPresenter(presenter: BasePresenter<*>) {
    }

    override fun onAvatarEditSuccess(url: String) {

    }

    override fun onAvatarEditFailed(code: Int) {

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

    override fun onRequestSuccess() {

    }

    override fun onUploadAvatarSuccess(url : String) {

    }

    override fun onUploadAvatarFailed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        editProfilePresenter = EditProfilePresenter()
        editProfilePresenter.attachView(this)
        setupViews()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}

