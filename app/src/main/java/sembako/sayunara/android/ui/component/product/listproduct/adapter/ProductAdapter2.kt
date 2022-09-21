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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class ProductAdapter2 : RecyclerView.Adapter<ProductAdapter2.ViewHolder>() {

    private var resultList: List<Product> = ArrayList()
    private var userList: List<User?> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener
    private var context : Context? =null
    private var like : Boolean? =null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_product, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = resultList[position]
        val user = userList[position]

        holder.tvProductName.text = setName(product.detail?.name.toString())
        holder.tvUsername.text = user?.profile?.username

        if(product.detail?.images?.size!=0){
            val urlImage = product.detail?.images?.get(0)
            setImage(urlImage.toString(),holder.ivProductImage)
        }

        val harga = product.detail?.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)
        if (product.detail?.discount == 0L) {
            holder.btnDiscount.visibility = View.GONE
            holder.tvProductPrice.text = "Rp$k /"
            holder.tvProductUnit.text = product.detail?.weight.toString() + " " + product.detail?.unit
        } else {
            holder.btnDiscount.visibility = View.VISIBLE
            holder.tvProductDiscount.visibility = View.VISIBLE
            holder.btnDiscount.text = product.detail?.discount.toString() + " %"
            val price = product.detail?.price.toString().toInt()
            val discount = product.detail?.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            holder.tvProductDiscount.paintFlags =
                holder.tvProductDiscount.paintFlags!! or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvProductDiscount.text =
                "Rp" + k + " / " + product.detail?.weight + " " + product.detail?.unit
            holder.tvProductUnit.text = product.detail?.weight.toString() + " " + product.detail?.unit
            holder.tvProductPrice.text = "Rp" + df.format(amount) + " /"
        }
        holder.btnBuy.setOnClickListener {
            mOnClickListener.onClickDetail(position,product)
        }

        holder.cvProduct.setOnClickListener {
            mOnClickListener.onClickDetail(position,product)
        }

        if(isSeller()||isAdmin()||isSuperAdmin()){
            if(product.status?.draft==true){
                holder.ivDraftImage.visibility = View.VISIBLE
            }
        }

        if(isCustomer()){
            if(getUser()?.profile?.userId == product.userId){
                holder.btnBuy.visibility = View.GONE
            }else{
                holder.btnBuy.visibility = View.VISIBLE
            }
            holder.ivAction.visibility = View.GONE
        }else{
            holder.btnBuy.visibility = View.GONE
            holder.ivAction.visibility = View.VISIBLE
        }

        holder.ivAction.setOnClickListener {
            mOnClickListener.onActionClick(position,product)
        }

        if(like==true){
            holder.rlLike.visibility=View.VISIBLE
        }else{
            holder.rlLike.visibility=View.GONE
        }

        if(isSuperAdmin()||isAdmin()||isSeller()){
            holder.layoutStatus.visibility = View.VISIBLE
            if(product.status?.active==false){
                //Delete By Owner
                holder.tvStatus.text = "Status Deleted"
            }else{
                if(product.status?.publish==true){
                    holder.tvStatus.text = "Status Publish"
                }else{
                    holder.tvStatus.text = "Status UnPublish"
                }

                if(product.status?.draft==true){
                    holder.tvStatus.text = "Status Draft"
                }
            }
        }

        if(user?.profile?.avatar!=""){
            Picasso.get()
                .load(user?.profile?.avatar.toString())
                .into(holder.ivUser)
        }

    }

    private fun setImage(url : String, imageView : ImageView){
        Picasso.get()
            .load(url)
            .into(imageView)

    }

    private fun isSeller() : Boolean{
        return (context as BaseActivity).getSeller()
    }

    private fun isCustomer() : Boolean{
        return (context as BaseActivity).isCustomer()
    }

    private fun isAdmin() : Boolean{
        return (context as BaseActivity).getAdmin()
    }

    private fun isSuperAdmin() : Boolean{
        return (context as BaseActivity).getSuperAdmin()
    }

    private fun getUser() : User? {
        return (context as BaseActivity).getUsers
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(context: Context,resultList: List<Product>,userList: List<User?> ,onClickListener: OnClickListener, like : Boolean) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        this.context = context
        this.like = like
        this.userList = userList
        notifyDataSetChanged()
    }


    private fun setName(str: String): String? {
        val strArray = str.split(" ").toTypedArray()
        val builder = StringBuilder()
        for (s in strArray) {
            val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
            val stm: String = cap
            builder.append("$stm ")
        }
        return builder.toString()
    }


    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvProductName: TextView      = view.findViewById(R.id.tvProductName)
        var tvProductPrice: TextView     = view.findViewById(R.id.tvProductPrice)
        var tvUsername: TextView         = view.findViewById(R.id.tvUsername)
        var tvProductDiscount: TextView  = view.findViewById(R.id.tvProductDiscount)
        var tvProductUnit: TextView      = view.findViewById(R.id.tvProductUnit)
        var ivProductImage: ImageView    = view.findViewById(R.id.ivProduct)
        var ivDraftImage: ImageView      = view.findViewById(R.id.ivDraftImage)
        var cvProduct: CardView          = view.findViewById(R.id.cvProduct)
        var btnBuy: Button               = view.findViewById(R.id.btnBuy)
        var btnDiscount: Button          = view.findViewById(R.id.btnDiscount)
        var rlLike: RelativeLayout       = view.findViewById(R.id.rlLike)
        var ivAction: ImageView          = view.findViewById(R.id.ivAction)
        var layoutStatus : RelativeLayout = view.findViewById(R.id.layoutStatus)
        var tvStatus : TextView          = view.findViewById(R.id.tvStatus)
        var ivUser : CircleImageView     = view.findViewById(R.id.ivUser)

    }

    interface OnClickListener {
        fun onClickDetail(position: Int,product: Product)
        fun onActionClick(position: Int,product: Product)
    }

}