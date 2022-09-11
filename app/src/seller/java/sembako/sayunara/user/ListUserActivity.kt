package sembako.sayunara.user

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
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sembako.sayunara.android.ui.component.account.login.data.model.User
import java.util.*


class ListUserActivity : BaseActivity(),UserView.List, UserAdapter.OnClickListener{

    private var mAdapter = UserAdapter()
    val services = UserServices()
    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false
    private var firstLoad = true
    val arrayList: ArrayList<User> = ArrayList()

    private var keyword : String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar(toolbar)

        if(intent.hasExtra("keyword")){
            keyword = intent.getStringExtra("keyword")
        }

        setupView()
        loadList()

    }

    private fun setupView(){
        ivBack.visibility = View.INVISIBLE

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 3)

        recyclerView.run {
            layoutManager = mLayoutManager
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }


        etSearchView.hint = "Search User by email / phone / username"
        etSearchView.setOnEditorActionListener(TextView.OnEditorActionListener setOnEditorActionListener@{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchView.text.toString() !== "") {

                    val text: String = etSearchView.text.toString()
                    hideKeyboard()
                    val text1 = text.split(" ").toTypedArray()
                    keyword = text1[0].lowercase(Locale.getDefault())

                    mLastQueriedDocument = null
                    firstLoad = true
                    arrayList.clear()
                    loadList()


                }
                return@setOnEditorActionListener true
            }
            false
        })


    }

    private fun refresh(){
        mLastQueriedDocument = null
        firstLoad = true
        arrayList.clear()

        loadList()
    }

    private fun loadList(){
        if(keyword!=null){
            services.getListSearchUser(this,mLastQueriedDocument,keyword.toString())
        }else{
            services.getList(this,mLastQueriedDocument)
        }
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
            val user = doc.toObject(User::class.java)
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

                    textViewEmptyList.text = getString(R.string.text_user_not_found)
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
    private fun updateList(arrayList: ArrayList<User>) {
        firstLoad = false
        mAdapter.setItems(arrayList,this)
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

    override fun onClickDetail(position: Int, user: User) {
        val intent = Intent(this,UserDetailActivity::class.java)
        intent.putExtra("user",user)
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
