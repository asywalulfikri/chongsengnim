/*
package sembako.sayunara.android.ui.component.home

*/
/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 *//*


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.firestore.FirebaseFirestore
import com.ms.banner.BannerConfig
import com.ms.banner.Transformer
import kotlinx.android.synthetic.main.fragment_home.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.home.adapter.CustomAdapterBanner
import sembako.sayunara.android.ui.component.home.model.Banner
import sembako.sayunara.android.ui.component.product.basket.ListBasketActivity
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.ListProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.ArrayList

class HomeFragment : BaseFragment(),BannerView {


    override fun onRequestProductSuccess(productArrayList: ArrayList<Product>) {
        swipeRefresh.isRefreshing = false
        updateList(productArrayList)

    }

    override fun onRequestProductFailed(code: Int?) {

    }

    override fun onRequestSuccess(bannerArrayList: ArrayList<Banner>) {
        bannerServices.getList(this, FirebaseFirestore.getInstance())
        setupBanner(bannerArrayList)
    }

    override fun loadingIndicator(isLoading: Boolean) {

    }

    override fun onRequestFailed(code: Int?) {
        setToast("failed")
    }

    override fun setupViews() {

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = layoutManager
        bannerServices.getBanner(this, FirebaseFirestore.getInstance())

        ll_meat.setOnClickListener { intent("daging") }


        ll_drinks.setOnClickListener { intent("minuman") }

        ll_seasoning.setOnClickListener { intent("bumbu") }

        ll_fruits.setOnClickListener { intent("buah") }


        ll_basic_food.setOnClickListener { intent("sembako") }

        ll_vegetables.setOnClickListener { intent("sayuran") }


        swipeRefresh.setOnRefreshListener {
            bannerServices.getList(this, FirebaseFirestore.getInstance())
        }


        tvAllProduk.setOnClickListener {

            val intent = Intent(activity, ListBasketActivity::class.java)
            startActivity(intent)


            */
/*val intent = Intent(activity, ListProductActivity::class.java)
            intent.putExtra("keyword","null")
            intent.putExtra("type","all")
            startActivity(intent)*//*

        }
    }


    fun intent(type: String) {
        val intent = Intent(activity, ListProductActivity::class.java)
        intent.putExtra("keyword", type)
        intent.putExtra("type","null")
        startActivity(intent)

    }

    var images : ArrayList<String?> = ArrayList()
    val bannerServices = HomeServices()
    private lateinit var productAdapter: ProductAdapter
    private val mIndicatorSelectedResId = R.drawable.indicator_select
    private val mIndicatorUnselectedResId = R.drawable.indicator_unselect
    private var lastPosition = 0
    private val indicatorImages = ArrayList<ImageView>()

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


    override fun onStop() {
        super.onStop()
        if (layout_banner != null && layout_banner.isPrepare && layout_banner.isStart) {
            layout_banner.stopAutoPlay()
        }
    }

    @Override
    override fun onStart() {
        super.onStart()
        if (layout_banner != null && layout_banner.isPrepare && !layout_banner.isStart) {
            layout_banner.startAutoPlay()
        }
    }

    private fun setupBanner(bannerArrayList: ArrayList<Banner>){


        initIndicator(bannerArrayList)
        layout_banner.setAutoPlay(true)
                .setOffscreenPageLimit(bannerArrayList.size)
                .setPages(bannerArrayList, CustomAdapterBanner())
                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setBannerAnimation(Transformer.Scale)
                .start()
        layout_banner.setDelayTime(5000)
        layout_banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                indicatorImages[(lastPosition + bannerArrayList.size) % bannerArrayList.size].setImageResource(mIndicatorUnselectedResId)
                indicatorImages[(position + bannerArrayList.size) % bannerArrayList.size].setImageResource(mIndicatorSelectedResId)
                lastPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        Handler().postDelayed({
            //progressBar.visibility = View.GONE
            layout_banner.visibility = View.VISIBLE
        }, 2000)


    }


    private fun updateList(productArrayList: ArrayList<Product>) {
        productAdapter = ProductAdapter(activity,true)
        productAdapter.data = productArrayList
        recyclerView.adapter = productAdapter

        productAdapter.actionQuestion { _, position ->
            val product = productArrayList[position]
            val intent = Intent(activity, DetailProductActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

    }

    private fun initIndicator(productArrayList: ArrayList<Banner>) {
        for (i in productArrayList.indices) {
            val imageView = ImageView(activity)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            val customParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            customParams.leftMargin = 2
            customParams.rightMargin = 2
            if (i == 0) {
                imageView.setImageResource(mIndicatorSelectedResId)
            } else {
                imageView.setImageResource(mIndicatorUnselectedResId)
            }
            indicatorImages.add(imageView)
            indicator.addView(imageView,customParams)
        }
    }

}
*/
