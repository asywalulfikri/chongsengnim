package sembako.sayunara.android.ui.component.basket

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.activity_list_detail_basket.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.blur.BlurBehind
import sembako.sayunara.android.helper.blur.OnBlurCompleteListener
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFirstActivity
import sembako.sayunara.android.ui.component.basket.adapter.DetailBasketAdapter
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.android.ui.component.transaction.ConfirmationPaymentActivity
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BasketListDetailActivity : BaseActivity(),BasketViewDetail,DetailBasketAdapter.OnClickListener{

    private lateinit var detailBasketAdapter : DetailBasketAdapter
    private val basketServices = BasketServices()
    var basketArrayList: ArrayList<Basket> = ArrayList()
    var pesanan: ArrayList<String> = ArrayList()
    var handler : Handler? = Handler()
    var listBasket : ListBasket? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail_basket)
        setupToolbar(toolbar)

        listBasket = intent.getSerializableExtra("basket") as ListBasket;

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            detailBasketAdapter = DetailBasketAdapter()
            adapter = detailBasketAdapter
        }


        if(isLogin()){
            getList()
            swipeRefresh.setOnRefreshListener {
               getList()
            }
        }else{
            layoutEmpty.visibility =View.VISIBLE
            textViewEmptyList.text = "Masuk Terlebih dahulu"
            btnOrder.visibility = View.GONE
        }
    }

    fun getList(){
        loadingIndicator(true)
        basketServices.getBasketDetail(this, FirebaseFirestore.getInstance(), getUsers?.profile?.userId.toString(),listBasket?.id.toString())
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
            layoutProgress.visibility = View.VISIBLE
        }else{
            layoutProgress.visibility = View.GONE
        }

    }

    override fun onRequestSuccess(basketArrayList: ArrayList<Basket>) {

        this.basketArrayList = basketArrayList
        getDetailPerList(basketArrayList)

        if(basketArrayList.size==0){
            layoutEmpty.visibility = View.VISIBLE
            btnOrder.visibility =View.GONE
            llBasket.visibility = View.GONE
            layoutProgress.visibility = View.GONE
            btnBuy.visibility = View.VISIBLE
            btnBuy.setOnClickListener {
                val intent = Intent()
                intent.putExtra("isBack",true)
                setResult(RESULT_OK,intent)
                finish()
            }
        }
    }

    private fun showHideButton(){
        if(basketArrayList.size>0){
            layoutEmpty.visibility = View.GONE
            btnOrder.visibility =View.VISIBLE
            llBasket.visibility = View.VISIBLE
        }else{
            layoutEmpty.visibility = View.VISIBLE
            btnOrder.visibility =View.GONE
            llBasket.visibility = View.GONE
        }
        layoutProgress.visibility = View.GONE
    }



    private fun getDetailPerList(basketArrayList: ArrayList<Basket>){

        swipeRefresh.isRefreshing = false

        val productArrayList: ArrayList<Product?> = ArrayList()
        for ( i in 0 until basketArrayList.size) {
            val basket1 = basketArrayList[i]
            var isi = ""
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT).document(basket1.productId.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if(task.result!!.exists()){
                        val product = task.result!!.toObject(Product::class.java)
                        Log.d("eksekusi", product?.detail?.name + "xx")
                        product?.let { productArrayList.add(it) }
                        isi =  "- "+product?.detail?.name.toString() +" "+ basket1.quantity!!.toInt().toString()+" "+ product?.detail?.unit +" "+ product?.detail?.price+"\n"
                        pesanan.add(isi)

                    }

                    Handler().postDelayed({
                        //doSomethingHere()
                        this.basketArrayList = basketArrayList

                        if(this.basketArrayList.isEmpty()){
                            layoutEmpty.visibility =View.VISIBLE
                            textViewEmptyList.text = "Keranjang Masih Kosong"
                        }


                        detailBasketAdapter.setItems(this.basketArrayList, this, productArrayList)
                       // recyclerView.adapter = detailBasketAdapter
                        showHideButton()
                        loadingIndicator(false)

                    }, 1000)



                    val builder = StringBuilder()
                    for (details in pesanan) {
                        builder.append(details + "\n")
                    }

                    val message = "Hallo Sayunara saya ingin order \n\n" + builder.toString() + " \nMohon di response segera ya\n \nSilakan tambahkan order lainya disini....\n"


                    btnOrder.setOnClickListener {
                        val intent = Intent(this, ConfirmationPaymentActivity ::class.java)
                        startActivity(intent)
                    }

                        /*if(getUsers!!.isLogin){
                            try {
                                val packageManager = activity.packageManager
                                val i = Intent(Intent.ACTION_VIEW)

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
                        }*/

                }
            }


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra("load",false)
        setResult(RESULT_OK,intent)
        finish()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addTransaction(desc : String){

        val obj: MutableMap<String?, Any?> = HashMap()
        val uuid = UUID.randomUUID().toString()

        obj["id"] = uuid
        obj["description"] = desc
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

        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET).document(uuid)
            .set(obj)
            .addOnSuccessListener {
                setToast("Telah Ditambahkan Ke keranjang")
                // view?.loadingIndicator(false)
                // onRequestSuccess()
            }
            .addOnFailureListener { e ->
                setToast(e.message.toString())
                //view?.loadingIndicator(false)
                // onRequestFailed(e.hashCode())
            }
    }

    @SuppressLint("InflateParams")
    override fun showDialogLogin(message: String?) {
        BlurBehind.instance!!.execute(activity as AppCompatActivity?, object : OnBlurCompleteListener {
            override fun onBlurComplete() {
                val intent = Intent(activity, LoginFirstActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivityForResult(intent, 1313)
            }
        })
    }


    override fun onRequestFailed(code: Int?) {
        // TODO("Not yet implemented")
    }

    override fun setupViews() {
        // TODO("Not yet implemented")
    }

    override fun onClickBasket(position: Int) {
        // TODO("Not yet implemented")
    }

    override fun onClickBasketPlus(position: Int, product: Product?, basket: Basket) {
        updateQuantit(position, true,basket)
    }
    override fun onClickBasketMinus(position: Int, product: Product?, basket: Basket) {
        updateQuantit(position, false,basket)
    }

    override fun onClickDelete(position: Int, basket: Basket) {
        dialog(position, basket)
    }

    private fun dialog(position: Int, basket: Basket){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete)+ basket.quantity)
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                deleteProduct(position, basket)
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }


    private fun deleteProduct(position: Int, basket: Basket){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).document(listBasket?.id.toString()).collection(Constant.Collection
            .COLLECTION_BASKET_PRODUCT_LIST).document(basket.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Produk Telah di Hapus", Toast.LENGTH_LONG).show()
                basketArrayList.removeAt(position)
                detailBasketAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Gagal menghapus produk " + e.message, Toast.LENGTH_LONG).show()
            }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun updateQuantit(position: Int, plus: Boolean,basket: Basket){

        val basketssss = this.basketArrayList[position]
        var total = basketssss.quantity!!.toInt()
        if(plus){
            total++
        }else{
            if(total==0){
                //habisss
            }else{
                total--
            }
        }
        this.basketArrayList[position].quantity = total.toDouble()
        detailBasketAdapter.notifyDataSetChanged()

        updateQuantity(basket,total)



    }





    private fun updateQuantity(basket: Basket, total: Int){
        val obj: MutableMap<String?, Any?> = HashMap()
        obj["quantity"] = total
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).document(listBasket?.id.toString())
            .collection(Constant.Collection.COLLECTION_BASKET_PRODUCT_LIST)
            .document(basket.id.toString())
            .set(obj, SetOptions.merge())
            .addOnSuccessListener {
                //setToast("Berubah")
                //success
            }
            .addOnFailureListener { e ->
                onRequestFailed(e.hashCode())
                setToast(e.message.toString())
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers!!.profile.userId.toString())

    }



}
