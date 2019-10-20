package sembako.sayunara.android.screen.mainmenu.widget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import java.util.ArrayList


class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

}
