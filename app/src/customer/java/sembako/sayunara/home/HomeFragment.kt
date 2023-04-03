package sembako.sayunara.home

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.customer.fragment_home.*
import kotlinx.android.synthetic.main.content_home.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.banner.Banner
import sembako.sayunara.home.adapter.MenuAdapter
import sembako.sayunara.home.model.Menu
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.favorite.ListFavoriteActivty
import sembako.sayunara.android.ui.component.product.listproduct.ListProductActivity2
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter2
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*

class HomeFragment : BaseFragment(),BannerView, MenuAdapter.OnClickListener,ProductAdapter2.OnClickListener {

    private lateinit var menuAdapter : MenuAdapter
    var  menuArrayList: ArrayList<Menu> = ArrayList()
    private val bannerServices = HomeServices()
    private lateinit var productAdapter: ProductAdapter2
    private val userList:  ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

    }


    private fun updateListMenu(menuArrayList: ArrayList<Menu>) {
        this.menuArrayList = menuArrayList
        menuAdapter.setItems(menuArrayList, this)

    }

    override fun loadingIndicator(isLoading: Boolean) {

    }

    override fun onRequestSuccess(querySnapshot: QuerySnapshot) {
        progress_bar.visibility = View.GONE
        setupBanner(querySnapshot)
        bannerServices.getMenu(this, FirebaseFirestore.getInstance())
    }

    override fun onRequestFailed(code: Int?) {

    }

    override fun onRequestProductSuccess(productArrayList: ArrayList<Product>) {
        //updateList(productArrayList)
        getProfilePerList(productArrayList)
    }



    override fun onRequestProductFailed(code: Int?) {

    }

    override fun onRequestMenuSuccess(menuArrayList: ArrayList<Menu>) {
        updateListMenu(menuArrayList)

    }

    override fun onRequestMenuFailed(code: Int?) {

    }

    override fun setLoading(loading: Boolean) {

        if (loading) {
            recyclerViewCategory.showShimmer()
        } else {
            recyclerViewCategory.hideShimmer()
        }
    }

    override fun setupViews() {

        if(getToken()!=getUsers?.profile?.firebaseToken){
            updateTokenFirebase()
        }


        iv_favorite.setOnClickListener {
            startActivity(Intent(activity, ListFavoriteActivty::class.java))
        }

        etSearchView.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchView.text.toString() !== "") {
                    val text: String = etSearchView.text.toString().toLowerCase()
                    val text1 = text.split(" ").toTypedArray()
                    hideKeyboard()
                    val intent = Intent(activity, SearcListProductActivity::class.java)
                    intent.putExtra("keyword", text1[0])
                    startActivity(intent)
                }
            }
            false
        }

        recyclerViewCategory.run {
            layoutManager = GridLayoutManager(activity, 5);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            menuAdapter = MenuAdapter()
            adapter = menuAdapter
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            productAdapter = ProductAdapter2()
            adapter = productAdapter
        }

        bannerServices.getBanner(this)
        bannerServices.getList(this)
    }

    private fun setupBanner(querySnapshot: QuerySnapshot){
        val list: MutableList<String> = ArrayList()
        val imageList = ArrayList<SlideModel>() // Create image list

        for (doc in querySnapshot) {
            val banner = doc.toObject(Banner::class.java)
            list.add(banner.detail?.image.toString())
            imageList.add(SlideModel(banner.detail?.image,banner.detail?.title))
        }
        Log.d("isinya",list.size.toString())
       // banner_1.setImagesUrl(list)
        banner_1.setImageList(imageList, ScaleTypes.FIT)
    }
    private fun updateList(productArrayList: ArrayList<Product>) {
       // productAdapter = ProductAdapter2(activity,true,false,true,this)
        productAdapter.setItems(requireContext(),true,productArrayList,userList,this,false)
       // productAdapter.data = productArrayList
        recyclerView.adapter = productAdapter

    }
    private fun getProfilePerList(arrayList: ArrayList<Product>){
        for ( i in 0 until arrayList.size) {
            val product = arrayList[i]
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER).document(product?.userId.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(task.result.exists()){
                        val user = task.result.toObject(User::class.java)
                        if (user != null) {
                            userList.add(user)
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingIndicator(false)
                        updateList(arrayList)
                    }, 800)
                }
            }
        }

    }

    override fun onClickMenu(position: Int) {
        val menu = menuArrayList[position]
        val intent = Intent(activity, ListProductActivity2::class.java)
        intent.putExtra("type", menu.type.toString().lowercase())
        startActivity(intent)
    }

    override fun onClickDetail(position: Int, product: Product) {
        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }


    override fun onActionClick(position: Int, product: Product) {
        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

}
