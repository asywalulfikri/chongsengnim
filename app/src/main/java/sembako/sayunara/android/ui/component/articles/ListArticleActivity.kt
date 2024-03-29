package sembako.sayunara.android.ui.component.articles

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
import androidx.recyclerview.widget.LinearLayoutManager
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


class ListArticleActivity : BaseActivity(),ArticleView.ViewList,ArticlesAdapter.OnClickListener{

    private var mAdapter =  ArticlesAdapter()
    val services = ArticleServices()
    private var mLastQueriedDocument: DocumentSnapshot? = null
    private var stopload = false
    private var firstLoad = true
    private var status : Boolean? =null
    val arrayList: ArrayList<Articles> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar(toolbar)
        ivBack.visibility = View.INVISIBLE

        status = getAdmin()||getSeller()||getSuperAdmin()

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
           refresh()
        }

        if(getUsers?.profile?.type.toString()!=Constant.userType.typeUser){
            fabAddData.visibility = View.VISIBLE
        }

        fabAddData.setOnClickListener {
            val intent = Intent(activity, PostArticleActivity::class.java)
            startForResult.launch(intent)
        }

        loadArticles()

    }

    private fun refresh(){
        mLastQueriedDocument = null
        firstLoad = true
        arrayList.clear()
        loadArticles()
    }

    private fun loadArticles(){
        services.getListArticle(this, FirebaseFirestore.getInstance(),getUsers,mLastQueriedDocument)
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
                layoutProgress.visibility = View.VISIBLE
            }
        }else{
            layoutProgress.visibility = View.GONE
        }
    }

    override fun onRequestSuccess(querySnapshot: QuerySnapshot) {
        for (doc in querySnapshot) {
            val articles = doc.toObject(Articles::class.java)
            arrayList.add(articles)
        }

        if (mLastQueriedDocument == null) {
            when {
                querySnapshot.size() in 1..9 -> {
                    rlLoadMore.visibility = View.GONE
                    stopload = true
                }
                querySnapshot.size()==0 -> {
                    layoutEmpty.visibility = View.VISIBLE
                    if(status==true){
                        textViewEmptyList.text = getString(R.string.text_empty_article_admin)
                    }else{
                        textViewEmptyList.text = getString(R.string.empty_article)
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
    private fun updateList(arrayList: ArrayList<Articles>) {
        firstLoad = false
        mAdapter.setItems(activity,arrayList,status!!,this)
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        automaticLoadMore()
    }


    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStatusChange(param : String,position : Int,value : Boolean) {
        if(param=="active"){
            arrayList[position].status?.publish = value
        }else if(param=="draft"){
            arrayList[position].status?.draft = value
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onClickDetail(position: Int, articles: Articles) {
        val intent = Intent(this,ArticleDetailActivity::class.java)
        intent.putExtra("articles",articles)
        startActivity(intent)
    }

    override fun onActionClick(position: Int, articles: Articles) {
        showDialogAction(position,articles)
    }

    private fun showDialogAction(position: Int,articles: Articles){

        val alertDialog = AlertDialog.Builder(activity)
        var ad : AlertDialog? =null

        val inflater     = LayoutInflater.from(activity)
        val view         = inflater.inflate(R.layout.activity_action_dialog, null)
        val tvTitle      = view.findViewById<TextView>(R.id.tvTitle)
        val btnDelete    = view.findViewById<Button>(R.id.btnDelete)
        val btnEdit      = view.findViewById<Button>(R.id.btnEdit)
        val btnPublish   = view.findViewById<Button>(R.id.btnPublish)
        val btnUnPublish = view.findViewById<Button>(R.id.btnUnPublish)

        if(getSeller()){
            if(articles.status?.draft==true){
                btnEdit.visibility = View.VISIBLE
            }
        }else if(getAdmin()||getSuperAdmin()){

            if(articles.status?.publish==true){
                btnDelete.visibility = View.VISIBLE

                if(articles.html!=""){
                    btnEdit.visibility = View.VISIBLE
                }
                if(articles.status?.moderation==true){
                    btnPublish.visibility = View.VISIBLE
                    btnPublish.setOnClickListener {
                        editStatus(position,"publish",true,articles)
                        ad?.dismiss()
                    }
                }else{

                    if(articles.status?.draft==true){
                        btnEdit.visibility = View.VISIBLE

                        if(articles.title!=""&&articles.category.size!=0&&articles.content!=""&&articles.images.size!=0){
                            btnPublish.visibility =View.VISIBLE
                            btnPublish.setOnClickListener {
                                editStatus(position,"draft",false,articles)
                                ad?.dismiss()
                            }
                            btnUnPublish.visibility = View.GONE
                        }else{
                            btnPublish.visibility =View.GONE
                            btnUnPublish.visibility = View.GONE
                        }
                    }else{
                        btnUnPublish.visibility =View.VISIBLE

                    }
                }
            }else{

                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                btnPublish.visibility = View.VISIBLE

                btnPublish.setOnClickListener {
                    editStatus(position,"publish",true,articles)
                    ad?.dismiss()
                }

            }
        }

        tvTitle.text = getString(R.string.text_choose)
        alertDialog.setView(view)
        ad = alertDialog.show()


        btnEdit.setOnClickListener {
            moveToEdit(articles)
            ad.dismiss()
        }

        btnUnPublish.setOnClickListener {
            editStatus(position,"publish",false,articles)
            ad.dismiss()
        }


        if(getSuperAdmin()){
            btnDelete.setOnClickListener {
                dialog(position,articles)
                ad.dismiss()
            }
        }else{
            btnDelete.setOnClickListener {
                editStatus(position,"active",false,articles)
                ad.dismiss()
            }
        }
    }

    private fun moveToEdit(articles: Articles){
        val intent = Intent(this, PostArticleActivity::class.java)
        intent.putExtra("articles",articles)
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

    private fun editStatus(position : Int,param : String, value : Boolean,articles: Articles) {
        services.editStatus(position, this, param, value, articles.id.toString())
    }

    private fun dialog(position: Int, articles: Articles){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete_article))
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                deleteArticles(position, articles)
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteArticles(position: Int, articles: Articles){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_ARTICLES).document(articles.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Artikel Telah di Hapus", Toast.LENGTH_LONG).show()
                arrayList.removeAt(position)
                mAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Gagal menghapus Artikel " + e.message, Toast.LENGTH_LONG).show()
            }
    }


    private fun automaticLoadMore() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                Handler(Looper.getMainLooper()).postDelayed({
                    if (!stopload) {
                        loadArticles()
                    }
                }, 300)
            }
        }
    }


}
