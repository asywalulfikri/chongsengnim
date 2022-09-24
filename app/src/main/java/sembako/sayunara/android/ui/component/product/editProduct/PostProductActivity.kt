package sembako.sayunara.android.ui.component.product.editProduct

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.features.cameraonly.CameraOnlyConfig
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.layout_publish_draft.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sembako.sayunara.android.R
import sembako.sayunara.android.adapter.ImagesAdapter
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.service.CityService
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.camera.CustomImagePickerComponents
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.editProduct.model.City
import sembako.sayunara.android.ui.component.product.editProduct.model.ImageData
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList


class PostProductActivity : BaseActivity(),PostProductContract.PostProductView, ImagesAdapter.OnClickListener {
    override val mProductId: String?
        get() = productId
    override val mProductName: String
        get() = etProductName.text.toString().trim()
    override val mProductType: String
        get() = etProductType.text.toString().trim()
    override val mProductWeight: String
        get() = etProductWeight.text.toString().trim()
    override val mProductUnit: String
        get() = etProductUnit.text.toString().trim()
    override val mProductPrice: String
        get() = etProductPrice.text.toString().trim().replace("Rp","").trim().replace(",","")
    override val mProductStock: String
        get() = etProductStock.text.toString().trim()
    override val mProductDescription: String
        get() = etProductDescription.text.toString().trim()
    override val mProductDiscount: String
        get() = etProductDiscount.text.toString()
    override val mLatitude: String
        get() = latitude
    override val mLongitude: String
        get() = longitude
    override val mVersionName: String?
        get() = getVersionName(this)
    override val mImages: List<String>
        get() = uploadImagesArray
    override val mImagesHasDelete: List<String>
        get() = deleteImagesArray
    override val isHighLight: Boolean
        get() =  highLight
    override val mArrayType: ArrayList<String>
        get() = arrayType

    var arrayLocation =  ArrayList<String>()

    @Inject
    lateinit var mPostProductPresenter: PostProductPresenter
    private lateinit var mPostProductDialog: PostProductDialog
    var arrayType = ArrayList<String>()
    var isEdit = false
    var highLight = false
    lateinit var product : Product
    private var isLoad = false

    private val images = arrayListOf<Image>()
    private val imagesData = arrayListOf<ImageData>()
    private var imagesAdapter: ImagesAdapter? = null
    private var imagesAdapter2: ImagesAdapter? = null

    private var isCameraOnly = false
    private val calendar = Calendar.getInstance()

    private val uploadImagesArray: MutableList<String> = java.util.ArrayList()
    private val deleteImagesArray: MutableList<String> = java.util.ArrayList()
    private var isUploadImage = false
    private var totalUpload = 0
    private var list1 = listOf<Boolean>()
    private var productId = ""
    private var cityList: List<City>? = ArrayList()

