package sembako.sayunara.android.ui.component.product.favorite

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity
import sembako.sayunara.android.ui.component.product.favorite.model.Favorite
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*


class ListFavoriteActivty : BaseActivity(),FavoriteView, ProductAdapter.OnClickListener {

    var productAdapter : ProductAdapter? =null
    var services = FavoriteServices()
    var arrayList : ArrayList<Favorite> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        recyclerView.run {
            layoutManager = mLayoutManager
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            productAdapter= ProductAdapter(this@ListFavoriteActivty,false,true,true)
            adapter = productAdapter
        }


        if(isLogin()){

            services.getFavorite(this, FirebaseFirestore.getInstance(), getUsers?.profile?.userId.toString())

            swipeRefresh.setOnRefreshListener {
                services.getFavorite(this, FirebaseFirestore.getInstance(), getUsers?.profile?.userId.toString())
            }
        }else{
            layout_empty.visibility =View.VISIBLE
            textViewEmptyList.text = "Masuk Terlebih dahulu"
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

    override fun onRequestSuccess(arrayList: ArrayList<Favorite>) {
        this.arrayList = arrayList

        if(arrayList.size==0){
            layout_empty.visibility = View.VISIBLE
            textViewEmptyList.text = getString(R.string.empty_favorite)
            layout_progress.visibility = View.GONE
        }else{
            getDetailPerList(arrayList)
        }
    }


    private fun showHideButton(){
        if(this.arrayList.size >0){
            layout_empty.visibility = View.GONE

        }else{
            layout_empty.visibility = View.VISIBLE
            textViewEmptyList.text = getString(R.string.empty_favorite)
        }
        layout_progress.visibility = View.GONE
    }


    private fun getDetailPerList(listFavArray: ArrayList<Favorite>){

        swipeRefresh.isRefreshing = false

        val productArrayList: ArrayList<Product?> = ArrayList()
        for ( i in 0 until listFavArray.size) {
            val produk1 = listFavArray[i]
            var isi = ""
            FirebaseFirestore.getInstance().collection(Constant.Collection.COLLECTION_PRODUCT).
            document(produk1.productId.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if(task.result!!.exists()){
                        val product = task.result!!.toObject(Product::class.java)
                        Log.d("eksekusi", product?.name + "xx")
                        product?.let { productArrayList.add(it) }

                    }


                    Handler().postDelayed({
                        //doSomethingHere()
                        this.arrayList = listFavArray

                        if(this.arrayList.isEmpty()){
                            layout_empty.visibility =View.VISIBLE
                            textViewEmptyList.text = "Keranjang Masih Kosong"
                        }

                        updateList(productArrayList)
                        // recyclerView.adapter = detailBasketAdapter
                        showHideButton()

                    }, 1000)


                } else {
                    setToast(task.exception?.message.toString())

                }
            }


        }


    }



    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(historyList: ArrayList<Product?>) {

        productAdapter = ProductAdapter(this, false,true,true)
        productAdapter?.data = historyList
        recyclerView.adapter = productAdapter
        productAdapter?.notifyDataSetChanged()
       /* if (productAdapter!!.dataItemCount > 0) {
            ll_no_product.setVisibility(View.GONE)
        } else {
            ll_no_product.setVisibility(View.VISIBLE)
        }*/

        //automaticLoadMore()
    }


    override fun onRequestFailed(code: Int?) {
        //TODO("Not yet implemented")
    }

    override fun setupViews() {
       // TODO("Not yet implemented")
    }

    override fun onClickDetail(position: Int, product: Product) {
        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, Constant.Code.CODE_LOAD)
    }

}
