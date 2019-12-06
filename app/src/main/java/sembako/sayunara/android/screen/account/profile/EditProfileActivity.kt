package sembako.sayunara.android.screen.account.profile
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
import sembako.sayunara.android.screen.account.profile.model.User
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.base.BasePresenter

class EditProfileActivity : BaseActivity(),EditProfileContract.EditProfileView {
    override val mUserName: String
        get() = etUserName.text.toString().trim()

    override val setUser : User?
        get() = user

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
        setToolbar(toolbar, getString(R.string.text_edit_profile))
        etUserName.setText(user!!.username)
        etUserName.setSelection(user!!.username!!.length)
        etEmail.setText(user!!.email)
        etPhoneNumber.setText(user!!.phoneNumber)

        if(user!!.avatar!!.isNotEmpty()){
            Picasso.get()
                    .load(user!!.avatar)
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

