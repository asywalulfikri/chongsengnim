/*
package sembako.sayunara.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_list_product.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar_search.*
import sembako.sayunara.adapter.UserAdapter
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity
import java.util.*

class ListUserActivity : AppCompatActivity(), UserAdapter.OnClickListener {

    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var productArrayList = ArrayList<User?>()
    private var userAdapter: UserAdapter? = null

    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)

        ivBack.visibility = View.VISIBLE
        etSearchView.hint = "Cari User By email / Phone"
        ivBack.setOnClickListener {
            onBackPressed()
        }

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 3)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = mLayoutManager

        list

        swipeRefresh.setOnRefreshListener {
            refresh()
        }
        rlLoadMore.setOnClickListener {
            list
        }
        etSearchView.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchView.text.toString() !== "") {
                    mLastQueriedDocument = null
                    rlLoadMore.visibility = View.GONE
                    userAdapter!!.data.clear()
                    val text = etSearchView.text.toString()
                    val text1 = text.split(" ").toTypedArray()
                    //hideKeyboard();
                    val intent = Intent(this, SearcListProductActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("keyword", text1[0].lowercase(Locale.getDefault()))
                    startActivityForResult(intent, 11)
                }
                true
            }
            false
        }

    }

    private fun refresh() {
        mLastQueriedDocument = null
        rlLoadMore!!.visibility = View.GONE
        userAdapter!!.data.clear()
        list
    }

    private fun automaticLoadMore() {
        nestedScrollView!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Handler().postDelayed({
                    if (!stopload) {
                        list
                    }
                }, 500)
            }
        })
    }

    val list: Unit
        get() {
            FirebaseFirestore.setLoggingEnabled(true)
            val collectionReference = firebaseFirestore.collection(Constant.Collection.COLLECTION_USER)
            collectionReference.firestore.firestoreSettings
            var query: Query? = null

            query = if (mLastQueriedDocument != null) {
                Log.d("urlnya", "pakai ini ")
                collectionReference
                    .whereEqualTo("profile.active", true)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument)
            } else {
                collectionReference
                    .whereEqualTo("profile.active", true)
                    .limit(10)
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
            }

            query.get().addOnCompleteListener {
                fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful) {
                        Log.d("total1", task.result.toString() + "--")
                        for (doc in task.result) {
                            val product = doc.toObject(
                                User::class.java
                            )
                            productArrayList.add(product)
                        }
                        Log.d("total", productArrayList.size.toString())
                        if (mLastQueriedDocument == null) {
                            if (task.result.size() <= 9) {
                                rlLoadMore.setVisibility(View.GONE)
                                stopload = true
                            } else {
                                stopload = false
                                rlLoadMore.setVisibility(View.VISIBLE)
                            }
                        } else {
                            if (task.result.size() <= 9) {
                                rlLoadMore.setVisibility(View.GONE)
                                stopload = true
                            } else {
                                stopload = false
                                rlLoadMore.setVisibility(View.VISIBLE)
                            }
                        }
                        swipeRefresh.setRefreshing(false)
                        updateList(productArrayList)
                        if (task.result.size() != 0) {
                            mLastQueriedDocument = task.result.documents[task.result.size() - 1]
                        }
                    } else {
                        // Toast.makeText(this,"gagal"+ task.getException(),Toast.LENGTH_SHORT).show();
                        Log.d("urlnya", "Error getting documents: ", task.exception)
                    }
                }
            }

        }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(arrayList: ArrayList<User?>) {
        showList()
        layout_progress?.visibility = View.GONE
        userAdapter = UserAdapter(applicationContext, this)
        userAdapter?.data = arrayList
        recyclerView?.adapter = userAdapter
        userAdapter?.notifyDataSetChanged()
        if (userAdapter?.dataItemCount!! > 0) {
            layout_empty?.visibility = View.GONE
        } else {
            layout_empty?.visibility = View.VISIBLE
            pbText?.text = "User tidak di temukan"
        }
        automaticLoadMore()
    }

    private fun showList() {
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
    override fun onClickDetail(position: Int, user: User) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }
}*/
