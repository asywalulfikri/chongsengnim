package sembako.sayunara.android.ui.component.articles

/**
 * Description: Post Article
 * Date: 2021/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_post_article.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import java.util.*


class PostArticleActivity : BaseActivity(), ArticleView.ViewArticle{

    val services = ArticleServices()
    var urlImage : String? = null
    var title : String? = null
    var content : String? = null
    var source : String? = null
    var image1 : String? = null
    var image2 : String? = null
    var image3 : String? = null
    var category : String? = null
    var arrayType = ArrayList<String>()
    var moderation  = false
    var isEdit = false
    var idArticle = ""
    var userId = ""
    var highLight = false
    var load = false

    private var tagsList = arrayOf("kesehatan", "makanan", "resep", "pasar", "infomasi","belanja","harga","wisata","kuliner")
    private lateinit var isSelectedArray: BooleanArray
    private var mSelectedItems: ArrayList<Int>? = ArrayList()
    private var articles = Articles()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_article)

        setupToolbar(toolbar)
        loadingIndicator(false)

        if(intent.hasExtra("edit")){
            articles  = intent.getSerializableExtra("articles") as Articles
            isEdit    = true
            idArticle = articles.id.toString()
            userId    = articles.userId.toString()
            updateData()
        }else{
            userId    = getUsers?.profile?.userId.toString()
        }
        setupViews()

    }

    private fun updateData(){
        etTitle.setText(articles.title)
        etContent.setText(articles.content)
        etSource.setText(articles.source)

        if(getAdmin()||getSuperAdmin()){
            if(articles.status?.notification==true){
                switchNotification.visibility = View.GONE
            }else{
                switchNotification.visibility = View.VISIBLE
            }
        }


        if(articles.images.size==1){
            etUrlImage1.setText(articles.images[0])
        }

        if(articles.images.size==2){
            etUrlImage1.setText(articles.images[0])
            etUrlImage2.setText(articles.images[1])
        }

        if(articles.images.size==3){
            etUrlImage1.setText(articles.images[0])
            etUrlImage2.setText(articles.images[1])
            etUrlImage3.setText(articles.images[2])
        }

        arrayType = articles.category
        val sb = StringBuilder()
        for (i in articles.category.indices) {
            val result = articles.category[i]
            sb.append(result)
            if (i < articles.category.size - 1) {
                sb.append(", ") // Add a comma for separation
            }
        }
        etCategory.text = sb

        if(articles.status?.draft==true){
            btnDraft.visibility = View.VISIBLE
        }else{
            btnDraft.visibility = View.GONE
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
            layout_progress.visibility =View.VISIBLE
        }else{
            layout_progress.visibility =View.GONE
        }
    }

    override fun onRequestSuccess(message: String , draft : Boolean, id : String) {
        hideKeyboard()
        load = true

        if(switchNotification.isChecked){
            pushNotificationGeneral(title.toString(),content.toString(),Constant.TypeNotification.article,id)
        }
        if(moderation){
            emptyForm()
            if(draft){
                showDialogGeneral(getString(R.string.text_draft_success))
            }else{
                showDialogGeneral(getString(R.string.text_article_moderation))
            }

        }else{
            emptyForm()
            setToast(message)
        }
    }

    private fun emptyForm(){
        etTitle.setText("")
        etContent.setText("")
        etSource.setText("")
        etUrlImage1.setText("")
        etUrlImage2.setText("")
        etUrlImage3.setText("")
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

        highLight = switchHigLight.isChecked

        if(getSuperAdmin()||getAdmin()){
            switchHigLight.visibility = View.VISIBLE
            llImageSuperAdmin.visibility = View.VISIBLE
            switchNotification.visibility = View.VISIBLE
        }

        etCategory.setOnClickListener {
            showDialogType()
        }

        if(getUsers?.profile?.type==Constant.userType.typeSeller){
            moderation = true
        }

        btnPublish.setOnClickListener {
            checkData()

            if(title==""||content==""||urlImage==""){
                setToast("Data tidak boleh kosong")
            }else if(content.toString().length<1){
                setToast("Artikel terlalu pendek")
            }else{
                hideKeyboard()
                services.addArticle(this,title.toString(),content.toString(),source.toString(),urlImage.toString(),userId,category.toString(),moderation,false,isEdit,idArticle,highLight,switchNotification.isChecked)
            }

        }

        btnDraft.setOnClickListener {
            checkData()
            if(title==""){
                setToast("Minimal Judul Harus Diisi")
            }else{
                hideKeyboard()
                services.addArticle(this,title.toString(),content.toString(),source.toString(),urlImage.toString(),userId,category.toString(),moderation,true,isEdit,idArticle,highLight,false)
            }
        }
    }

    fun checkData(){
        title = etTitle.text.toString().trim()
        content = etContent.text.toString().trim()
        source = etSource.text.toString().trim()
        image1 = etUrlImage1.text.toString().trim()
        image2 = etUrlImage2.text.toString().trim()
        image3 = etUrlImage3.text.toString().trim()
        category = etCategory.text.toString().trim()

        urlImage = image1

        if(image2!=""){
            "," + image2
            urlImage = image1+","+image2
        }
        if(image3!=""){
            ","+image3
            urlImage = image1+","+image2+","+image3
        }

    }


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(RESULT_OK,intent)
        finish()
    }
}
