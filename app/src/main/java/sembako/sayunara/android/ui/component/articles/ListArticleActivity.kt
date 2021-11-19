package sembako.sayunara.android.ui.component.articles

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.recyclerView
import kotlinx.android.synthetic.main.activity_list.swipeRefresh
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.article.PostArticleActivity
import java.util.ArrayList

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import sembako.sayunara.android.R

class ListArticleActivity : BaseActivity(),ArticleView.ViewList,ArticlesAdapter.OnClickListener{

    private var mAdapter = ArticlesAdapter()
    val services = ArticleServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupToolbar(toolbar)
        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
            loadArticles()
        }

        if(getUsers?.profile?.type.toString()!=Constant.userType.typeUser){
            fabAddData.visibility = View.VISIBLE
        }

        fabAddData.setOnClickListener {
            val intent = Intent(activity,PostArticleActivity::class.java)
            startActivity(intent)
        }
        loadArticles()

    }

    private fun loadArticles(){
        layout_progress.visibility = View.VISIBLE
        services.getListArticle(this, FirebaseFirestore.getInstance(),getUsers)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun loadingIndicator(isLoading: Boolean) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRequestSuccess(arrayList: ArrayList<Articles>) {
        if(arrayList.size>0){
            layout_empty.visibility = View.GONE
        }else{
            layout_empty.visibility = View.VISIBLE
            textViewEmptyList.text = getString(R.string.empty_article)
        }
        swipeRefresh.isRefreshing = false
        layout_progress.visibility = View.GONE
        val status : Boolean = getAdmin()||getSeller()||getSuperAdmin()
        mAdapter.setItems(activity,arrayList, status,this)
    }

    override fun onRequestFailed(code: Int) {
        setToast(code)
    }


    override fun onClickDetail(position: Int, articles: Articles) {
        val intent = Intent(this,ArticleDetailActivity::class.java)
        intent.putExtra("articles",articles)
        startActivity(intent)
    }

    override fun onActionClick(position: Int, articles: Articles) {
        showDialogAction(articles)

    }

    private fun showDialogAction(articles: Articles){
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.activity_action_dialog, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnPublish = view.findViewById<Button>(R.id.btnPublish)
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
                }else{
                    btnUnPublish.visibility = View.VISIBLE
                }
            }else{
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                btnPublish.visibility = View.VISIBLE
            }
        }

        tvTitle.text = getString(R.string.text_choose)

        btnEdit.setOnClickListener {
            moveToEdit(articles)
        }

        val builder = getBuilder(activity)
        builder.setView(view)
        builder.create().show()

    }

    private fun moveToEdit(articles: Articles){
        val intent = Intent(this,PostArticleActivity::class.java)
        intent.putExtra("articles",articles)
        intent.putExtra("edit",true)
        startActivity(intent)
    }


}
