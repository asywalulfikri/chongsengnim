/*

package sembako.sayunara.android.ui.component.product.listproduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_list_product.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.ui.product.addProduct.PostProductActivity
import java.util.*

class ListProductActivity : BaseActivity() {
    private var firebaseFirestore: FirebaseFirestore? = null
    private var productArrayList = ArrayList<Product?>()
    private var productAdapter: ProductAdapter? = null

    private var type: String? = null

    //View
    private var stopload = false
    override var user: User? = null


    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)


        type = intent.getStringExtra("type")
        firebaseAuth = FirebaseAuth.getInstance()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "List Produk $type")
        firebaseFirestore = FirebaseFirestore.getInstance()


        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = mLayoutManager


        if (isLogin()) {
            user = user
            if (user!!.profile.type == Constant.UserKey.userTypeAdmin || user!!.profile.type == Constant.UserKey.userTypeMitra) {
                floating_action_button.visibility = View.VISIBLE
            } else {
                floating_action_button.visibility = View.GONE
            }
        } else {
            floating_action_button.visibility = View.GONE
        }
        floating_action_button.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, PostProductActivity::class.java)
            startActivityForResult(intent, Constant.Code.CODE_LOAD)
        })



        if (firebaseAuth.currentUser != null) {
            Log.i("usuarioLogado", "Usuario Logado")
        } else {
            Log.i("usuarioLogado", "Usuario Deslogado")
        }



        if(isLogin()){
            getList(type)
        }else{
            firebaseAuth.signInAnonymously().addOnCompleteListener(this@ListProductActivity) { task ->
                if (task.isSuccessful) {
                    getList(type)
                } else {
                    Log.i("FirebaseAuth", task.exception.toString())
                }
            }

        }
        swipe_refresh.setOnRefreshListener {
            refresh()
        }
        rl_load_more.setOnClickListener {
            getList(type)
        }
    }

    private fun refresh() {
        //mLastQueriedDocument = null
        rl_load_more!!.visibility = View.GONE
        productAdapter!!.data.clear()
        getList(type)
    }

    private fun automaticLoadMore() {
        nestedScrollView!!.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Handler().postDelayed({
                    if (!stopload) {
                        getList(type)
                    }
                }, 500)
            }
        }
    }

    private fun getList(type: String?) {
        FirebaseFirestore.setLoggingEnabled(true)
        val collectionReference = firebaseFirestore!!.collection("product")
        collectionReference.firestore.firestoreSettings
        var query: Query? = null
        var mLastQueriedDocument: DocumentSnapshot? = null
        query = if (mLastQueriedDocument != null) {
            Log.d("urlnya", "pakai ini ")
            collectionReference.whereArrayContains("type", type!!)
                    .whereEqualTo("isActive", true)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument)
        } else {
            Log.d("urlnya", "pakai ono ")
            collectionReference.whereArrayContains("type", type!!)
                    .whereEqualTo("isActive", true)
                    .limit(10)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
        }
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result!!) {
                    val product = doc.toObject(Product::class.java)
                    productArrayList.add(product)
                }
                Log.d("total", productArrayList.size.toString())
                if (mLastQueriedDocument == null) {
                    if (task.result!!.size() <= 9) {
                        rl_load_more!!.visibility = View.GONE
                        stopload = true
                    } else {
                        stopload = false
                        rl_load_more!!.visibility = View.VISIBLE
                    }
                } else {
                    if (task.result!!.size() <= 9) {
                        rl_load_more!!.visibility = View.GONE
                        stopload = true
                    } else {
                        stopload = false
                        rl_load_more!!.visibility = View.VISIBLE
                    }
                }
                swipe_refresh!!.isRefreshing = false
                updateList(productArrayList)
                if (task.result!!.size() != 0) {
                    mLastQueriedDocument = task.result!!.documents[task.result!!.size() - 1]
                }
            } else {
                Toast.makeText(activity, "gagal" + task.exception, Toast.LENGTH_SHORT).show()
                Log.d("urlnya", "Error getting documents: ", task.exception)
            }
        }
    }

    private fun updateList(historyList: ArrayList<Product?>) {
        showList()
        progress_bar!!.visibility = View.GONE
        productAdapter = ProductAdapter(this, false)
        productAdapter!!.data = historyList
        recyclerView!!.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()
        if (productAdapter!!.dataItemCount > 0) {
            ll_no_product!!.visibility = View.GONE
        } else {
            ll_no_product!!.visibility = View.VISIBLE
        }
        productAdapter!!.actionQuestion { view, position ->
            val product = historyList[position]
            val intent = Intent(activity, DetailProductActivity::class.java)
            intent.putExtra("product", product)
            startActivityForResult(intent, Constant.Code.CODE_LOAD)
        }
        automaticLoadMore()
    }

    private fun showList() {
        progress_bar!!.visibility = View.GONE
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
        if (requestCode == Constant.Code.CODE_LOAD && resultCode == Activity.RESULT_OK) {
            val load = data!!.getBooleanExtra(Constant.IntentExtra.isLoad, false)
            if (load) {
                refresh()
            }
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
}
*/
