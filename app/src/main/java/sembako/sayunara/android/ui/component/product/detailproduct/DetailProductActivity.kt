package sembako.sayunara.android.ui.component.product.detailproduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.cunoraz.tagview.Tag
import com.google.firebase.firestore.FirebaseFirestore
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.content_scrolling.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.android.ui.component.product.postproduct.PostProductActivity
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class DetailProductActivity : BaseActivity() {

    var product: Product? = null
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private var load = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        toolbar(toolbar)

        product = intent.getSerializableExtra("product") as Product

        updateView(product)
        setupBanner(product)

        swipeRefresh.setOnRefreshListener {
            detail
        }
    }

    private fun setupBanner(product: Product?){
        if(product!!.images[0].isNotEmpty()){
            val list: MutableList<String> = ArrayList()
            for (i in product!!.images.indices) {
                list.add(product!!.images[i])

            }
            banner_1.setImagesUrl(list)
            pageIndicatorView.count =2
            pageIndicatorView.radius = 4
            pageIndicatorView.setViewPager(banner_1.mViewPager)
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

    @SuppressLint("SetTextI18n")
    private fun updateView(product: Product?) {
        progressBar.visibility = View.GONE
        toolbar_layout.title = product!!.name.toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tvProductName.text = product.name
        //tvProductPrice.text = "Rp" + convertPrice(product.price.toString().toInt())
        tvProductDescription.text = product.description
        tvProductUnit.text = product.unit
        tv_product_stock.text = product.stock.toString()

        setupBanner(product)

        val harga = product.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)



        if (product.discount == 0L) {
            tvProductPrice.text = "Rp$k /"
            tvProductUnit.text = product.weight.toString() + " " + product.unit

        } else {
            tvProductDiscount.visibility = View.VISIBLE
            val price = product.price.toString().toInt()
            val discount = product.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            tvProductDiscount.paintFlags = tvProductDiscount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            if (product.weight!! <= 1) {
                tvProductDiscount.text = "Rp" + k + " / " + product.unit
                tvProductUnit.text = product.unit
            } else {
                tvProductDiscount.text = "Rp" + k + " / " + product.weight + " " + product.unit
                tvProductUnit.text = product.weight.toString() + " " + product.unit
            }
            tvProductPrice.text = "Rp" + df.format(amount) + " /"
        }

        if (product.type == null || product.type.size === 0) {
            tag_group.visibility = View.GONE
        } else {
            tag_group.visibility = View.VISIBLE
            tag_group.removeAll()
            for (i in 0 until product.type.size) {
                val tag = Tag(product.type[i])
                tag.radius = 10f
                tag.layoutColor = Color.parseColor("#89C23F")
                tag.layoutBorderColor = Color.parseColor("#9E9D24")
                tag.layoutBorderSize = 1f
                tag.tagTextColor = Color.parseColor("#FFFFFF")
                tag.tagTextSize = 12f
                tag_group.addTag(tag)
            }
        }
        button_submit!!.setOnClickListener {

            if(user!!.isLogin){
                try {
                    val packageManager = activity.packageManager
                    val i = Intent(Intent.ACTION_VIEW)
                    val message = "Hallo Sayunara saya ingin order " + product.name + " mohon di response segera ya\n \nSilakan tambahkan order lainya disini..... \n \n \nSelamat Berbelanja di Sayunara"
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


        Log.d("userYu",user!!.profile.type)
        detail.isVisible = user!!.profile.type==Constant.Session.userTypeAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.edit_menu -> {
                val intent = Intent(this,PostProductActivity::class.java)
                intent.putExtra(Constant.IntentExtra.product,product)
                startActivityForResult(intent,Constant.Code.CODE_EDIT)

            }
            R.id.delete_menu -> {
                dialog()

            }
            else -> {
               // finish()
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
}