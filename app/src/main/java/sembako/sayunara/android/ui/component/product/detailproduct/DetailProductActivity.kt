package sembako.sayunara.android.ui.component.product.detailproduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.cunoraz.tagview.Tag
import com.google.gson.Gson
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_scrolling.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.net.URLEncoder
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface

import com.google.firebase.firestore.*
import sembako.sayunara.android.ui.component.product.favorite.model.Favorite

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*


class DetailProductActivity : BaseActivity() {

    var product: Product? = null
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private var load = false
    private var isBasketEmpty : Boolean? = null
    val basketArrayList: ArrayList<ListBasket> = ArrayList()
    var quantity = 0

    private var isLike = false
    private var favoriteId : String? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        toolbar(toolbar)

        product = intent.getSerializableExtra("product") as Product

        checkBasket()
        checkStatusProduct()
        checkFavorite()

        swipeRefresh.setOnRefreshListener {
            detail
            checkBasket()
        }
    }

    private fun setupBanner(product: Product?){
        if(product?.detail?.images?.size!! >0){
            if(product.detail?.images!![0].isNotEmpty()){
                val list: MutableList<String> = ArrayList()
                for (i in product.detail?.images?.indices!!) {
                    list.add(product.detail?.images!![i])

                }
                banner_1.setImagesUrl(list)
                pageIndicatorView.count =2
                pageIndicatorView.radius = 4
                pageIndicatorView.setViewPager(banner_1.mViewPager)
            }
        }
    }


    private val detail: Unit
        get() {
            firebaseFirestore.collection(Constant.Collection.COLLECTION_PRODUCT).document(product!!.id!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    product = task.result!!.toObject(Product::class.java)
                    swipeRefresh.isRefreshing = false
                    updateView(product)
                } else {
                    swipeRefresh.isRefreshing = false
                    setToast(task.exception!!.message.toString())
                }
            }
        }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateView(product: Product?) {
        toolbar_layout.title = product!!.detail?.name.toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tvProductName.text = product.detail?.name
        //tvProductPrice.text = "Rp" + convertPrice(product.price.toString().toInt())
        tvProductDescription.text = product.detail?.description
        tvProductUnit.text = product.detail?.unit
        tv_product_stock.text = product.detail?.stock.toString()

        setupBanner(product)

        val harga = product.detail?.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)



        if (product.detail?.discount == 0L) {
            tvProductPrice.text = "Rp$k /"
            tvProductUnit.text = product.detail?.weight.toString() + " " + product.detail?.unit

        } else {
            tvProductDiscount.visibility = View.VISIBLE
            val price = product.detail?.price.toString().toInt()
            val discount = product.detail?.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            tvProductDiscount.paintFlags = tvProductDiscount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            if (product.detail?.weight!! <= 1) {
                tvProductDiscount.text = "Rp" + k + " / " + product.detail?.unit
                tvProductUnit.text = product.detail?.unit
            } else {
                tvProductDiscount.text = "Rp" + k + " / " + product.detail?.weight + " " + product.detail?.unit
                tvProductUnit.text = product.detail?.weight.toString() + " " + product.detail?.unit
            }
            tvProductPrice.text = "Rp" + df.format(amount) + " /"
        }

        if (product.detail?.type == null || product.detail?.type?.size === 0) {
            tag_group.visibility = View.GONE
        } else {
            tag_group.visibility = View.VISIBLE
            tag_group.removeAll()
            for (i in 0 until product.detail?.type!!.size) {
                val tag = Tag(product.detail?.type!![i])
                tag.radius = 10f
                tag.layoutColor = Color.parseColor("#89C23F")
                tag.layoutBorderColor = Color.parseColor("#9E9D24")
                tag.layoutBorderSize = 1f
                tag.tagTextColor = Color.parseColor("#FFFFFF")
                tag.tagTextSize = 12f
                tag_group.addTag(tag)
            }
        }


        if(getSuperAdmin()){
            ll_admin.visibility = View.VISIBLE
            ll_user.visibility = View.GONE
        }else{
            ll_admin.visibility = View.VISIBLE
            ll_user.visibility = View.GONE
        }

        btnEdit.setOnClickListener {
            /*val intent = Intent(this, PostProductActivity::class.java)
            intent.putExtra(Constant.IntentExtra.product,product)
            startActivityForResult(intent,Constant.Code.CODE_EDIT)*/

        }

        layout_progress.visibility = View.GONE


        btnDelete.setOnClickListener {
            dialog()
        }


        btnChat.setOnClickListener {
            comingSoon()
            /* val intent = Intent(this,MessageActivity::class.java)
             startActivity(intent)*/
        }


        btnSubmit!!.setOnClickListener {

            if(getUsers!!.isLogin){
                try {
                    val packageManager = activity.packageManager
                    val i = Intent(Intent.ACTION_VIEW)
                    val message = "Hallo Sayunara saya ingin order " + product.detail?.name + " mohon di response segera ya\n \nSilakan tambahkan order lainya disini..... \n \n \nSelamat Berbelanja di Sayunara"
                    val url = "https://api.whatsapp.com/send?phone=" + "6281293239009" + "&text=" + URLEncoder.encode(message, "UTF-8")
                    i.setPackage("com.whatsapp")
                    i.data = Uri.parse(url)
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i)
                    } else {
                        // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    }
                } catch (e: Exception) {
                    Log.e("ERROR WHATSAPP", e.toString())
                    // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }
            }else{
                showDialogLogin("Login Bos")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.detail_menu, menu)
        val detail = menu!!.findItem(R.id.detail)


        Log.d("userYu",getUsers!!.profile.type.toString())
        detail.isVisible = getUsers!!.profile.type==Constant.userType.typeAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.edit_menu -> {
               /* val intent = Intent(this, PostProductActivity::class.java)
                intent.putExtra(Constant.IntentExtra.product,product)
                startActivityForResult(intent,Constant.Code.CODE_EDIT)*/

            }
            R.id.delete_menu -> {
                dialog()

            }
            else -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}


    private fun dialog(){
        SmartDialogBuilder(this)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete))
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                deleteProduct()
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }





    private fun dialogBasket() {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.dialog_basket, null)

        val btnMin = view.findViewById<Button>(R.id.btnMin)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val tvCount = view.findViewById<TextView>(R.id.tvCount)


        btnMin.setOnClickListener {
            if(quantity!=0){
                quantity--
            }
            tvCount.text = quantity.toString()
        }

        btnPlus.setOnClickListener {
            quantity++
            tvCount.text = quantity.toString()
        }


        val builder = getBuilder(activity)

        builder.setView(view)
            .setNegativeButton(getString(R.string.text_cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.text_add_basket)
            ) { _, _ ->

                if(isBasketEmpty==true){
                    //addProductInBasket(quantity)
                    dialogAddBasket()
                }else{
                    setupViewBasket(quantity)
                }
            }

        builder.create().show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Constant.Code.CODE_EDIT){
            var isLoad = data!!.getBooleanExtra(Constant.IntentExtra.isLoad,false)
            if(isLoad){
                load = true
                detail
            }
        }else if(requestCode == 1313){

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addProductInBasket(basketId : String ,quantity : Int){

        val obj: MutableMap<String?, Any?> = HashMap()
        val uuid = UUID.randomUUID().toString()
        obj["quantity"] = quantity
        obj["id"] = uuid
        obj["productId"] = product?.id
        obj["basketId"] = basketId
        obj["statusOrder"] = "basket"
        obj["isActive"] = true
        obj["userId"] = getUsers?.profile?.userId.toString()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val tsLong = System.currentTimeMillis() / 1000
        val tz = TimeZone.getTimeZone("UTC")
        df.timeZone = tz

        val dateSubmittedData: MutableMap<String, Any> = java.util.HashMap()
        dateSubmittedData[Constant.UserKey.iso] = nowAsISO
        dateSubmittedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateSubmittedData

        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET).document(basketId).collection(Constant.Collection.COLLECTION_BASKET_PRODUCT_LIST).document(uuid)
            .set(obj)
            .addOnSuccessListener {
                setToast("Produk Telah Ditambahkan Ke keranjang")
            }
            .addOnFailureListener { e ->
                setToast(e.message.toString())
            }

    }




    @SuppressLint("SimpleDateFormat")
    private fun addBasketName(basketName : String){

        val obj: MutableMap<String?, Any?> = HashMap()
        val uuid = UUID.randomUUID().toString()

        obj["id"] = getUsers?.profile?.username +"_"+uuid
        obj["basketName"] = basketName
        obj["statusOrder"] = "basket"
        obj["isActive"] = true
        obj["userId"] = getUsers?.profile?.userId.toString()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val tsLong = System.currentTimeMillis() / 1000
        val tz = TimeZone.getTimeZone("UTC")
        df.timeZone = tz

        val dateSubmittedData: MutableMap<String, Any> = java.util.HashMap()
        dateSubmittedData[Constant.UserKey.iso] = nowAsISO
        dateSubmittedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateSubmittedData

        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET).document(getUsers?.profile?.username +"_"+uuid)
            .set(obj)
            .addOnSuccessListener {
                setToast("Keranjang berhasil dibuat, Silakan mengisi dengan produk")
                checkBasket()
            }
            .addOnFailureListener { e ->
                setToast(e.message.toString())
                setToast("Keranjang gagal dibuat, Silakan coba lagi")
            }
    }



    private fun deleteProduct(){
        firebaseFirestore.collection(Constant.Collection.COLLECTION_PRODUCT).document(product!!.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Produk Telah di Hapus", Toast.LENGTH_LONG).show()
                load = true
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus produk "+ e.message, Toast.LENGTH_LONG).show()
            }
    }
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constant.IntentExtra.isLoad, load)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    @SuppressLint("InflateParams")
    fun dialogAddBasket() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_input_url, null)
        val etUrl = view.findViewById<EditText>(R.id.etUrlImage)
        val tvInfo = view.findViewById<TextView>(R.id.tv_info)
        val ivClear = view.findViewById<ImageView>(R.id.ivClear)
        etUrl.hint = "Nama keranjang"
        tvInfo.text = "Buat Nama Keranjang terlebih dahulu"

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


        val builder = AlertDialog.Builder(this)
        builder.setView(view)
            .setNegativeButton(getString(R.string.text_cancel)) {
                    dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.text_save)) { dialog, _ ->
                dialog.dismiss()
                val url = etUrl.text.toString()
                if(url.isNotEmpty()){
                    addBasketName(url)
                }

            }
        builder.create().show()
    }




    private fun checkBasket(){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).whereEqualTo("statusOrder","basket").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(task.result.isEmpty){
                    isBasketEmpty = true
                    dialogEmptyBasket()
                }else{
                    isBasketEmpty = false
                    for (doc in task.result!!) {
                        val basket = doc.toObject(ListBasket::class.java)
                        Log.d("aslm", Gson().toJson(basket))
                        basketArrayList.add(basket)
                    }

                    //setupViewBasket()
                }
                setupBanner(product)
                updateView(product)

                btnBasket.setOnClickListener {
                    if(isBasketEmpty as Boolean){
                        dialogAddBasket()
                    }else{
                        dialogBasket()
                    }

                }
            }
        }

    }

    private fun setupViewBasket(quantity: Int){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Pilih Keranjang")
        var basketId : String? =null


        val animalsArray = arrayOfNulls<String>(basketArrayList.size)
        val animalsArray1 = arrayOfNulls<String>(basketArrayList.size)
        for (i in 0 until basketArrayList.size) {
            animalsArray[i] = basketArrayList[i].basketName
            animalsArray1[i] = basketArrayList[i].id
        }
        var checkedItem = 0
        if(basketArrayList.size>0){
            checkedItem = -1
        }

        alertDialog.setSingleChoiceItems(animalsArray, checkedItem) {
                dialog, which ->
            basketId = animalsArray1[which]
            setToast(basketId.toString())

           // Log.e(TAG, "onClick: " + animalsArray[which])
        }
        alertDialog.setPositiveButton("Pilih", DialogInterface.OnClickListener { dialog, id ->
            setToast(basketId.toString())
            addProductInBasket(basketId.toString(),quantity)
            dialog.dismiss()
        })
        alertDialog.setNegativeButton("Batal", { dialog, id ->
            dialog.dismiss()
        })

        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

    }


    private fun checkStatusProduct(){

       /* setToast(product?.id.toString())
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET)
            .document()
            .collection(Constant.Collection.COLLECTION_BASKET_PRODUCT_LIST)
            .whereEqualTo("userId",getUsers?.profile?.userId)
            .whereEqualTo("productId",product?.id)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(task.result.isEmpty){
                   setToast("tidak ada")
                }else{

                    //setupViewBasket()
                }

            }
        }*/

    }

    private fun checkFavorite(){
        val rootRef = FirebaseFirestore.getInstance()
        val usersRef = rootRef.collection(Constant.Collection.COLLECTION_FAVORITE)
        val queryUsersByName: Query = usersRef
            .whereEqualTo("productId", product?.id.toString())
            .whereEqualTo("userId",getUsers?.profile?.userId)

        queryUsersByName.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fabNoLike.visibility = View.VISIBLE
                fabLike.visibility = View.GONE
                for (document in task.result!!) {

                    if (document.exists()) {
                        val favorite = document.toObject(Favorite::class.java)
                        Log.d("response",Gson().toJson(favorite))

                        favoriteId = favorite.id
                        isLike = true
                        fabNoLike.visibility = View.GONE
                        fabLike.visibility = View.VISIBLE
                    } else {
                        isLike = false
                        fabNoLike.visibility = View.VISIBLE
                        fabLike.visibility = View.GONE
                    }

                }



                fabNoLike.setOnClickListener {
                    fabNoLike.visibility = View.GONE
                    fabLike.visibility = View.VISIBLE
                    postFavorite(true)
                    setToast("Produk ditambahkan ke favorit")

                }

                fabLike.setOnClickListener {
                    fabNoLike.visibility = View.VISIBLE
                    fabLike.visibility = View.GONE
                    deleteFavorite()

                }
            } else {

                setToast(task.exception?.message.toString()+"---")
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }

    }


    private fun deleteFavorite(){
        firebaseFirestore.collection(Constant.Collection.COLLECTION_FAVORITE).document(favoriteId.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Produk dihapus dari favorit", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun postFavorite(isLike : Boolean){
        val obj: MutableMap<String?, Any?> = HashMap()
        val uuid = UUID.randomUUID().toString()
        favoriteId = uuid
        obj["id"] = uuid
        obj["productId"] = product?.id
        obj["isLike"] = isLike
        obj["userId"] = getUsers?.profile?.userId.toString()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val tsLong = System.currentTimeMillis() / 1000
        val tz = TimeZone.getTimeZone("UTC")
        df.timeZone = tz

        val dateSubmittedData: MutableMap<String, Any> = java.util.HashMap()
        dateSubmittedData[Constant.UserKey.iso] = nowAsISO
        dateSubmittedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.createdAt] = dateSubmittedData



        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_FAVORITE).document(uuid)
            .set(obj)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                setToast(e.message.toString())
            }

    }


}