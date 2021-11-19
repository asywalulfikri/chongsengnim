/*
package sembako.sayunara.product.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.ArrayList

class ListProductActivity : BaseActivity(),
    ProductAdapter.OnClickListener {
    protected var firebaseFirestore: FirebaseFirestore? = null
    protected var productArrayList = ArrayList<Product?>()
    protected var productAdapter: ProductAdapter? = null
    protected var layout_progress: LinearLayout? = null
    protected var layout_empty: RelativeLayout? = null
    protected var swipe_refresh: SwipeRefreshLayout? = null
    protected var mLastQueriedDocument: DocumentSnapshot? = null
    protected var nestedScrollView: NestedScrollView? = null
    protected var keyword: String? = null
    protected var rl_load_more: RelativeLayout? = null

    //View
    private var recyclerView: RecyclerView? = null
    private var stopload = false
    private var type: String? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var etSearch: AppCompatEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)
        if (intent.hasExtra("keyword")) {
            keyword = intent.getStringExtra("keyword")
        }
        type = intent.getStringExtra("type")
        firebaseAuth = FirebaseAuth.getInstance()
        etSearch = findViewById(R.id.etSearchView)
        recyclerView = findViewById(R.id.recyclerView)
        layout_progress = findViewById(R.id.layout_progress)
        layout_empty = findViewById(R.id.layout_empty)
        swipe_refresh = findViewById(R.id.swipeRefresh)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        rl_load_more = findViewById(R.id.rlLoadMore)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { onBackPressed() }

        // setupToolbar(toolbar,"List Produk "+ type);
        firebaseFirestore = FirebaseFirestore.getInstance()
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(mLayoutManager)


        // firebaseAuth.signInAnonymously();
        if (!isLogin()) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(
                this@ListProductActivity
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                } else {
                }
            }
        }
        getList(keyword)
        swipe_refresh.setOnRefreshListener(OnRefreshListener { refresh() })
        rl_load_more.setOnClickListener(View.OnClickListener { getList(keyword) })
        etSearch.setOnEditorActionListener(OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearch.getText().toString() !== "") {
                    mLastQueriedDocument = null
                    rl_load_more.setVisibility(View.GONE)
                    productAdapter!!.data.clear()
                    val text = etSearch.getText().toString()
                    val text1 = text.split(" ").toTypedArray()
                    hideKeyboard()
                    val intent = Intent(this, SearcListProductActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("keyword", text1[0].toLowerCase())
                    startActivityForResult(intent, 11)
                }
                return@setOnEditorActionListener true
            }
            false
        })
    }

    fun refresh() {
        mLastQueriedDocument = null
        rl_load_more!!.visibility = View.GONE
        productAdapter!!.data.clear()
        getList(keyword)
    }

    fun automaticLoadMore() {
        nestedScrollView!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Handler().postDelayed({
                    if (stopload == false) {
                        getList(keyword)
                    }
                }, 500)
            }
        })
    }

    fun getList(keyword: String?) {
        FirebaseFirestore.setLoggingEnabled(true)
        val collectionReference =
            firebaseFirestore!!.collection(Constant.Collection.COLLECTION_PRODUCT)
        collectionReference.firestore.firestoreSettings
        var query: Query? = null
        query = if (mLastQueriedDocument != null) {
            Log.d("urlnya", "pakai ini ")
            if (type == "all") {
                collectionReference.whereEqualTo("isActive", true)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument)
            } else {
                collectionReference.whereArrayContains("type", keyword!!)
                    .whereEqualTo("isActive", true)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument)
            }
        } else {
            if (type == "all") {
                collectionReference.whereEqualTo("isActive", true)
                    .limit(10)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
            } else {
                collectionReference.whereArrayContains("type", keyword!!)
                    .whereEqualTo("isActive", true)
                    .limit(10)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
            }
        }
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result) {
                    val product = doc.toObject(Product::class.java)
                    productArrayList.add(product)
                }
                Log.d("total", productArrayList.size.toString())
                if (mLastQueriedDocument == null) {
                    if (task.result.size() <= 9) {
                        rl_load_more!!.visibility = View.GONE
                        stopload = true
                    } else {
                        stopload = false
                        rl_load_more!!.visibility = View.VISIBLE
                    }
                } else {
                    if (task.result.size() <= 9) {
                        rl_load_more!!.visibility = View.GONE
                        stopload = true
                    } else {
                        stopload = false
                        rl_load_more!!.visibility = View.VISIBLE
                    }
                }
                swipe_refresh!!.isRefreshing = false
                updateList(productArrayList)
                if (task.result.size() != 0) {
                    mLastQueriedDocument = task.result.documents[task.result.size() - 1]
                }
            } else {
                Toast.makeText(activity, "gagal" + task.exception, Toast.LENGTH_SHORT).show()
                Log.d("urlnya", "Error getting documents: ", task.exception)
            }
        }
    }

    private fun updateList(historyList: ArrayList<Product?>) {
        showList()
        layout_progress!!.visibility = View.GONE
        productAdapter = ProductAdapter(this, false, false, true)
        productAdapter!!.data = historyList
        recyclerView!!.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()
        if (productAdapter!!.dataItemCount > 0) {
            layout_empty!!.visibility = View.GONE
        } else {
            layout_empty!!.visibility = View.VISIBLE
        }
        automaticLoadMore()
    }

    fun showList() {
        layout_progress!!.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.Code.CODE_LOAD && resultCode == RESULT_OK) {
            val load = data!!.getBooleanExtra(Constant.IntentExtra.isLoad, false)
            if (load) {
                refresh()
            }
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
    override fun onClickDetail(position: Int, product: Product) {
        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, Constant.Code.CODE_LOAD)
    }
}*/
