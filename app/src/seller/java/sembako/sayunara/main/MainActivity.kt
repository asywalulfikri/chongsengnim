package sembako.sayunara.main

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.seller.activity_main.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFragment
import sembako.sayunara.android.ui.component.account.profile.ProfileFragment
import sembako.sayunara.android.ui.component.mobile.SayunaraMobilActivity
import sembako.sayunara.home.HomeFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabSelected(0, getString(R.string.text_menu_home))

        tab_home.setOnClickListener {
            tabSelected(0, getString(R.string.text_menu_home))
        }


        tab_article.setOnClickListener {
            tabSelected(3, getString(R.string.text_menu_article))
        }

        tab_account.setOnClickListener {
            tabSelected(5, getString(R.string.text_menu_account))
        }




    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }


    private fun tabSelected(position: Int, type: String?) {
        if (!TextUtils.isEmpty(type)) {
            when (type) {
                getString(R.string.text_menu_home) -> {
                    displayFragment(HomeFragment(), R.id.fragment_container)
                }

                getString(R.string.text_menu_article) -> {
                    comingSoon()
                    //displayFragment(ArticleFragment(), R.id.fragment_container)
                }


                getString(R.string.text_menu_account) -> {

                    if (isLogin())
                    {
                        displayFragment(ProfileFragment(), R.id.fragment_container)
                    }
                    else
                    {
                        displayFragment(LoginFragment(), R.id.fragment_container)
                    }
                }
            }
            updateTabView(position)
           // mTabPosition = position
        }
    }

    private fun updateTabView(position: Int) {

        iv_tab_home.setColorFilter(ContextCompat.getColor(activity,R.color.grey_400), PorterDuff.Mode.SRC_ATOP)
        tv_tab_home.setTextColor(ContextCompat.getColor(this, R.color.grey_500))
        tv_tab_home.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        iv_tab_article.setColorFilter(ContextCompat.getColor(activity,R.color.grey_400), PorterDuff.Mode.SRC_ATOP)
        tv_tab_article.setTextColor(ContextCompat.getColor(this, R.color.grey_500))
        tv_tab_article.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        iv_tab_account.setColorFilter(ContextCompat.getColor(activity,R.color.grey_400), PorterDuff.Mode.SRC_ATOP)
        tv_tab_account.setTextColor(ContextCompat.getColor(this, R.color.grey_500))
        tv_tab_account.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)


        when (position) {
            0 -> {
                iv_tab_home.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                tv_tab_home.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv_tab_home.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            1 -> {
                iv_tab_article.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                tv_tab_article.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv_tab_article.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            else -> {
                iv_tab_account.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                tv_tab_account.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv_tab_account.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }
    }


    private fun displayFragment(fragment: Fragment?, fragmentResourceID: Int) {
        try {
            showFragment(fragment, fragmentResourceID)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            showFragmentAllowingStateLoss(fragment, fragmentResourceID)
        }
    }
}