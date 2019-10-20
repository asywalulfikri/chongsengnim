package sembako.sayunara.android.screen.mainmenu

import androidx.viewpager.widget.ViewPager

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View

import com.facebook.FacebookSdk
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import kotlinx.android.synthetic.main.activity_main2.*

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import sembako.sayunara.android.R
import sembako.sayunara.android.screen.base.BaseActivity
import sembako.sayunara.android.screen.home.HomeFragment
import sembako.sayunara.android.screen.inbox.InboxFragment
import sembako.sayunara.android.screen.login.LoginFragment
import sembako.sayunara.android.screen.mainmenu.util.ViewPagerAdapter
import sembako.sayunara.android.screen.profile.ProfileFragment
import sembako.sayunara.android.screen.setting.SettingFragment
import sembako.sayunara.android.screen.signup.SignUpFragment

class MainMenuActivity:BaseActivity() {

    internal var prevMenuItem:MenuItem? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        if (isLogin) {
            val user = user
        }
        setContentView(R.layout.activity_main2)
        viewPager.offscreenPageLimit = 3

        bottom_navigation.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> {
                    rl_searchView.visibility = View.VISIBLE
                    viewPager.currentItem = 0
                }

                R.id.setting_menu -> {
                    rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 1
                }
                R.id.inbox -> {
                    rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 2
                }
                R.id.account_menu -> {
                    rl_searchView.visibility = View.GONE
                    viewPager.currentItem = 3
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

        setupViewPager(viewPager)

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
        val homeFragment = HomeFragment()
        val signUpFragmentt = SignUpFragment()
        val loginFragment = LoginFragment()
        val settingFragment = SettingFragment()
        val inboxFragment = InboxFragment()
        val profileFragment = ProfileFragment()
        adapter.addFragment(homeFragment)
        adapter.addFragment(settingFragment)
        adapter.addFragment(inboxFragment)
        if (isLogin)
        {
            adapter.addFragment(profileFragment)
        }
        else
        {
            adapter.addFragment(loginFragment)
        }

        viewPager.adapter = adapter
    }
}
