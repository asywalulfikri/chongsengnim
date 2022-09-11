package sembako.sayunara.android.ui.component.banner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.rahman.dialog.Utilities.SmartDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_banner.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import com.google.firebase.storage.StorageReference


class BannerDetailActivity : BaseActivity(),BannerView.ViewDetail{

    val services = BannerServices()
    var banner : Banner? = null
    var load = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_banner)

        setupToolbar(toolbar)
        banner = intent.getSerializableExtra("banner") as Banner

        swipeRefresh.setOnRefreshListener {
            getDetail()
        }

        setupViews(banner)

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
            layout_progress.visibility = View.VISIBLE
        }else{
            layout_progress.visibility = View.GONE
        }
    }

    override fun onRequestSuccess() {
        load = true
        setToast("Status Telah Berubah")
        getDetail()
    }


    override fun onDeleteSuccess(message: String) {
        setToast(message)
        load = true
        onBackPressed()
    }

    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    override fun onRequestDetailSuccess(banner: Banner?) {
        this.banner = banner
        setupViews(banner)
    }


    @SuppressLint("SetTextI18n")
    override fun setupViews(banner: Banner?) {
        swipeRefresh.isRefreshing = false
        tvTitle.text = banner?.detail?.title
        tvDescription.text = banner?.detail?.description

        if(banner?.detail?.image!=""){
            Glide
                .with(activity)
                .load(banner?.detail?.image)
                .centerCrop()
                .into(ivBanner);
        }

        if(getSuperAdmin()||getAdmin()){
            llAction.visibility = View.VISIBLE
        }

        btnDelete.setOnClickListener {
            dialogDelete()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(activity,PostBannerActivity::class.java)
            intent.putExtra("banner",banner)
            startForResult.launch(intent)
        }



        if(banner?.status?.active==true){
            if(banner.status?.draft==true){
                if(banner.detail?.title!=""&&banner.detail?.description!=""&&banner.detail?.image!=""){
                    btnPublish.visibility = View.VISIBLE
                    btnPublish.text = getString(R.string.text_publish)
                }else{
                    btnPublish.visibility = View.GONE
                }
            }else{
                btnPublish.text = getString(R.string.text_unpublish)
            }

        }else{
            btnPublish.text = getString(R.string.text_publish)
        }
        btnPublish.setOnClickListener {
            if(banner?.status?.active==true){
                if(banner.status?.draft==true){
                    services.editStatus(this,"draft",false,banner.id.toString())
                }else{
                    services.editStatus(this,"active",false,banner.id.toString())
                }
            }else{
                services.editStatus(this,"active",true,banner?.id.toString())
            }
        }


    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val load = data?.getBooleanExtra("load", false)
            if (load==true) {
                this.load = true
                getDetail()
            }
        }
    }

    private fun getDetail(){
         services.getDetail(this,banner?.id.toString())
    }

    private fun dialogDelete(){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete_banner))
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                if(banner?.detail?.image!=null||banner?.detail?.image!=""){
                    delete()
                }else{
                    val url = banner?.detail?.image.toString()
                    if(url.contains("sayunara-483b4.appspot.com")){
                        delete()
                    }else{
                        services.deleteBanner(this,banner?.id.toString())
                    }
                }
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }

    fun delete(){
        loadingIndicator(true)
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(banner?.detail?.image.toString())
        photoRef.delete().addOnSuccessListener {
            services.deleteBanner(this,banner?.id.toString())
        }.addOnFailureListener { // Uh-oh, an error occurred!
            loadingIndicator(false)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(RESULT_OK,intent)
        finish()

    }


}