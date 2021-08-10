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
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.blur.BlurBehind
import sembako.sayunara.android.helper.blur.OnBlurCompleteListener
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFirstActivity
import sembako.sayunara.android.ui.component.basket.adapter.BasketAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.net.URLEncoder
import java.util.*


class BasketActivity : BaseActivity(),BasketView,BasketAdapter.OnClickListener{

    private lateinit var basketAdapter : BasketAdapter
    val basketServices = BasketServices()
    var basketArrayList: ArrayList<Basket> = ArrayList()
    var pesanan: ArrayList<String> = ArrayList()
    var handler : Handler? = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_list)

        setupToolbar(toolbar)

        recyclerView.run {
            layoutManager = LinearLayoutManager(activity);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            basketAdapter = BasketAdapter()
            adapter = basketAdapter
        }


        if(isLogin()){

            basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers!!.profile.userId.toString())


            swipeRefresh.setOnRefreshListener {
                basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers!!.profile.userId.toString())
            }
        }else{
            layout_empty.visibility =View.VISIBLE
            textViewEmptyList.text = "Masuk Terlebih dahulu"
            btnOrder.visibility = View.GONE
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

    }

    override fun onRequestSuccess(basketArrayList: ArrayList<Basket>) {

        getDetailPerList(basketArrayList)

        if(basketArrayList.size>0){
            layout_empty.visibility = View.GONE
            btnOrder.visibility =View.VISIBLE
        }else{
            layout_empty.visibility = View.VISIBLE
            btnOrder.visibility =View.GONE
        }
    }



    private fun getDetailPerList(basketArrayList: ArrayList<Basket>){

        swipeRefresh.isRefreshing = false
        layout_progress.visibility = View.GONE

        val productArrayList: ArrayList<Product> = ArrayList()
        Log.d("eksekusi", "hhehheheh")
        for ( i in 0 until basketArrayList.size) {
            var basket1 = basketArrayList[i]
            Log.d("eksekusi --", basket1.productId + "xx")

            var isi = ""
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT).document(basket1.productId.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(task.result!!.exists()){
                        val product = task.result!!.toObject(Product::class.java)
                        Log.d("eksekusi", product!!.name + "xx")
                        productArrayList.add(product)
                        isi =  "- "+product.name.toString() +" "+ basket1.quantity!!.toInt().toString()+" "+ product.unit +"\n"
                        pesanan.add(isi)

                    }
                    // basketArrayList.addAll(basketArrayList)
                    this.basketArrayList = basketArrayList

                    if(this.basketArrayList.isEmpty()){
                        layout_empty.visibility =View.VISIBLE
                        textViewEmptyList.text = "Keranjang Masih Kosong"
                    }

                    basketAdapter.setItems(this.basketArrayList, this, productArrayList)

                    val builder = StringBuilder()
                    for (details in pesanan) {
                        builder.append(details + "\n")
                    }

                    val message = "Hallo Sayunara saya ingin order \n\n" + builder.toString() + " \nMohon di response segera ya\n \nSilakan tambahkan order lainya disini....\n"


                    btnOrder.setOnClickListener {
                        if(getUsers!!.isLogin){
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
                        }
                    }


                } else {

                }
            }


        }

       /* if(basketAdapter.itemCount>0){
            layout_empty.visibility = View.GONE
        }else{
            layout_empty.visibility = View.VISIBLE
        }*/


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

    override fun onClickBasketPlus(position: Int, product: Product, basket: Basket) {
        updateQuantit(position, true)
    }
    override fun onClickBasketMinus(position: Int, product: Product, basket: Basket) {
       updateQuantit(position, false)
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
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).document(basket.id.toString())
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(activity, "Produk Telah di Hapus", Toast.LENGTH_LONG).show()
                   // recyclerView.adapter = null
                   // basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers!!.profile.userId.toString())
                    basketArrayList.removeAt(position-1)
                    basketAdapter.notifyDataSetChanged()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Gagal menghapus produk " + e.message, Toast.LENGTH_LONG).show()
                }
    }


    private fun updateQuantit(position: Int, plus: Boolean){

        var basketssss = this.basketArrayList[position]
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
        basketAdapter.notifyDataSetChanged()

        myRunnable
        start()

    }


    var myRunnable = Runnable {
        // your code here
       // setToast("xxxx")
    }

    var myHandler = Handler()
    private val TIME_TO_WAIT = 3000

    fun start() {
        myHandler.postDelayed(myRunnable, TIME_TO_WAIT.toLong())
    }

    fun stop() {
        myHandler.removeCallbacks(myRunnable)
    }

    fun restart() {
        myHandler.removeCallbacks(myRunnable)
        myHandler.postDelayed(myRunnable, TIME_TO_WAIT.toLong())
    }



    private fun updateQuantity(basket: Basket, total: Int){

        val obj: MutableMap<String?, Any?> = HashMap()
        obj["quantity"] = total
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).document(basket.id.toString())
                .set(obj, SetOptions.merge())
                .addOnSuccessListener {
                 setToast("suksessss")
                }
                .addOnFailureListener { e ->
                    onRequestFailed(e.hashCode())
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       // basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers!!.profile.userId.toString())

    }



}
