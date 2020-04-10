package sembako.sayunara.android.ui.component.account.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import sembako.sayunara.android.BuildConfig

import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.verification.VerificationActivity

class ProfileFragment : BaseFragment() {

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = (activity as BaseActivity).user!!
        updateView(user)

    }

    private fun updateView(user: User){
        et_first_name.text = user.username
        et_email.text = user.email
        tv_versionApp.text = BuildConfig.VERSION_NAME

        if(user.avatar!=""){
            Picasso.get()
                    .load(user.avatar)
                    .into(ivAvatar)
        }

        btnSubmit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivityForResult(intent,1313)
        }

        llVerification.setOnClickListener {
            val intent = Intent(activity, VerificationActivity::class.java)
            startActivityForResult(intent,1212)
        }


        ll_exit.setOnClickListener { confirmSignout() }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val userVerification = FirebaseAuth.getInstance().currentUser
        if(userVerification?.isEmailVerified!!){
            llVerification.visibility =View.GONE
            et_email.text = user.email + " " + "("+getString(R.string.text_verified)+")"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {
            user = (activity as BaseActivity).user!!
            updateView(user)
        }
    }
}

