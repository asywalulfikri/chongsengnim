package sembako.sayunara.android.ui.component.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*


class ConfirmationProductAdapter : RecyclerView.Adapter<ConfirmationProductAdapter.ViewHolder>() {

    private var resultList: List<Product?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order_confirmation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: Product? = resultList[position]

        holder.tvName.text = product?.detail?.name.toString()
        holder.tvPrice.text = product?.detail?.price.toString()

    }

    fun setItems(resultList: List<Product?>) {
        this.resultList = resultList
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvPrice: TextView = view.findViewById(R.id.tvProductPrice)

        override fun onClick(view: View) {

        }

        init {
            itemView.setOnClickListener(this)

        }
    }

    interface OnClickListener {

    }

}