    private val imagePickerLauncher = registerImagePicker {
        if(it.isNotEmpty()){
            if(!isCameraOnly){
                images.clear()
            }
            isUploadImage = true
            images.addAll(it)
            onPhotosReturned(images)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        setupToolbar(toolbar)

        mPostProductPresenter = PostProductPresenter()
        mPostProductDialog = PostProductDialog(this)
        mPostProductPresenter.attachView(this)


        initiate()
        //setDataManual()

        if(intent.hasExtra(Constant.IntentExtra.product)){
            product = intent.getSerializableExtra(Constant.IntentExtra.product) as Product
            productId = product.id.toString()
            setEdit(product)
            isEdit = true
        }

        setupViews()
        //getListCity(false)
    }

    private fun initiate(){
        imagesAdapter = ImagesAdapter()
        val mainMenuLayoutManager = GridLayoutManager(this, 3)
        recycler_view_photo.layoutManager = mainMenuLayoutManager
        recycler_view_photo.isHorizontalScrollBarEnabled
        recycler_view_photo.setHasFixedSize(true)
        recycler_view_photo.adapter = imagesAdapter
    }

    override fun showErrorValidation(message: Int) {
        setToast(message)
    }

    override fun loadingIndicator(isLoading: Boolean) {
        if(isLoading){
            layoutProgress.visibility = View.VISIBLE
        }else{
            layoutProgress.visibility = View.GONE
        }
        //setDialog(isLoading)

    }

    override fun setColorButton(color: Int) {
        //setColorTintButton(btnPublish, getColors(this,color))
    }

    override val getUser: User?
        get() = getUsers

    override fun onBack() {
        if(isEdit){
            setToast("Produk berhasil di ubah")
        }else{
            setToast("Produk berhasil di tambah")
        }
        isLoad = true
        emptyData()
        onBackPressed()
    }

    private fun emptyData(){
        etProductName.setText("")
        etProductPrice.setText("")
        etProductDescription.setText("")
        etProductType.setText("")
        etProductUnit.setText("")
        etProductWeight.setText("")
        etProductStock.setText("")
        etProductDiscount.setText("")
        etProductAvailable.setText("")
    }


    override fun setPresenter(presenter: BasePresenter<*>) {

    }

    private fun getListCity(isShow : Boolean){

        if(isShow){
           showLoadingDialog()
        }
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val retro = Retrofit.Builder()
            .baseUrl("https://asywalulfikri.github.io")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()


        val service = retro.create(CityService::class.java)
        val countryRequest = service.listCountries()

        countryRequest.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                if(response.isSuccessful){
                    cityList = response.body();
                    if (cityList != null) {
                        for (i in cityList!!.indices) {
                            val lokasi = cityList!![i].getName().toString()
                            val xx = setUppercaseFirstLetter(lokasi)
                            arrayLocation.add(xx.toString())
                            hideLoadingDialog()
                        }
                    }
                    if(isShow){
                        showLocationDialog()
                        hideLoadingDialog()
                    }
                }else{
                    hideLoadingDialog()
                    Log.d("error", response.message().toString()+".")
                }
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                Log.d("error", t.message.toString()+".")
            }

        })
    }


    private fun showLocationDialog() {
        val type = arrayLocation.toTypedArray()
        val builder: android.app.AlertDialog.Builder = getBuilder(this)
        builder.setTitle("Pilih Lokasi")
            .setItems(type) { _, which ->
                run {
                   // etDeliveryFrom.setText((type[which]))
                   // etDeliveryFrom.setSelection(type[which].length)
                }
            }
        builder.create().show()
    }


    private fun setupViews(){
        mPostProductPresenter.validation()
        etProductUnit.setOnClickListener {
            mPostProductDialog.showUnitDialog()
        }

        ivImage.setOnClickListener {
        }
            mPostProductDialog.takeImage()
        etProductType.setOnClickListener {
            mPostProductDialog.showDialogType()
        }

        etHighLight.setOnClickListener {
            mPostProductDialog.dialogHighLight()
        }

        btnPublish.setOnClickListener{

            if(mPostProductPresenter.validationData()){
                if(imagesData.size==0){
                    setToast(getString(R.string.text_product_image_empty))
                }else{
                    if(isUploadImage){
                        for (i in 0 until imagesData.size) {
                            list1 = listOf(imagesData[i].isUpload)
                            val predicate: (Boolean) -> Boolean = {it == true}
                            totalUpload = list1.count(predicate)
                        }
                        uploadImageFirebase(publish = true)
                    }else{
                        mPostProductPresenter.checkData(isEdit)
                    }
                }
            }
        }

        btnDraft.setOnClickListener{
            if(etProductName.text.toString()==""){
                setToast("Minimal Isi Nama Product")
            }else{
                if(isUploadImage){
                    for (i in 0 until imagesData.size) {
                        list1 = listOf(imagesData[i].isUpload)
                        val predicate: (Boolean) -> Boolean = {it == true}
                        totalUpload = list1.count(predicate)
                    }
                    uploadImageFirebase(false)
                }else{
                    mPostProductPresenter.postProduct(isEdit, isDraft = true)
                }

            }
        }


        chooser_button.setOnClickListener {
            checkCamera()
        }

        rlFirst.setOnClickListener {
            mPostProductDialog.dialogUrl(1)
        }
        rlSecond.setOnClickListener {
            mPostProductDialog.dialogUrl(2)
        }
        rlThird.setOnClickListener {
            mPostProductDialog.dialogUrl(3)
        }
    }


    private fun fromCamera(){
        isCameraOnly = true
        imagePickerLauncher.launch(CameraOnlyConfig())
    }

    private fun fromUrl(){
        dialogUrl()
    }

    private fun fromGallery(){
        isCameraOnly = false
        imagePickerLauncher.launch(createConfig())
    }


    private fun showDialogAction(){

        val alertDialog = AlertDialog.Builder(activity)

        val inflater     = LayoutInflater.from(activity)
        val view         = inflater.inflate(R.layout.activity_action_dialog, null)
        val tvTitle      = view.findViewById<TextView>(R.id.tvTitle)
        val btnDelete    = view.findViewById<Button>(R.id.btnDelete)
        val btnEdit      = view.findViewById<Button>(R.id.btnEdit)
        val btnPublish   = view.findViewById<Button>(R.id.btnPublish)
        val btnUnPublish = view.findViewById<Button>(R.id.btnUnPublish)



        tvTitle.text = "Pilih Gambar melalui"
        alertDialog.setView(view)
        val ad: AlertDialog? = alertDialog.show()


        btnEdit.text = "Dari Kamera"
        btnEdit.visibility = View.VISIBLE
        btnEdit.setOnClickListener {
            fromCamera()
            ad?.dismiss()
        }

        btnPublish.text = "Dari Gallery"
        btnPublish.visibility = View.VISIBLE
        btnPublish.setOnClickListener {
            fromGallery()
            ad?.dismiss()
        }

        btnUnPublish.text = "Dari URL"
        btnUnPublish.visibility = View.GONE
        btnUnPublish.setOnClickListener {
            fromUrl()
            ad?.dismiss()
        }


        btnDelete.visibility = View.GONE
        btnDelete.setOnClickListener {
           // dialog(position,product)
            ad?.dismiss()
        }
    }


    @SuppressLint("InflateParams")
    private fun dialogUrl() {

        val inflater = LayoutInflater.from(this)
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


        if(isEdit){
           // etUrl.setText(product.detail?.images!![0])

        }

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
            .setNegativeButton(getString(R.string.text_cancel)) {
                    dialog, _ ->
                dialog.dismiss()
                hideKeyboard()
            }
            .setPositiveButton(getString(R.string.text_save)) { dialog, _ ->
                dialog.dismiss()
                val url = etUrl.text.toString()
                if(url.isNotEmpty()){
                    val image = Image(calendar.timeInMillis,Random().toString(),url)
                    images.add(image)
                    onPhotosReturned(images)

                }

            }
        builder.create().show()
    }



    private fun checkCamera() {
        if (checkAndRequestPermissions()) {
            if(images.size>2){
                setToast("Gambar yang terpilih sudah mencapai batas maksimal")
            }else{
                showDialogAction()
            }
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if(images.size>2){
                                setToast("Gambar yang terpilih sudah mencapai batas maksimal")
                            }else{
                                showDialogAction()
                            }
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
                    //TODO change text
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }


    private fun createConfig(): ImagePickerConfig {
        val returnAfterCapture = false//binding.switchReturnAfterCapture.isChecked
        val isSingleMode = false//binding.switchSingle.isChecked
        val useCustomImageLoader = false//binding.switchImageloader.isChecked
        val folderMode = true//binding.switchFolderMode.isChecked
        val includeVideo = false//binding.switchIncludeVideo.isChecked
        val onlyVideo = false//binding.switchOnlyVideo.isChecked
        val isExclude = false//binding.switchIncludeExclude.isChecked

        ImagePickerComponentsHolder.setInternalComponent(
            CustomImagePickerComponents(this, useCustomImageLoader)
        )

        return ImagePickerConfig {

            mode = if (isSingleMode) {
                ImagePickerMode.SINGLE
            } else {
                ImagePickerMode.MULTIPLE // multi mode (default mode)
            }

            language = "in" // Set image picker language
            theme = R.style.ImagePickerTheme

            // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            returnMode = if (returnAfterCapture) ReturnMode.ALL else ReturnMode.NONE

            isFolderMode = folderMode // set folder mode (false by default)
            isIncludeVideo = includeVideo // include video (false by default)
            isOnlyVideo = onlyVideo // include video (false by default)
            arrowColor = Color.RED // set toolbar arrow up color
            folderTitle = "Gallery" // folder selection title
            imageTitle = "Tap to select" // image selection title
            doneButtonText = "DONE" // done button text
            showDoneButtonAlways = true // Show done button always or not
            limit = 3 // max images can be selected (99 by default)
            isShowCamera = false // show camera or not (true by default)
            savePath = ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
            savePath = ImagePickerSavePath(Environment.getExternalStorageDirectory().path, isRelative = false) // can be a full path

            if (isExclude) {
                excludedImages = images.toFiles() // don't show anything on this selected images
            } else {
                selectedImages = images  // original selected images, used in multi mode
            }
        }
    }

    fun setDataManual(){
        etProductName.setText("Ayam Bakar")
        etProductPrice.setText("10000")
        etProductDescription.setText("Mantap Gays")
        etProductType.setText("daging")
        etProductUnit.setText("10")
        etProductWeight.setText("10")
        etProductStock.setText("10")
        etProductDiscount.setText("")
        etProductAvailable.setText("")
    }


    private fun dialogCameraGalleryUrl(){
        SmartDialogBuilder(this)
            .setTitle("Ambil Gambar")
            .setSubTitle("Pilih Sumber Gambar")
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(false) //hide cancel button
            .setPositiveButton("Dari Kamera / Gallery") { smartDialog ->
                //easyImage?.openChooser(this)
                smartDialog.dismiss()
            }.setNegativeButton("Dari Url ") { smartDialog ->

                smartDialog.dismiss()
            }.build().show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPostProductDialog.onActivityResult(requestCode,resultCode,data)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onPhotosReturned(images: ArrayList<Image>) {
        for(i in 0 until  images.size){
            val id = images[i].id
            val name = images[i].name
            val path = images[i].path
            val uri = images[i].uri

            val image = ImageData(id,name,path,uri,true)
            imagesData.add(image)
        }
        imagesAdapter?.setItems(this,imagesData,this)
        imagesAdapter?.notifyDataSetChanged()
        recycler_view_photo.scrollToPosition(images.size.minus(1))

    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constant.IntentExtra.isLoad, isLoad)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        mPostProductPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPostProductPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPostProductPresenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setEdit(product : Product){
        etProductName.setText(product.detail?.name)

        arrayType = product.detail!!.type
        val sb = StringBuilder()
        for (i in product.detail!!.type.indices) {
            val result = product.detail!!.type[i]
            sb.append(result)
            if (i < product.detail!!.type.size - 1) {
                sb.append(", ") // Add a comma for separation
            }
        }

        val totalImage = product.detail!!.images.size
        for (i in 0 until totalImage) {
            val path = (product.detail!!.images[i])
            val image = Image(calendar.timeInMillis,Random().toString(),path)
            images.add(image)
        }

        for(i in 0 until  images.size){
            val id = images[i].id
            val name = images[i].name
            val path = images[i].path
            val uri = images[i].uri

            val image = ImageData(id,name,path,uri,false)
            imagesData.add(image)
            uploadImagesArray.add(path)
        }
        imagesAdapter?.setItems(this,imagesData,this)
        //
        recycler_view_photo.scrollToPosition(images.size.minus(1))

        etProductType.setText(sb)
        etProductPrice.setText(product.detail?.price.toString())
        etProductStock.setText(product.detail?.stock.toString())
        etProductWeight.setText(product.detail?.weight.toString())
        etProductDescription.setText(product.detail?.description)
        etProductDiscount.setText(product.detail?.discount.toString())
        etProductUnit.setText(product.detail?.unit)
        if(product.status?.highlight == true){
            etHighLight.setText(getString(R.string.text_yes))
        }else{
            etHighLight.setText(getString(R.string.text_no))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickDelete(position: Int) {
        if(isEdit){
            deleteImagesArray.add(uploadImagesArray[position])
            uploadImagesArray.removeAt(position)
        }
        imagesData.removeAt(position)
        imagesAdapter?.notifyDataSetChanged()
    }

    private fun uploadImageFirebase(publish : Boolean) {
        loadingIndicator(true)
        for (i in 0 until imagesData.size) {
            if(imagesData[i].isUpload){
                val uri: Uri = imagesData[i].uri
                val bitmap = getBitmap(uri)//MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                val uri2 : Uri? = bitmap?.let { bitmapToUriConverter(it) }

                val ref: StorageReference = FirebaseStorage.getInstance()
                    .reference.child("image/product/"+getUser?.profile?.userId+"/"+ UUID.randomUUID())

                if (uri2 != null) {
                    ref.putFile(uri2)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { downloadPhotoUrl ->

                                uploadImagesArray.add(downloadPhotoUrl.toString())
                                var totalUpload2 = 0;
                                for (i in 0 until imagesData.size) {
                                    val list1 = listOf(imagesData[i].isUpload)
                                    val predicate: (Boolean) -> Boolean = {it == true}
                                    totalUpload2 = list1.count(predicate)
                                }

                                if(totalUpload==totalUpload2){

                                    if(publish){
                                        mPostProductPresenter.checkData(isEdit)
                                    }else{
                                        mPostProductPresenter.postProduct(isEdit, isDraft = true)
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {
                            loadingIndicator(false)
                        }
                        .addOnProgressListener { taskSnapshot ->
                            //  setToast("upload image.....")

                        }
                }
            }
        }
    }
    private fun getBitmap(fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }

    private fun uploadImageFirebasezz(data: Intent?) {
        loadingIndicator(true)
        val allData: Int = data?.clipData!!.itemCount
        for (i in 0 until allData) {

            val uri: Uri = data.clipData!!.getItemAt(i).uri
            val bitmap = getBitmap(uri)//MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val uri2 : Uri? = bitmap?.let { bitmapToUriConverter(it) }

            val ref: StorageReference = FirebaseStorage.getInstance()
                .reference.child("image/product/"+getUser?.profile?.userId+"/"+ UUID.randomUUID())

            uri2?.let {
                ref.putFile(it)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { downloadPhotoUrl ->
                            //Toast.makeText(this, downloadPhotoUrl.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("urlnya",downloadPhotoUrl.toString())
                            uploadImagesArray.add(downloadPhotoUrl.toString())
                            /*when (i) {
                                0 -> {
                                    url1 = downloadPhotoUrl.toString()
                                    Log.d("url1",url1+"--")
                                }
                                1 -> {
                                    url2 = downloadPhotoUrl.toString()
                                    Log.d("url2",url2+"--")
                                }
                                else -> {
                                    url3 = downloadPhotoUrl.toString()
                                    Log.d("url3",url3+"--")
                                }
                            }*/
                            Log.d("posisi i",i.toString()+"---")
                            Log.d("posisi all",data.clipData!!.itemCount.toString())
                            val beres = (data.clipData?.itemCount)?.minus(1)
                            if(i==beres){
                                mPostProductPresenter.checkData(isEdit)
                            }
                        }
                    }
                    .addOnFailureListener {
                        loadingIndicator(false)
                    }
                    .addOnProgressListener { taskSnapshot ->
                        //  setToast("upload image.....")

                    }
            }

        }
    }

}