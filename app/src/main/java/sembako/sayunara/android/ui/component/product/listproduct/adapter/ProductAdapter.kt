package sembako.sayunara.android.ui.component.product.listproduct.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.desmond.squarecamera.SquareImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.android.ui.util.HFRecyclerViewAdapter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

open class ProductAdapter(var context: Context?, isHome: Boolean, isLike: Boolean, isBuy : Boolean) : HFRecyclerViewAdapter<Product?, ProductAdapter.ItemViewHolder?>(context) {

    private lateinit var mOnClickListener: OnClickListener
    var total = 0
    private var isHome = false
    private var isLike = false
    private var isBuy  = false
    var price = 0

    override fun footerOnVisibleItem() {

    }
    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View = if (isHome) {
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_home, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product, parent, false)
        }
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvProductName: TextView      = view.findViewById(R.id.tvProductName)
        var tvProductPrice: TextView     = view.findViewById(R.id.tvProductPrice)
        var tvProductDiscount: TextView  = view.findViewById(R.id.tvProductDiscount)
        var tvProductUnit: TextView      = view.findViewById(R.id.tvProductUnit)
        var ivProductImage: SquareImageView       = view.findViewById(R.id.ivProduct)
        var cvProduct: CardView                 = view.findViewById(R.id.cvProduct)
        var btnBuy: Button               = view.findViewById(R.id.btnBuy)
        var btnDiscount: Button          = view.findViewById(R.id.btnDiscount)
        var progressBar: ProgressBar     = view.findViewById(R.id.progress_bar)
        var rlLike: RelativeLayout       = view.findViewById(R.id.ll_like)


    }

    interface OnClickListener {
        fun onClickDetail(position: Int,product:Product)
    }


    init {
        this.isHome = isHome
        this.isLike = isLike
        this.isBuy  = isBuy
    }

    @SuppressLint("SetTextI18n")
    override fun onBindDataItemViewHolder(holder: ItemViewHolder?, position: Int) {
        val product = data[position]
        holder?.tvProductName?.text = product?.name
        if (!product?.images?.get(0)?.isEmpty()!!) {
            Picasso.get()
                .load(product.images.get(0))
                .into(holder?.ivProductImage, object : Callback {
                    override fun onSuccess() {
                        holder?.progressBar?.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        holder?.progressBar?.visibility = View.VISIBLE
                    }
                })
        }
        val harga = product.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)
        if (product.discount == 0L) {
            holder?.btnDiscount?.visibility = View.GONE
            holder?.tvProductPrice?.text = "Rp$k /"
            holder?.tvProductUnit?.text = product.weight.toString() + " " + product.unit
        } else {
            holder?.btnDiscount?.visibility = View.VISIBLE
            holder?.tvProductDiscount?.visibility = View.VISIBLE
            holder?.btnDiscount?.text = product.discount.toString() + " %"
            val price = product.price.toString().toInt()
            val discount = product.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            holder?.tvProductDiscount?.paintFlags =
                holder?.tvProductDiscount?.paintFlags!! or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvProductDiscount.text =
                "Rp" + k + " / " + product.weight + " " + product.unit
            holder.tvProductUnit.text = product.weight.toString() + " " + product.unit
            holder.tvProductPrice.text = "Rp" + df.format(amount) + " /"
        }
        holder?.btnBuy?.setOnClickListener { v ->
            mOnClickListener.onClickDetail(position,product)
        }

        if(isBuy){
            holder?.btnBuy?.visibility = View.VISIBLE
        }else{
            holder?.btnBuy?.visibility = View.GONE
        }
        if (isLike) {
            holder?.rlLike?.visibility = View.VISIBLE
        } else {
            holder?.rlLike?.visibility = View.GONE
        }

        holder?.cvProduct?.setOnClickListener { v ->
            mOnClickListener.onClickDetail(position,product)
        }
    }

}