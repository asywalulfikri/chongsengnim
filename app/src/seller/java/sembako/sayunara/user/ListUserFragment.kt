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
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.recyclerView
import kotlinx.android.synthetic.main.activity_list.swipeRefresh
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import androidx.core.widget.NestedScrollView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_search.*
import sembako.sayunara.android.R
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import java.util.*


class ListUserFragment : BaseFragment(),UserView.List, UserAdapter.OnClickListener{

    private var mAdapter = UserAdapter()
    val services = UserServices()
    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false
    private var firstLoad = true
    val arrayList: ArrayList<User> = ArrayList()
    private var type : String?  = null
    private var isSearch = false
    private var keyword = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val message = arguments
        if (message != null) {
            type = message.getString("key").toString()
            if(type!="user"){
                rlToolbar.visibility = View.GONE
            }
        }

        setupView()
        loadList()
    }

    private fun setupView(){
        ivBack.visibility = View.GONE
        toolbar.visibility = View.GONE
        etSearchView.hint = "Search User by email / phone / username"

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 3)

        recyclerView.run {
            layoutManager = mLayoutManager
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
            refresh()
        }


        etSearchView.setOnEditorActionListener(OnEditorActionListener setOnEditorActionListener@{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchView.text.toString() !== "") {

                    val text: String = etSearchView.text.toString()
                    hideKeyboard()
                    val text1 = text.split(" ").toTypedArray()
                    keyword = text1[0].lowercase(Locale.getDefault())

                    val intent = Intent(activity,ListUserActivity::class.java)
                    intent.putExtra("keyword",keyword)
                    startForResult.launch(intent)


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
        services.getListByType(this,mLastQueriedDocument,type.toString())
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

                    if(!isCustomer()){
                        if(getSeller()){
                            textViewEmptyList.text = getString(R.string.text_empty_product_admin)
                        }else{
                            textViewEmptyList.text = getString(R.string.text_empty_product)
                        }
                    }else{
                        textViewEmptyList.text = getString(R.string.text_empty_product)
                    }
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
        val intent = Intent(activity,UserDetailActivity::class.java)
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
