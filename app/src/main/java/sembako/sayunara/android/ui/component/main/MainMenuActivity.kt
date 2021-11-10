package sembako.sayunara.android.ui.component.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Html.ImageGetter
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.yayandroid.locationmanager.configuration.Configurations
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.rl_searchView
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.android.synthetic.main.toolbar_search.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFragment
import sembako.sayunara.android.ui.component.account.profile.ProfileFragment
import sembako.sayunara.android.ui.component.account.register.LocationBaseActivity
import sembako.sayunara.android.ui.component.basket.BasketFragment
import sembako.sayunara.android.ui.component.main.model.Item
import sembako.sayunara.android.ui.component.main.model.ItemWrapper
import sembako.sayunara.android.ui.component.main.model.Price
import sembako.sayunara.android.ui.component.main.model.PriceWrapper
import sembako.sayunara.android.ui.component.main.util.ViewPagerAdapter
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import sembako.sayunara.android.ui.example.BlankFragment
import java.io.IOException
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainMenuActivity : LocationBaseActivity(), ImageGetter {

    internal var prevMenuItem:MenuItem? = null
    var list: Array<String?>? =null
    var arrayMarquee: ArrayList<Item>? = null
    var wrapper: PriceWrapper? = null
    var arrayLokasi = ArrayList<String>()

    override val locationConfiguration: LocationConfiguration?
        get() = Configurations.defaultConfiguration("Gimme the permission!", "Would you mind to turn GPS on?")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rating()
        location

        if (isLogin()) {
            val user = getUsers
        }
        viewPager.offscreenPageLimit = 3

        bottom_navigation.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> {
                    //coordinator.visibility =View.VISIBLE
                    rl_toolbar.visibility = View.VISIBLE
                   // rl_searchView.visibility = View.VISIBLE
                    viewPager.currentItem = 0
                }

                R.id.basket_menu -> {
                   // coordinator.visibility =View.GONE
                    rl_toolbar.visibility = View.VISIBLE
                   // rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 1
                }
                R.id.mobil_menu -> {
                    // coordinator.visibility =View.GONE
                    rl_toolbar.visibility = View.VISIBLE
                    // rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 2
                }
                R.id.article -> {
                   // coordinator.visibility =View.GONE
                    rl_toolbar.visibility = View.VISIBLE
                    rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 3
                }
                R.id.account_menu -> {
                    //coordinator.visibility =View.GONE
                    rl_toolbar.visibility = View.GONE
                    //rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 4
                }
            }
            false
        }

        // viewPager.disableScroll(true)
        viewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position:Int, positionOffset:Float, positionOffsetPixels:Int) {

            }

            override fun onPageSelected(position:Int) {
                if (prevMenuItem != null)
                {
                    prevMenuItem!!.isChecked = false
                }
                else
                {
                    bottom_navigation.menu.getItem(0).isChecked = false
                }
                Log.d("page", "onPageSelected: $position")
                bottom_navigation.menu.getItem(position).isChecked = true
                prevMenuItem = bottom_navigation.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state:Int) {

            }
        })

        ivChat.setOnClickListener {
            try {
                val packageManager = packageManager
                val i = Intent(Intent.ACTION_VIEW)

                val url = "https://api.whatsapp.com/send?phone=" + "6281293239009" + "&text=" + URLEncoder.encode("", "UTF-8")
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i)
                } else {
                    // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }
            } catch (e: Exception) {
                Log.e("ERROR WHATSAPP", e.toString())
                // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }
        }

        setupViewPager(viewPager)

        etSearchView.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
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
        })

    }

    fun printHashKey() {
        try
        {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures)
            {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("HASH", "printHashKey() Hash Key: $hashKey")
            }
        }
        catch (e:NoSuchAlgorithmException) {
            Log.i("HASH", "printHashKey()", e)
        }
        catch (e:Exception) {
            Log.i("HASH", "printHashKey()", e)
        }

    }


    private fun setupViewPager(viewPager:ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val homeFragment = BlankFragment()
        val loginFragment = LoginFragment()
        val basketFragment = BasketFragment()
       // val settingFragment =  TabFragmentHistory()
        //val inboxFragment = InboxFragment()
        val profileFragment = ProfileFragment()
        adapter.addFragment(homeFragment)
        adapter.addFragment(basketFragment)
        //adapter.addFragment(settingFragment)
        //adapter.addFragment(inboxFragment)
        if (isLogin())
        {
            adapter.addFragment(profileFragment)
        }
        else
        {
            adapter.addFragment(loginFragment)
        }

        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
       dialogExit()
    }

    override fun onLocationChanged(location: Location) {
        if(isNetworkAvailable()){
            setLocation(location)
        }
    }

    override fun onLocationFailed(type: Int) {

    }


    private fun getMarquee(list: ArrayList<Item>): String {
        val marque: String
        val sb = StringBuilder()
        val length = list.size
        for (i in 0 until length) {
            val itemList = list[i]
            val name = itemList.name
            val itemName = name.substring(0, name.lastIndexOf("("))
            val satuan = name.substring(name.lastIndexOf("/"), name.lastIndexOf(")"))
            var hMin1 = itemList.HMin1
            var priceImage: String
            if (hMin1.contains("(")) {
                priceImage = if (hMin1.contains("-")) {
                    " <img src ='down.png'> "
                } else {
                    " <img src ='up.png'> "
                }
                hMin1 = hMin1.substring(hMin1.lastIndexOf(" ") + 1, hMin1.length)
            } else {
                priceImage = ""
            }
            val code = "$priceImage$itemName Rp. $hMin1$satuan"
            sb.append("&nbsp;&nbsp;&nbsp;$code&nbsp;&nbsp;&nbsp;")
            if (i != length - 1) {
                sb.append(" " + "  |  " + " ")
            }
        }
        marque = sb.toString()
        marquee.text = Html.fromHtml(marque, this, null)
        marquee.textSize = 12f
        marquee.isSelected = true
        return marque
    }

    private fun updatePrice(wrapper: PriceWrapper) {
        list = arrayOfNulls(35)
        arrayMarquee = ArrayList<Item>()
        var place1 = ""
        var place2 = ""
        for (i in 0 until wrapper.list.size) {
            val price: Price = wrapper.list[i]
            place1 = price.lokasi
            if (place1 != place2) {
                place2 = place1
                arrayLokasi.add(place2)
            }
            if (place1 == "Harga Nasional") {
                spinner.text = "Harga Nasional"
                val item = Item()
                item.name = price.name
                item.HMin1 = price.HMin1
                item.HMin2 = price.HMin2
                arrayMarquee!!.add(item)
                val get: String = getMarquee(arrayMarquee!!)
            }
        }
        layout_price.visibility = View.VISIBLE
    }

    override fun getDrawable(source: String): Drawable? {
        var id = 0
        if (source == "down.png") {
            id = R.drawable.down_price
        }
        if (source == "up.png") {
            id = R.drawable.up_price
        }
        val d = LevelListDrawable()
        val empty = resources.getDrawable(id)
        d.addLevel(0, 0, empty)
        d.setBounds(0, 0, empty.intrinsicWidth, empty.intrinsicHeight)
        return d
    }

    @Throws(JSONException::class)
    fun priceParse(stringJson: String?): PriceWrapper? {
        var wrapper: PriceWrapper? = null
        val itemWrapper: ItemWrapper
        if (stringJson != null) {
            val jsonArray = JSONArray(stringJson)
            val size = jsonArray.length()
            if (size > 0) {
                wrapper = PriceWrapper()
                itemWrapper = ItemWrapper()
                wrapper.list = ArrayList<Price>()
                itemWrapper.list = ArrayList<Item>()
                for (i in 0 until size) {
                    val `object` = jsonArray.getJSONObject(i)
                    val komoditas = `object`.getJSONObject("komoditas")
                    val price = Price()
                    price.lokasi = `object`.getString("lokasi")
                    price.name = komoditas.getString("nama")
                    price.HMin1 = komoditas.getString("HMin1")
                    price.HMin2 = komoditas.getString("HMin2")
                    wrapper.list.add(price)
                }
            }
        }
        return wrapper
    }


    override fun onResume() {
        super.onResume()
    }

}
