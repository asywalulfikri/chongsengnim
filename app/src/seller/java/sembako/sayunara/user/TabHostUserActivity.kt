package sembako.sayunara.user

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tabhost.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity

class TabHostUserActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabhost)

        setupToolbar(toolbar)
        setupViewPager(tab_viewpager)
        tab_tablayout.setupWithViewPager(tab_viewpager)
    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(fragment("user"), "User",)
        adapter.addFragment(fragment("seller"), "Seller",)
        adapter.addFragment(fragment("admin"), "Admin",)
        adapter.addFragment(fragment("superadmin"), "Super\nAdmin",)


        viewpager.adapter = adapter
    }

    fun fragment(key : String) : Fragment{
        val bundle = Bundle();
        bundle.putString("key", key);
        val fragment1 = ListUserFragment()
        fragment1.arguments = bundle
        return fragment1

    }

    class ViewPagerAdapter
        (supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager) {


        private var fragmentList1: ArrayList<Fragment> = ArrayList()
        private var fragmentTitleList1: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList1[position]
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1[position]
        }

        override fun getCount(): Int {
            return fragmentList1.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}