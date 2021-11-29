package sembako.sayunara.android.ui.component.basket.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.desmond.squarecamera.SquareImageView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class DetailBasketAdapter : RecyclerView.Adapter<DetailBasketAdapter.ViewHolder>() {

    private var resultList: List<Basket> = ArrayList()
    private var producttList: List<Product?> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)
        return ViewHolder(view, mOnClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val basket: Basket = resultList[position]
        val product : Product? = producttList[position]
        holder.tvName.text = product?.detail?.name

        holder.tvCount.text = basket.quantity?.toInt().toString()

        /*if (product.productDetail?.images[0].isNotEmpty()) {
            Picasso.get()
                    .load(product.images[0])
                    .into(holder.ivProduct, object : Callback {
                        override fun onSuccess() {
                           // holder.progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {
                           // holder.progressBar.visibility = View.VISIBLE
                        }
                    })
        }
*/

        val harga: Double = product?.detail?.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)


        if (product?.detail?.discount == 0L) {
            holder.button_discount.visibility = View.GONE
            holder.price.text = "Rp$k /"
            holder.tv_product_unit.text = product.detail?.weight.toString() + " " + product.detail?.unit
        } else {
            holder.button_discount.visibility = View.VISIBLE
            holder.textview_price_discount.visibility = View.VISIBLE
            holder.button_discount.text = product?.detail?.discount.toString() + " %"
            val price: Int = product?.detail?.price.toString().toInt()
            val discount: Int = product?.detail?.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            holder.textview_price_discount.paintFlags = holder.textview_price_discount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textview_price_discount.text = "Rp" + k + " / " + product?.detail?.weight + " " + product?.detail?.unit
            holder.tv_product_unit.text = product?.detail?.weight.toString() + " " + product?.detail?.unit
            holder.price.text = "Rp" + df.format(amount) + " /"
        }


        holder.btnPlus.setOnClickListener {
            mOnClickListener.onClickBasketPlus(position,product,basket)
        }

        holder.btnMin.setOnClickListener {
            mOnClickListener.onClickBasketMinus(position,product,basket)
        }

        holder.btnDelete.setOnClickListener {
            mOnClickListener.onClickDelete(position,basket)
        }



    }

    fun setItems(resultList: List<Basket>, onClickListener: OnClickListener, productArrayList: ArrayList<Product?>) {
        this.resultList = resultList
        this.producttList = productArrayList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvCount: TextView = view.findViewById(R.id.tvCount)
        var price : TextView = view.findViewById(R.id.tvProductPrice)
        var ivProduct : SquareImageView = view.findViewById(R.id.ivProduct)
        //var progressBar : ProgressBar = view.findViewById(R.id.progress_bar)
        var button_discount : Button = view.findViewById(R.id.btnDiscount)
        var tv_product_unit : TextView = view.findViewById(R.id.tvProductUnit)
        var textview_price_discount :TextView = view.findViewById(R.id.tvProductDiscount)
        var btnPlus : Button = view.findViewById(R.id.btnPlus)
        var btnMin : Button = view.findViewById(R.id.btnMin)
        var btnDelete : ImageView = view.findViewById(R.id.btnDelete)


        private var mOnClickListener: OnClickListener = onClickListener

        override fun onClick(view: View) {
            mOnClickListener.onClickBasket(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)

        }
    }

    interface OnClickListener {
        fun onClickBasket(position: Int)
        fun onClickBasketPlus(position: Int, product: Product?,basket: Basket)
        fun onClickBasketMinus(position: Int, product: Product?,basket: Basket)
        fun onClickDelete(position: Int,basket: Basket)
    }

}