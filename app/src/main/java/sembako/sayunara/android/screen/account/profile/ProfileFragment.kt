package sembako.sayunara.android.screen.account.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import sembako.sayunara.android.BuildConfig

import sembako.sayunara.android.R
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.base.BaseFragment
import sembako.sayunara.android.screen.account.profile.model.User

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

        ll_exit.setOnClickListener { confirmSignout() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {
            user = (activity as BaseActivity).user!!
            updateView(user)
        }
    }
}

