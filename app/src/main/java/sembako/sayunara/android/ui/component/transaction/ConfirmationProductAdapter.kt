package sembako.sayunara.android.ui.component.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.text.DecimalFormat
import java.util.*


class ConfirmationProductAdapter : RecyclerView.Adapter<ConfirmationProductAdapter.ViewHolder>() {

    private var resultList: List<Product?> = ArrayList()
    private var basketArrayList =  ArrayList<Basket>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order_confirmation, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: Product? = resultList[position]
        val basket : Basket = basketArrayList[position]

        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val harga: Double = product?.detail?.price.toString().toDouble()
        val k = df.format(harga)
        holder.tvName.text = product?.detail?.name.toString() +" "+ product?.detail?.weight +"/"+ product?.detail?.unit
        holder.tvPrice.text = k
        val format = DecimalFormat("#.#")
        holder.tvQuantity.text = "x "+ basket.quantity?.toInt().toString() + " "


    }

    fun setItems(resultList: List<Product?>,basketArrayList : ArrayList<Basket>) {
        this.resultList = resultList
        this.basketArrayList = basketArrayList
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        var tvQuantity : TextView = view.findViewById(R.id.tvQuantity)

        override fun onClick(view: View) {

        }

        init {
            itemView.setOnClickListener(this)

        }
    }

    interface OnClickListener {

    }

}