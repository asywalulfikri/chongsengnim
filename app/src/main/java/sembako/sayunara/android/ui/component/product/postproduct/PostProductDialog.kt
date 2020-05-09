package sembako.sayunara.android.ui.component.product.postproduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.rahman.dialog.Utilities.SmartDialogBuilder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_product.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.product.postproduct.adapter.PodImageAdapter
import sembako.sayunara.android.ui.component.product.postproduct.model.PodImage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PostProductDialog (private val context: PostProductActivity){
    private var tagsList = arrayOf("daging", "minuman", "bumbu", "buah", "sayuran","sembako","kemasan","paket")
    private lateinit var isSelectedArray: BooleanArray
    private var mSelectedItems: ArrayList<Int>? = ArrayList()
    private var mImageListPhoto = ArrayList<PodImage>()
    private var mAdapterPhoto: PodImageAdapter? = null
    private val mRequestCamera = 0
    val PICK_IMAGE = 7458
    val CAPTURE_IMAGE = 7459

    fun showDialogType() {
        isSelectedArray = BooleanArray(200)
        Arrays.fill(isSelectedArray, java.lang.Boolean.FALSE)
        mSelectedItems = ArrayList()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.text_choose_category))
                .setMultiChoiceItems(tagsList, isSelectedArray) { _, which, isChecked ->
                    if (isChecked) {
                        isSelectedArray[which] = true
                    }
                }
                .setPositiveButton(context.getString(R.string.text_ok)) { dialog, _ ->
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
                    for (ii in strArray.indices) { // String tipex;
                        context.arrayType.add(strArray[ii].trim { it <= ' ' })
                    }
                    context.etProductType.setText(sb)
                    Log.e("tags", mSelectedItems.toString())
                    dialog.dismiss()
                }
                .setNegativeButton(context.getString(R.string.text_cancel)) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    fun takeImage() {
        /* if (mImageListPhoto.size > 2) {
             context.setToast(R.string.text_max_photo_product)
         } else {
             val popup = PopupMenu(context,context.ivImage)
             popup.inflate(R.menu.menu_photo_chooser)
             popup.setOnMenuItemClickListener { item: MenuItem ->
                 when (item.itemId) {
                     R.id.action_browse -> EasyImage.openGallery(context, AddProductActivity.PICK_IMAGE)
                     R.id.action_take -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                         PermissionHelper.requestCameraStorage(context) { accepted: Boolean ->
                             if (accepted) {
                                 val intent = Intent(context, CameraActivity::class.java)
                                 context.startActivityForResult(intent, AddProductActivity.CAPTURE_IMAGE)
                             } else {
                                 val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                 val uri = Uri.fromParts("package", context.packageName, null)
                                 intent.data = uri
                                 context.startActivityForResult(intent, 123)
                                 context.setToast(R.string.text_permission_camera_storage)
                             }
                         }
                     } else {
                         val intent = Intent(context, CameraActivity::class.java)
                         context.startActivityForResult(intent, AddProductActivity.CAPTURE_IMAGE)
                     }
                 }
                 true
             }
             popup.show()
         }*/
    }


    @SuppressLint("InflateParams")
    fun dialogUrl(type : Int) {
        val image: ImageView
        val progressBar: ProgressBar
        val ivClose : ImageView
        when (type) {
            1 -> {
                image = context.ivFirst
                progressBar = context.progressBar1
                ivClose = context.ivCloseFirst
            }
            2 -> {
                image = context.ivSecond
                progressBar = context.progressBar2
                ivClose = context.ivCloseSecond
            }
            else -> {
                image = context.ivThird
                progressBar = context.progressBar3
                ivClose = context.ivCloseThird
            }
        }
        val inflater = LayoutInflater.from(context)
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


        if(context.isEdit){
            when (type){
                1 -> {
                    etUrl.setText(context.product.images[0])
                }
                2 -> {
                    if(context.product.images.size>1){
                        etUrl.setText(context.product.images[1])
                    }
                }
                3 -> {
                    if(context.product.images.size>2){
                        etUrl.setText(context.product.images[2])
                    }
                }
            }

        }

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
                .setNegativeButton(context.getString(R.string.text_cancel)) {
                    dialog, _ ->
                    dialog.dismiss()
                    context.hideKeyboard()
                }
                .setPositiveButton(context.getString(R.string.text_save)
                ) { dialog, _ ->
                    dialog.dismiss()
                    val url = etUrl.text.toString()
                    if(url.isNotEmpty()){
                        setProgressImage(true,progressBar)
                        setImage(type,url,image,progressBar,ivClose)
                        context.hideKeyboard()
                        when (type) {
                            1 -> { context.url1 = url }
                            2 -> { context.url2 = url }
                            else -> { context.url3= url }
                        }
                    }

                }
        builder.create().show()
    }


    fun setImage(type : Int ,url : String,image: ImageView,progressBar: ProgressBar,ivClose : ImageView){
        Picasso.get()
                .load(url)
                .into(image, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        ivClose.visibility = View.VISIBLE
                        image.visibility = View.VISIBLE
                    }

                    override fun onError(e: java.lang.Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(context,"Foto tidak di temukan, ganti foto lain",Toast.LENGTH_SHORT).show()
                    }
                })


        ivClose.setOnClickListener {
            image.visibility = View.GONE
            ivClose.visibility=View.GONE

            when (type){
                1 -> { context.url1 = "" }
                2 ->{ context.url2 = "" }
                else-> {context.url3 = ""}
            }
        }
    }

    private fun setProgressImage(status : Boolean,progressBar: ProgressBar){
        if(status){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }

    }
    fun showUnitDialog() {
        val type = arrayOf("Ekor", "Kg", "Ons", "Kwintal", "Gram", "Ml", "Pon", "Pack", "Bungkus","Ikat","Sisir","Potong","Liter")
        val builder: android.app.AlertDialog.Builder = context.getBuilder(context)
        builder.setTitle(App.application!!.getString(R.string.text_product_unit))
                .setItems(type) { _, which -> context.etProductUnit.setText(type[which]) }
        builder.create().show()
    }


    fun dialogHighLight(){
        SmartDialogBuilder(context)
                .setTitle(context.getString(R.string.text_notification))
                .setSubTitle(context.getString(R.string.text_confirm_highlight))
                .setCancalable(false)
                .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
                .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
                .setCancalable(true)
                .setNegativeButtonHide(false) //hide cancel button
                .setPositiveButton(context.getString(R.string.text_yes)) { smartDialog ->
                    context.highLight = true
                    context.etHighLight.setText(context.getString(R.string.text_yes))
                    smartDialog.dismiss()
                }.setNegativeButton(context.getString(R.string.text_no)) { smartDialog ->
                    context.etHighLight.setText(context.getString(R.string.text_no))
                    context.highLight = false
                    smartDialog.dismiss()
                }.build().show()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE) {
            val path: String = data!!.getStringExtra("path")
            val file = File(path)
            val uri = Uri.fromFile(file)
            val podImage = PodImage()
            podImage.source = uri
            mImageListPhoto.add(podImage)
            mAdapterPhoto!!.notifyDataSetChanged()
        }
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, context, object : DefaultCallback() {
                override fun onImagesPicked(imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int) {
                    /* CropImage.activity(Uri.parse(imageFiles[type].toURI().toString()))
                             .start(context)*/
                }

                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource, type: Int) {
                    e.printStackTrace()
                }

                /* fun onImagePicked(imageFile: File, source: EasyImage.ImageSource, type: Int) {
                     CropImage.activity(Uri.parse(imageFile.toURI().toString()))
                             .start(context)
                 }*/
            })
        } else if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            val uri = result.uri
            val podImage = PodImage()
            podImage.source = uri
            mImageListPhoto.add(podImage)
            mAdapterPhoto!!.notifyDataSetChanged()
        } else if (requestCode == mRequestCamera) {
            val uri: Uri = data?.data!!
            val podImage = PodImage()
            podImage.source = uri
            mImageListPhoto.add(podImage)
            mAdapterPhoto!!.notifyDataSetChanged()
        } else if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {
            val path: String = data!!.getStringExtra("path")
            if (path != null || path != "") {
                val file = File(path)
                val uri = Uri.fromFile(file)
                val podImage = PodImage()
                podImage.source = uri
                mImageListPhoto.add(podImage)
                mAdapterPhoto!!.notifyDataSetChanged()
            }
        }
    }

}