package sembako.sayunara.android.ui.component.product.listproduct

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.recyclerView
import kotlinx.android.synthetic.main.activity_list.swipeRefresh
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import java.util.ArrayList
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_search.*
import sembako.sayunara.android.R
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_list_detail_basket.*
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.editProduct.PostProductActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter2
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.net.URLEncoder


class ListProductActivity2 : BaseActivity(),ProductView.List, ProductAdapter2.OnClickListener{

    private var mAdapter = ProductAdapter2()
    val services = ProductServices()
    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false
    private var firstLoad = true
    val arrayList: ArrayList<Product> = ArrayList()
    private val userList:  ArrayList<User?> = ArrayList()
    private var querySnapshot: QuerySnapshot? =null

    private var type : String?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar(toolbar)

        if(intent.hasExtra("type")){
            type = intent.getStringExtra("type")
        }

        if (!isLogin()) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(activity) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    //success
                } else {
                    //failed
                }
            }
        }
        setupView()


        if(type!=null){
            loadListByType()
            Log.d("loadByType",type.toString()+"--")
        }else{
            loadListGeneral()
        }

    }

    private fun setupView(){
        ivBack.visibility = View.INVISIBLE

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)

        recyclerView.run {
            layoutManager = mLayoutManager
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }


        swipeRefresh.setOnRefreshListener {
            refresh()
        }

        if(!isCustomer()){
            if(getSeller()){
                fabAddData.visibility = View.VISIBLE
            }
        }


        fabAddData.setOnClickListener {
            val intent = Intent(activity, PostProductActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun refresh(){
        mLastQueriedDocument = null
        firstLoad = true
        arrayList.clear()

        if(type!=null){
            loadListByType()
        }else{
            loadListGeneral()
        }
    }

    private fun loadListGeneral(){
        services.getListGeneral(this,getUsers,mLastQueriedDocument)
    }

    private fun loadListByType(){
        services.getListByType(this,getUsers,mLastQueriedDocument,type.toString())
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
        this.querySnapshot = querySnapshot
        for (doc in querySnapshot) {
            val articles = doc.toObject(Product::class.java)
            Log.d("response",Gson().toJson(articles))
            arrayList.add(articles)
        }

        if(mLastQueriedDocument==null){
            if(arrayList.size==0){
                loadingIndicator(false)
                setupData()
                swipeRefresh.isRefreshing = false
                updateList(arrayList)
            }else{
                getProfilePerList()
            }

        }else{
            getProfilePerList()
        }

        //swipeRefresh.isRefreshing = false
       // updateList(arrayList)

        if (querySnapshot.size() != 0) {
            mLastQueriedDocument = querySnapshot.documents[querySnapshot.size() - 1]
        }


    }


    private fun setupData(){
        if (mLastQueriedDocument == null) {
            when {
                querySnapshot?.size() in 1..9 -> {
                    rlLoadMore.visibility = View.GONE
                    layout_empty.visibility = View.GONE
                    stopload = true
                }
                querySnapshot?.size()==0 -> {
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
            if (querySnapshot?.size()!! <= 9) {
                rlLoadMore.visibility = View.GONE
                layout_empty.visibility = View.GONE
                stopload = true
            } else {
                stopload = false
                rlLoadMore.visibility = View.VISIBLE
            }
        }
    }





    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(arrayList: ArrayList<Product>) {
        firstLoad = false
        mAdapter.setItems(activity,arrayList,userList,this,false)
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        automaticLoadMore()
    }


    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStatusChange(position : Int,isActive : String,active : Boolean,isPublish : String, publish : Boolean) {
        if(isActive=="active"){
            if(getSeller()||getSuperAdmin()||getAdmin()){

                if(!active){
                    Toast.makeText(activity, "Produk Telah di Hapus", Toast.LENGTH_LONG).show()
                    arrayList.removeAt(position)
                }else{
                    if(publish){
                        Toast.makeText(activity, "Produk Telah di Publish", Toast.LENGTH_LONG).show()
                        arrayList[position].status?.publish = publish
                    }else{
                        Toast.makeText(activity, "Produk Telah di UnPublish", Toast.LENGTH_LONG).show()
                        arrayList[position].status?.publish = publish
                    }
                }

                if(arrayList.size==0){
                    layout_empty.visibility = View.VISIBLE
                }
            }else{
                arrayList[position].status?.active = active
            }
        }else if(isActive=="draft"){
            arrayList[position].status?.draft = active
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onClickDetail(position: Int, product: Product) {
        val intent = Intent(this,DetailProductActivity::class.java)
        intent.putExtra("product",product)
        startActivity(intent)
    }

    override fun onActionClick(position: Int, product: Product) {
        showDialogAction(position,product)
    }


    private fun showDialogAction(position: Int,product: Product){

        val alertDialog = AlertDialog.Builder(activity)

        val inflater     = LayoutInflater.from(activity)
        val view         = inflater.inflate(R.layout.activity_action_dialog, null)
        val tvTitle      = view.findViewById<TextView>(R.id.tvTitle)
        val btnDelete    = view.findViewById<Button>(R.id.btnDelete)
        val btnEdit      = view.findViewById<Button>(R.id.btnEdit)
        val btnPublish   = view.findViewById<Button>(R.id.btnPublish)
        val btnUnPublish = view.findViewById<Button>(R.id.btnUnPublish)


        if(getSeller()||getSuperAdmin()||getAdmin()){

            if(getAdmin()){
                btnEdit.visibility = View.GONE
            }else{
                btnEdit.visibility = View.VISIBLE
            }
            btnDelete.visibility = View.VISIBLE

            if(product.status?.publish==true){
                btnUnPublish.visibility = View.VISIBLE
            }else{
                if(product.status?.draft==true){
                    btnPublish.visibility = View.GONE
                }else{
                    btnPublish.visibility = View.VISIBLE
                }
            }

        }

        tvTitle.text = getString(R.string.text_choose)
        alertDialog.setView(view)
        val ad: AlertDialog? = alertDialog.show()


        btnEdit.setOnClickListener {
            moveToEdit(product)
            ad?.dismiss()
        }

        btnPublish.setOnClickListener {
            editStatus(position,"active",true,"publish", true,product)
            ad?.dismiss()
        }

        btnUnPublish.setOnClickListener {
            editStatus(position,"active",true,"publish",false,product)
            ad?.dismiss()
        }


        btnDelete.setOnClickListener {
            dialog(position,product)
            ad?.dismiss()
        }
    }

    private fun moveToEdit(product: Product){
        val intent = Intent(this, PostProductActivity::class.java)
        intent.putExtra("product",product)
        intent.putExtra("edit",true)
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

    private fun editStatus(position : Int,isActive : String, active: Boolean ,isPublish : String, publish : Boolean, product: Product) {
        services.editStatus(position, this, isActive,active, isPublish,publish, product.id.toString())
    }

    private fun dialog(position: Int, product: Product){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete))
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                if(getSeller()||getAdmin()){
                    editStatus(position,"active",false,"publish",false,product)
                }else{
                    deleteProduct(position, product)
                }
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteProduct(position: Int, product: Product){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT).document(product.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Produk Telah di Hapus", Toast.LENGTH_LONG).show()
                arrayList.removeAt(position)
                if(arrayList.size==0){
                    layout_empty.visibility = View.VISIBLE
                }
                mAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Gagal menghapus Produk " + e.message, Toast.LENGTH_LONG).show()
            }
    }


    private fun automaticLoadMore() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                Handler(Looper.getMainLooper()).postDelayed({
                    if (!stopload) {
                        refresh()
                    }
                }, 300)
            }
        }
    }


    private fun getProfilePerList(){
        swipeRefresh.isRefreshing = false
        for ( i in 0 until arrayList.size) {
            val product = arrayList[i]
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_USER).document(product.userId.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(task.result.exists()){
                        val user = task.result.toObject(User::class.java)
                        Log.d("eksekusi", product.detail?.name + "xx")
                        userList.add(user)
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingIndicator(false)
                        setupData()
                        swipeRefresh.isRefreshing = false
                        updateList(arrayList)
                    }, 800)
                }
            }
        }

    }
}
