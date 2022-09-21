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
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
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
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BasketListActivity : BaseActivity(),BasketView,BasketAdapter.OnClickListener{

    private var basketAdapter = BasketAdapter()
    val basketServices = BasketServices()
    var handler : Handler? = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_list)

        setupToolbar(toolbar)
        setupRecyclerView(recyclerView)


        if(isLogin()){

            loadBasket()
            swipeRefresh.setOnRefreshListener {
                loadBasket()
            }

        }else{

            layoutEmpty.visibility =View.VISIBLE
            textViewEmptyList.text = "Masuk Terlebih dahulu"
        }

        btnAddBasket.setOnClickListener {
            if(basketAdapter.itemCount==8){
                setToast("Maksimal 8 Keranjang")
            }else{
                dialogAddBasket()
            }
        }
    }

    private fun loadBasket(){
        hideLoadingDialog()
        layoutProgress.visibility = View.VISIBLE
        basketServices.getBasket(this, FirebaseFirestore.getInstance(), getUsers?.profile?.userId.toString())
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

    override fun onRequestSuccess(basketArrayList: ArrayList<ListBasket>) {

        getDetailPerList(basketArrayList)

        if(basketArrayList.size>0){
            layoutEmpty.visibility = View.GONE
        }else{
            layoutEmpty.visibility = View.VISIBLE
            textViewEmptyList.text = getString(R.string.empty_basket)
        }
        btnAddBasket.visibility = View.VISIBLE
    }



    private fun getDetailPerList(basketArrayList: ArrayList<ListBasket>){

        swipeRefresh.isRefreshing = false
        layoutProgress.visibility = View.GONE
        basketAdapter.setItems(basketArrayList, this)
        recyclerView.adapter = basketAdapter
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


    override fun onClickDetail(position: Int, basket: ListBasket) {
        val intent = Intent(this,BasketListDetailActivity::class.java)
        intent.putExtra("basket",basket)
        startActivityForResult(intent,1212)
    }

    override fun onDeleteBasket(position: Int, basket: ListBasket) {
        dialog(basket)
    }


    private fun dialog( basket: ListBasket){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle("Apakah anda akan menghapus keranjang ini")
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                deleteProduct(basket)
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }


    private fun deleteProduct(basket: ListBasket){
        FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_BASKET).document(basket.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Keranjang Telah di Hapus", Toast.LENGTH_LONG).show()
                loadBasket()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Gagal menghapus keranjang " + e.message, Toast.LENGTH_LONG).show()
            }
    }


    @SuppressLint("SimpleDateFormat")
    fun addBasketName(basketName : String){

        showLoadingDialog()

        val obj: MutableMap<String?, Any?> = HashMap()
        val uuid = UUID.randomUUID().toString()

        obj["id"] = uuid
        obj["basketName"] = basketName
        obj["statusOrder"] = "basket"
        obj["isActive"] = true
        obj["userId"] = getUsers?.profile?.userId.toString()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val nowAsISO = df.format(Date())
        val tsLong = System.currentTimeMillis() / 1000
        val tz = TimeZone.getTimeZone("UTC")
        df.timeZone = tz

        val updatedAt: MutableMap<String, Any> = java.util.HashMap()
        updatedAt[Constant.UserKey.iso] = nowAsISO
        updatedAt[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = updatedAt

        val createdAt: MutableMap<String, Any> = java.util.HashMap()
        createdAt[Constant.UserKey.iso] = nowAsISO
        createdAt[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.createdAt] = createdAt

        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_BASKET).document(uuid)
            .set(obj)
            .addOnSuccessListener {
                loadBasket()
            }
            .addOnFailureListener { e ->
                setToast(e.message.toString())
                hideLoadingDialog()
            }
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


        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1212){
            val load = data?.getBooleanExtra("load",false)
            if(data?.hasExtra("isBack") == true){
                val isBack= data?.getBooleanExtra("isBack",false)
                val intent = Intent()
                intent.putExtra("isBack",isBack)
                setResult(RESULT_OK,intent)
                finish()
            }
            if(load == true){
                loadBasket()
            }

        }
    }


}
