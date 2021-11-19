package sembako.sayunara.article

/**
 * Description: Post Article
 * Date: 2021/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.seller.activity_post_article.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.articles.ArticleServices
import sembako.sayunara.android.ui.component.articles.ArticleView
import sembako.sayunara.android.ui.component.articles.Articles
import java.util.*


class PostArticleActivity : BaseActivity(), ArticleView.ViewArticle{

    val services = ArticleServices()
    var choose : String? = null
    var title : String? = null
    var content : String? = null
    var source : String? = null
    var image : String? = null
    var category : String? = null
    var arrayType = ArrayList<String>()
    var moderation  = false
    var isEdit = false
    var idArticle = ""
    var userId = ""

    private var tagsList = arrayOf("kesehatan", "makanan", "resep", "pasar", "infomasi","belanja","harga","wisata","kuliner")
    private lateinit var isSelectedArray: BooleanArray
    private var mSelectedItems: ArrayList<Int>? = ArrayList()
    private var articles : Articles? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_article)

        setupToolbar(toolbar)
        loadingIndicator(false)

        if(intent.hasExtra("edit")){
            articles  = intent.getSerializableExtra("articles") as Articles
            isEdit    = true
            idArticle = articles?.id.toString()
            userId    = articles?.userId.toString()
            updateData()
        }else{
            userId    = getUsers?.profile?.userId.toString()
        }
        setupViews()

    }

    private fun updateData(){
        etTitle.setText(articles?.title)
        etContent.setText(articles?.content)
        etSource.setText(articles?.source)
        etUrlImage1.setText("")
        etCategory.text = ""
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
            layout_progress.visibility =View.VISIBLE
        }else{
            layout_progress.visibility =View.GONE
        }
    }

    override fun onRequestSuccess() {
        if(moderation){
            emptyForm()
            showDialogGeneral(getString(R.string.text_article_moderation))

        }else{
            emptyForm()
            setToast("Artikel berhasil di publish")
        }
    }

    private fun emptyForm(){
        etTitle.setText("")
        etContent.setText("")
        etSource.setText("")
        etUrlImage1.setText("")
        etCategory.text = ""
    }

    override fun onRequestFailed(message: String) {
        setToast(message)
    }


    private fun showDialogType() {
        isSelectedArray = BooleanArray(200)
        Arrays.fill(isSelectedArray, java.lang.Boolean.FALSE)
        mSelectedItems = ArrayList()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.text_choose_category))
            .setMultiChoiceItems(tagsList, isSelectedArray) { _, which, isChecked ->
                if (isChecked) {
                    isSelectedArray[which] = true
                }
            }
            .setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
                val sb = StringBuilder()
                for (i in isSelectedArray.indices) {
                    if (isSelectedArray[i]) {
                        mSelectedItems!!.add(i)
                    }
                }
                for (i in mSelectedItems!!.indices) {
                    val index = mSelectedItems!![i]
                    val result = tagsList[index]
                    sb.append(result)
                    if (i < mSelectedItems!!.size - 1) {
                        sb.append(",") // Add a comma for separation
                    }
                }
                val strArray = sb.toString().split(",").toTypedArray()
                for (ii in strArray.indices) {
                    // String tipex; arrayType.add(strArray[ii].trim { it <= ' ' })
                }
                etCategory.text = sb
                Log.e("tags", mSelectedItems.toString())
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }


    @SuppressLint("SetTextI18n")
    override fun setupViews() {

        etCategory.setOnClickListener {
            showDialogType()
        }

        if(getUsers?.profile?.type==Constant.userType.typeSeller){
            moderation = true
        }

        btnPublish.setOnClickListener {
            title = etTitle.text.toString().trim()
            content = etContent.text.toString().trim()
            source = etSource.text.toString().trim()
            image = etUrlImage1.text.toString().trim()
            category = etCategory.text.toString().trim()

            if(title==""||content==""||image==""){
                setToast("Data tidak boleh kosong")
            }else if(content.toString().length<100){
                setToast("Artikel terlalu pendek")
            }else{
                services.addArticle(this,title.toString(),content.toString(),source.toString(),image.toString(),userId,category.toString(),moderation,false,isEdit,idArticle)
            }

        }

        btnDraft.setOnClickListener {
            title = etTitle.text.toString().trim()
            content = etContent.text.toString().trim()
            source = etSource.text.toString().trim()
            image = etUrlImage1.text.toString().trim()
            category = etCategory.text.toString().trim()

            if(title==""){
                setToast("Minimal Judul Harus Diisi")
            }else{
                services.addArticle(this,title.toString(),content.toString(),source.toString(),image.toString(),userId,category.toString(),moderation,true,isEdit,idArticle)
            }
        }
    }
}
