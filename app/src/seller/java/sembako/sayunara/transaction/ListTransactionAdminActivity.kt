package sembako.sayunara.transaction

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.example.BlankFragment

class ListTransactionAdminActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabhost)

        setupToolbar(toolbar)

        // Create the object of Toolbar, ViewPager and
        // TabLayout and use “findViewById()” method*/
        var tab_toolbar = findViewById<Toolbar>(R.id.toolbar)
        var tab_viewpager = findViewById<ViewPager>(R.id.tab_viewpager)
        var tab_tablayout = findViewById<TabLayout>(R.id.tab_tablayout)

        // As we set NoActionBar as theme to this activity
        // so when we run this project then this activity doesn't
        // show title. And for this reason, we need to run
        // setSupportActionBar method
        setSupportActionBar(tab_toolbar)
        setupViewPager(tab_viewpager)

        // If we dont use setupWithViewPager() method then
        // tabs are not used or shown when activity opened
        tab_tablayout.setupWithViewPager(tab_viewpager)
    }

    // This function is used to add items in arraylist and assign
    // the adapter to view pager
    private fun setupViewPager(viewpager: ViewPager) {
        var adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(BlankFragment(), "Orderan\nMasuk")
        adapter.addFragment(BlankFragment(), "Orderan\nDi Proses")
        adapter.addFragment(BlankFragment(), "Orderan\nDi Kirim")
        adapter.addFragment(BlankFragment(), "Orderan\nDi Selesai")

        // setting adapter to view pager.
        viewpager.adapter = adapter
    }

    // This "ViewPagerAdapter" class overrides functions which are
    // necessary to get information about which item is selected
    // by user, what is title for selected item and so on.*/
    class ViewPagerAdapter : FragmentPagerAdapter {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        private var fragmentList1: ArrayList<Fragment> = ArrayList()
        private var fragmentTitleList1: ArrayList<String> = ArrayList()

        // this is a secondary constructor of ViewPagerAdapter class.
        constructor(supportFragmentManager: FragmentManager)
                : super(supportFragmentManager)

        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1[position]
        }

        // returns which item is selected from arraylist of titles.
        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1[position]
        }

        // returns the number of items present in arraylist.
        override fun getCount(): Int {
            return fragmentList1.size
        }

        // this function adds the fragment and title in 2 separate  arraylist.
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
        }
    }
}