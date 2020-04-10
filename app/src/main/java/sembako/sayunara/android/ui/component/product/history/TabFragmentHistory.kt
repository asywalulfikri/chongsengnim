package sembako.sayunara.android.ui.component.product.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.inbox.InboxFragment
import sembako.sayunara.android.ui.component.product.history.tab.RoundTabLayout

//private var mFragment: List<Fragment>? = null

class TabFragmentHistory : BaseFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_history,
                container, false)
    }
    private lateinit var mViewPager: ViewPager
    private lateinit var round_tabs: RoundTabLayout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewPager = view.findViewById(R.id.mViewPager) as ViewPager
        round_tabs = view.findViewById(R.id.round_tabs) as RoundTabLayout
        val adapter = activity?.supportFragmentManager?.let { ViewAdapter(it) }
      /*  mFragment = ArrayList()
        (mFragment as ArrayList<Fragment>).add(HistoryTransactionFragment.newInstance())
        (mFragment as ArrayList<Fragment>).add(HistoryTransactionFragment.newInstance())
        (mFragment as ArrayList<Fragment>).add(HistoryTransactionFragment.newInstance())
        (mFragment as ArrayList<Fragment>).add(HistoryTransactionFragment.newInstance())
        (mFragment as ArrayList<Fragment>).add(HistoryTransactionFragment.newInstance())*/

        mViewPager.adapter = adapter
        round_tabs.setupWithViewPager(mViewPager)

       /* if(!isLogin){
            round_tabs.visibility = View.GONE
        }*/

    }

    private inner class ViewAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            //return mFragment!![position]
            val bundle = Bundle()
            var fragment: Fragment
            var isi=String()
            fragment = Fragment()
            when (position) {
                0 -> {
                    fragment =  InboxFragment()
                    isi = "unpaid"
                }
                1 -> {
                    fragment = InboxFragment()
                    isi ="paid"
                }
                2 -> {
                    fragment = InboxFragment()
                    isi ="paid"
                }


            }
            bundle.putCharSequence("type",isi)
            fragment.arguments = bundle
            return  fragment
        }

        override fun getCount(): Int {
            //return mFragment?.size!!
            var count = 3
            /*if(!isLogin){
                count = 1
            }*/
            return count
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Order di Proses"
                1 -> "Order dikirim"
                2 -> "Order Selesai"
                else -> null
            }
        }
    }

}

