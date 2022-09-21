package sembako.sayunara.home

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.customer.fragment_home.*
import kotlinx.android.synthetic.main.content_home.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.banner.Banner
import sembako.sayunara.android.ui.component.basket.BasketListActivity
import sembako.sayunara.home.adapter.MenuAdapter
import sembako.sayunara.home.model.Menu
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.favorite.ListFavoriteActivty
import sembako.sayunara.android.ui.component.product.listproduct.ListProductActivity2
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*

class HomeFragment : BaseFragment(),BannerView, MenuAdapter.OnClickListener,ProductAdapter.OnClickListener {

    private lateinit var menuAdapter : MenuAdapter
    var  menuArrayList: ArrayList<Menu> = ArrayList()
    private val bannerServices = HomeServices()
    private lateinit var productAdapter: ProductAdapter

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

    override fun onRequestProductSuccess(productArrayList: ArrayList<Product?>) {
        updateList(productArrayList)
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
            layoutManager = GridLayoutManager(activity, 4);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            menuAdapter = MenuAdapter()
            adapter = menuAdapter
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            productAdapter = ProductAdapter(activity,true,false,true,this@HomeFragment)
            adapter = productAdapter
        }

        bannerServices.getBanner(this)
    }

    private fun setupBanner(querySnapshot: QuerySnapshot){
        val list: MutableList<String> = ArrayList()

        for (doc in querySnapshot) {
            val banner = doc.toObject(Banner::class.java)
            list.add(banner.detail?.image.toString())
        }
        Log.d("isinya",list.size.toString())
        banner_1.setImagesUrl(list)
        pageIndicatorView.radius = 4
        pageIndicatorView.count = list.size
        pageIndicatorView.setViewPager(banner_1.mViewPager)
    }

    private fun updateList(productArrayList: ArrayList<Product?>) {
        productAdapter = ProductAdapter(activity,true,false,true,this)
        productAdapter.data = productArrayList
        recyclerView.adapter = productAdapter

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

}
