package sembako.sayunara.android.ui.component.banner

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_banner.*
import kotlinx.android.synthetic.main.activity_add_banner.etTitle
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.layout_publish_draft.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import sembako.sayunara.android.R
import android.provider.MediaStore

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_banner.ivBanner
import kotlinx.android.synthetic.main.layout_publish_draft.btnPublish
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.util.checkReadExtStoragePermission
import sembako.sayunara.android.ui.util.checkWriteExStoragePermission


class PostBannerActivity : BaseActivity(), BannerView.Post {

    private val cameraRequest = 1888
    private val pickImage = 100
    private var services = BannerServices()
    private var load = false
    private var imageUri : Uri? =null
    private var urlImage = ""
    private var title : String? =null
    private var description : String? =null
    private var draft  = false
    private var banner : Banner ? =null
    private var isEdit = false
    private var isDelete = false
    private var bannerId : String? =null
    private var xx  = ""
    private var fromUrl = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_banner)
        setupToolbar(toolbar)

        if(intent.hasExtra("banner")){
            banner = intent.getSerializableExtra("banner") as Banner
            bannerId = banner?.id.toString()
            xx = banner?.detail?.image .toString()
            isEdit = true
            setData()
        }

        setupViews()

    }

    private fun setData(){
        etTitle.setText(banner?.detail?.title)
        etDescription.setText(banner?.detail?.description)

        if(xx !=""){
            setImage(banner?.detail?.image.toString())
        }else{
            imageUri = null
            urlImage = ""
        }
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
        setToast(getString(R.string.text_banner_success_add))
        onBackPressed()

    }

    override fun onRequestImageSuccess(url: String) {
        urlImage  = url
        addBanner()
    }

    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    override fun setupViews() {

        llImage.setOnClickListener {
            showDialogChooser()
        }

        btnPublish.setOnClickListener {
            title = etTitle.text.toString().trim()
            description = etDescription.text.toString().trim()
            draft = false

            if(isEdit){
                if(title==""||description==""){
                    setToast(getString(R.string.text_complete_form))
                }else{
                    if(isDelete){
                        if(imageUri==null){
                            setToast(getString(R.string.text_complete_form))
                        }else{
                            if(xx.contains("sayunara-483b4.appspot.com")){
                                deleteImage()
                            }else{
                                addBanner()
                            }
                        }
                    }else{
                        addBanner()
                    }
                }
            }else{
                if(title==""||description==""||imageUri==null){
                    setToast(getString(R.string.text_complete_form))
                }else{
                    if(fromUrl){
                        addBanner()
                    }else{
                        uploadImage()
                    }
                }
            }


        }

        if(banner?.status?.draft==false){
            btnDraft.visibility = View.GONE
        }

        btnDraft.setOnClickListener {
            title = etTitle.text.toString().trim()
            description = etDescription.text.toString().trim()
            draft = true
            if(title==""){
                setToast("Judul tidak boleh kosong")
            }else{
                if(isEdit){
                    if(isDelete){
                        if(xx.contains("sayunara-483b4.appspot.com")){
                            deleteImage()

                        }else{
                            addBanner()
                        }
                    }else{
                        if(imageUri==null){
                            addBanner()
                        }else{
                            if(fromUrl){
                                addBanner()
                            }else{
                                uploadImage()
                            }
                        }
                    }
                }else{
                    if(imageUri==null){
                        addBanner()
                    }else{
                        if(fromUrl){
                            addBanner()
                        }else{
                            uploadImage()
                        }
                    }
                }
            }

        }

    }

    private fun addBanner(){
        services.addBanner(this,title.toString(),description.toString(),urlImage,draft,isEdit,bannerId)
    }

    private fun uploadImage(){
        services.uploadImage(this,imageUri)
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogChooser(){

        val alertDialog = AlertDialog.Builder(activity)

        val inflater     = LayoutInflater.from(activity)
        val view         = inflater.inflate(R.layout.activity_action_dialog, null)
        val tvTitle      = view.findViewById<TextView>(R.id.tvTitle)

        val btnCamera      = view.findViewById<Button>(R.id.btnEdit)
        val btnGallery    = view.findViewById<Button>(R.id.btnDelete)
        val btnUrl   = view.findViewById<Button>(R.id.btnPublish)
        val btnCancel = view.findViewById<Button>(R.id.btnUnPublish)

        btnCamera.visibility = View.GONE
        btnGallery.visibility = View.VISIBLE
        btnUrl.visibility = View.VISIBLE
        btnCancel.visibility = View.VISIBLE

        btnCamera.text = "Ambil dari Kamera"
        btnGallery.text = "Ambil dari Gallery"
        btnUrl .text = "Ambil dari URL "
        btnCancel .text = "Batal"


        tvTitle.text = getString(R.string.text_choose)
        alertDialog.setView(view)
        val ad: AlertDialog? = alertDialog.show()

        btnCamera.setOnClickListener {
            ad?.dismiss()
        }

        btnGallery.setOnClickListener {
            checkPermission()
            ad?.dismiss()
        }

        btnUrl.setOnClickListener {
            dialogUrl()
            ad?.dismiss()
        }


        btnCancel.setOnClickListener {
            ad?.dismiss()
        }
    }

    /*private fun openCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, cameraRequest)
    }*/

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==cameraRequest && resultCode == RESULT_OK) {
            val photo = data?.extras!!["data"] as Bitmap?
            setImage(photo)
        }
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            ivClear.visibility = View.VISIBLE
            imageUri = data?.data
            setImage(imageUri)

            ivClear.setOnClickListener {
                imageUri = null
                urlImage = ""
                ivBanner.setImageURI(null)
                ivClear.visibility = View.GONE
            }

        }
    }

    fun setImage(photo : Bitmap?){
        ivBanner.setImageBitmap(photo)
    }

    fun setImage(photo : Uri?){
        ivBanner.visibility = View.VISIBLE
        ivBanner.setImageURI(photo)
    }

    private fun checkPermission() {
        if (checkWriteExStoragePermission() && checkReadExtStoragePermission()) {
            openGallery()
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            openGallery()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied permanently, navigate user to app settings
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    Toast.makeText(activity, "Error occurred! ", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }



    @SuppressLint("InflateParams")
    fun dialogUrl() {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_input_url, null)
        val etUrl = view.findViewById<EditText>(R.id.etUrlImage)
        val ivClear = view.findViewById<ImageView>(R.id.ivClear)

        ivClear.setOnClickListener {
            etUrl.setText("")
        }

        etUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()){
                    ivClear.visibility = View.VISIBLE
                }else{
                    ivClear.visibility = View.GONE
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int,
                                           count: Int, after: Int) {
                if(s.toString().isNotEmpty()){
                    ivClear.visibility = View.VISIBLE
                }else{
                    ivClear.visibility = View.GONE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int, count: Int) {

            }
        })


        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
            .setNegativeButton(getString(R.string.text_cancel)) {
                    dialog, _ ->
                dialog.dismiss()
                hideKeyboard()
            }
            .setPositiveButton(getString(R.string.text_save)
            ) { dialog, _ ->

                val url = etUrl.text.toString()
                if(url!=""){
                    setImage(url)
                    fromUrl = true
                }
                hideKeyboard()
                dialog.dismiss()

            }
        builder.create().show()
    }


    fun setImage(url : String?){
        ivBanner.visibility = View.VISIBLE
        urlImage = url.toString()
        if(url!=""){
            Picasso.get()
                .load(url)
                .into(ivBanner, object : Callback {
                    override fun onSuccess() {
                        urlImage = url.toString()
                        ivClear.visibility = View.VISIBLE
                        ivClear.setOnClickListener {
                            if(isEdit){
                                isDelete = true
                                ivBanner.setImageURI(null)
                                ivBanner.visibility = View.GONE
                                fromUrl = false
                                urlImage = ""
                                ivClear.visibility =View.GONE
                            }

                        }
                    }

                    override fun onError(e: Exception?) {
                        setToast(e?.message.toString())
                    }

                })
        }
    }


    private fun deleteImage(){
        loadingIndicator(true)
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(banner?.detail?.image.toString())
        photoRef.delete().addOnSuccessListener {

            if(imageUri!=null){
                services.uploadImage(this,imageUri)
            }else{
                addBanner()
            }

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