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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.seller.fragment_home.*
import sembako.sayunara.adapter.MainMenuAdapter
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.ItemClickSupport
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.articles.ListArticleActivity
import sembako.sayunara.android.ui.component.banner.ListBannerActivity
import sembako.sayunara.android.ui.component.product.listproduct.ListProductActivity2
import sembako.sayunara.apk.ApkListActivity
import sembako.sayunara.notification.PushNotificationActivity
import sembako.sayunara.transaction.ListTransactionAdminActivity
import sembako.sayunara.user.ListUserActivity
import sembako.sayunara.user.TabHostUserActivity
import java.util.ArrayList


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
    }



    @SuppressLint("SetTextI18n")
    fun setupMenu(){

        tvUsername.text = "Hallo "+ getUsers?.profile?.username.toString()
        tv_status.text = "Status kamu adalah "+getUsers?.profile?.type.toString()

        displayMainMenu()


    }

    private fun displayMainMenu() {


        recyclerview.setHasFixedSize(true)
        val mainMenuLayoutManager = GridLayoutManager(context, 4)
        recyclerview.layoutManager = mainMenuLayoutManager
        recyclerview.isNestedScrollingEnabled = false


        val mainMenuList: ArrayList<Home> = mainMenuList
        val adapter = activity?.let { MainMenuAdapter(it, mainMenuList, R.layout.list_item_home) }
        recyclerview.adapter = adapter
        ItemClickSupport.addTo(recyclerview)
            .setOnItemClickListener { _: RecyclerView?, position: Int, v: View? ->
                val home = mainMenuList[position]
                when (home.title) {
                    "Manage Product" -> {
                        val intent = Intent(activity, ListProductActivity2::class.java)
                        startActivity(intent)
                    }
                    "Manage Transaksi" -> {
                        val intent = Intent(activity, ListTransactionAdminActivity::class.java)
                        intent.putExtra("type", "all")
                        startActivity(intent)
                    }
                    "Manage User" -> {
                        val intent = Intent(activity, TabHostUserActivity::class.java)
                        startActivity(intent)
                    }
                    "Manage Menu" -> {
                        setToast("Masih dalam pengembangan")
                    }
                    "Manage Banner" -> {
                        val intent = Intent(activity, ListBannerActivity::class.java)
                        startActivity(intent)
                    }
                    "Manage Article" -> {
                        val intent = Intent(activity, ListArticleActivity::class.java)
                        startActivity(intent)
                    }
                    "Manage Apk" -> {
                        val intent = Intent(activity, ApkListActivity::class.java)
                        startActivity(intent)
                    }
                    "Notifikasi Promo" -> {
                        val intent = Intent(activity, PushNotificationActivity::class.java)
                        startActivity(intent)
                    }

                }
            }

    }


    private val mainMenuList: ArrayList<Home>
        get() {
            val arrayList = ArrayList<Home>()
            val item1 = Home()
            item1.title = "Manage Product"
            item1.icon = R.drawable.admin_product

            val item2 = Home()
            item2.title = "Manage Transaksi"
            item2.icon = R.drawable.admin_transaction

            val item3 = Home()
            item3.title = "Manage User"
            item3.icon = R.drawable.admin_user

            val item4 = Home()
            item4.title = "Manage Menu"
            item4.icon = R.drawable.admin_menu

            val item5 = Home()
            item5.title = "Manage Banner"
            item5.icon = R.drawable.admin_banner

            val item6 = Home()
            item6.title = "Manage Article"
            item6.icon = R.drawable.admin_article

            val item7 = Home()
            item7.title = "Manage Apk"
            item7.icon = R.drawable.admin_config

            val item8 = Home()
            item8.title = "Notifikasi Promo"
            item8.icon = R.drawable.admin_promo

            arrayList.add(item1)
            arrayList.add(item2)

            if(getAdmin()||getSuperAdmin()){
                arrayList.add(item3)
                arrayList.add(item4)
                arrayList.add(item5)
                arrayList.add(item7)
                arrayList.add(item8)
            }
            arrayList.add(item6)

            return arrayList
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
