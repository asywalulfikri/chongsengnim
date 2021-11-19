package sembako.sayunara.home

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.seller.fragment_home.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.articles.ListArticleActivity
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.splashcreen.SplashScreenState
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.apk.ApkListActivity
import sembako.sayunara.article.PostArticleActivity
import sembako.sayunara.constant.valueApp
import sembako.sayunara.notification.PushNotifActivty
import sembako.sayunara.product.list.ListProductActivity
import sembako.sayunara.user.ListUserActivity
import java.util.HashMap


class HomeFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        Log.d("tokenNow",getToken())
        Log.d("tokenOld",getUsers?.profile?.firebaseToken+"--")

        if(getToken()!=getUsers?.profile?.firebaseToken){
            updateTokenFirebase()
        }

        xx()
    }

    private fun xx(){
    }

    @SuppressLint("SetTextI18n")
    fun setupMenu(){

        tvUsername.text = "Hallo "+ getUsers?.profile?.username.toString()
        tv_status.text = "Status kamu adalah "+getUsers?.profile?.type.toString()


        cv_product.setOnClickListener {
            val intent = Intent(activity, ListProductActivity::class.java)
            intent.putExtra("type","all")
            startActivity(intent)

        }

        if(getAdmin()||getSuperAdmin()){
            cv_user.visibility = View.VISIBLE
            cvManageApk.visibility = View.VISIBLE
        }else{
            cv_user.visibility = View.GONE
            cvManageApk.visibility = View.GONE
        }

        cv_user.setOnClickListener {
            val intent = Intent(activity,ListUserActivity::class.java)
            startActivity(intent)
        }

        cvManageApk.setOnClickListener {
            val intent = Intent(activity,ApkListActivity::class.java)
            startActivity(intent)
        }

        cv_menu.setOnClickListener {
            val intent = Intent(activity,PushNotifActivty::class.java)
            startActivity(intent)
        }

        cvArticle.setOnClickListener {
            val intent = Intent(activity,ListArticleActivity::class.java)
            startActivity(intent)

        }

    }

    fun refreshUser(){
        val mFireBaseFireStore = FirebaseFirestore.getInstance()
        val docRef = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_USER).document(getUsers?.profile?.userId.toString())
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result.toObject(User::class.java)
                user?.let { saveUser(it) }

            } else {

            }
        }

    }
}
