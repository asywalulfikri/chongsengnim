package sembako.sayunara.android.ui.component.banner

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.recyclerView
import kotlinx.android.synthetic.main.activity_list.swipeRefresh
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import sembako.sayunara.android.ui.base.BaseActivity
import androidx.core.widget.NestedScrollView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_search.*
import sembako.sayunara.android.R
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import java.util.*


class ListBannerActivity : BaseActivity(),BannerView.List, BannerAdapter.OnClickListener{

    private var mAdapter = BannerAdapter()
    val services = BannerServices()
    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false
    private var firstLoad = true
    val arrayList: ArrayList<Banner> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar(toolbar)
        setupView()
        loadList()

    }

    private fun setupView(){
        ivBack.visibility = View.INVISIBLE
        rl_searchView.visibility = View.GONE

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }


        if(!isCustomer()){
            if(getAdmin()||getSuperAdmin()){
                fabAddData.visibility = View.VISIBLE
            }
        }


        fabAddData.setOnClickListener {
            val intent = Intent(activity, PostBannerActivity::class.java)
            startForResult.launch(intent)
        }

    }

    private fun refresh(){
        mLastQueriedDocument = null
        firstLoad = true
        arrayList.clear()

        loadList()
    }

    private fun loadList(){
        services.getList(this,mLastQueriedDocument,getUsers)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadingIndicator(isLoading: Boolean) {
        if(isLoading){
            if(firstLoad){
                layout_progress.visibility = View.VISIBLE
            }
        }else{
            layout_progress.visibility = View.GONE
        }
    }

    override fun onRequestSuccess(querySnapshot: QuerySnapshot) {

        for (doc in querySnapshot) {
            val user = doc.toObject(Banner::class.java)
            Log.d("response",Gson().toJson(user))
            arrayList.add(user)
        }

        if (mLastQueriedDocument == null) {
            when {
                querySnapshot.size() in 1..9 -> {
                    rlLoadMore.visibility = View.GONE
                    layout_empty.visibility = View.GONE
                    stopload = true
                }
                querySnapshot.size()==0 -> {
                    layout_empty.visibility = View.VISIBLE

                    textViewEmptyList.text = getString(R.string.text_banner_empty)
                    rlLoadMore.visibility = View.GONE
                    stopload = true
                }
                else -> {
                    stopload = false
                    rlLoadMore.visibility = View.VISIBLE
                }
            }
        } else {
            if (querySnapshot.size() <= 9) {
                rlLoadMore.visibility = View.GONE
                layout_empty.visibility = View.GONE
                stopload = true
            } else {
                stopload = false
                rlLoadMore.visibility = View.VISIBLE
            }
        }


        swipeRefresh.isRefreshing = false
        updateList(arrayList)

        if (querySnapshot.size() != 0) {
            mLastQueriedDocument = querySnapshot.documents[querySnapshot.size() - 1]
        }
    }




    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(arrayList: ArrayList<Banner>) {
        firstLoad = false
        mAdapter.setItems(activity,arrayList,this)
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        automaticLoadMore()
    }


    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    private fun automaticLoadMore() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                Handler(Looper.getMainLooper()).postDelayed({
                    if (!stopload) {
                        loadList()
                    }
                }, 300)
            }
        }
    }

    override fun onClickDetail(position: Int, banner: Banner) {
        val intent = Intent(this,BannerDetailActivity::class.java)
        intent.putExtra("banner",banner)
        startForResult.launch(intent)

    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val load = data?.getBooleanExtra("load", false)
            if (load==true) {
                refresh()
            }
        }
    }

}
