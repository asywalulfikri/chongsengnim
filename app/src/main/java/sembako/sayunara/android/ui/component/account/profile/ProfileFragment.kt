package sembako.sayunara.android.ui.component.account.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import sembako.sayunara.android.App
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
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
        Log.d("tipenyaaa ",user.profile.type+"--")
        et_first_name.text = user.profile.username
        et_email.text = user.profile.email
        tv_versionApp.text = BuildConfig.VERSION_NAME

        if(user.profile.avatar!=""){
            Picasso.get()
                    .load(user.profile.avatar)
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

        llRating.setOnClickListener {
            val appPackageName: String = activity!!.packageName // getPackageName() from Context or Activity object

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }


        ll_exit.setOnClickListener { confirmSignout() }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        if(isLogin()){
            val userVerification = FirebaseAuth.getInstance().currentUser
            if(userVerification?.isEmailVerified!!){
                llVerification.visibility =View.GONE
                et_email.text = user.profile.email + " " + "("+getString(R.string.text_verified)+")"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {
            user = (activity as BaseActivity).user!!
            updateView(user)
        }
    }

    private fun refresh(){
        val mFireBaseAuth = FirebaseAuth.getInstance()
        val mFireBaseFireStore = FirebaseFirestore.getInstance()
        val id = mFireBaseAuth.currentUser!!.uid
        val docRef = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_USER).document(id)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = task.result!!.toObject(User::class.java)
                if (user != null) {
                    swipeRefresh.isRefreshing = false
                    updateView(user)
                    saveUser(user)
                }

            } else {
                Toast.makeText(App.application, App.app!!.getString(R.string.text_user_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

