package sembako.sayunara.android.ui.component.product.listproduct.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.desmond.squarecamera.SquareImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.android.ui.util.HFRecyclerViewAdapter
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

open class ProductAdapter(var context: Context?, isHome: Boolean, isLike: Boolean, isBuy : Boolean,mOnClickListener: OnClickListener) : HFRecyclerViewAdapter<Product?, ProductAdapter.ItemViewHolder?>(context) {

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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_product, parent, false)
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
        var ivProductImage: ImageView    = view.findViewById(R.id.ivProduct)
        var cvProduct: CardView          = view.findViewById(R.id.cvProduct)
        var btnBuy: Button               = view.findViewById(R.id.btnBuy)
        var btnDiscount: Button          = view.findViewById(R.id.btnDiscount)
        var llinear : LinearLayout = view.findViewById(R.id.llProduct)


    }

    interface OnClickListener {
        fun onClickDetail(position: Int,product:Product)
    }


    init {
        this.isHome = isHome
        this.isLike = isLike
        this.isBuy  = isBuy
        this.mOnClickListener = mOnClickListener
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

    @SuppressLint("SetTextI18n")
    override fun onBindDataItemViewHolder(holder: ItemViewHolder?, position: Int) {
        val product = data[position]

        // val user = userList[position]

        holder?.tvProductName?.text = setName(product?.detail?.name.toString())
       // holder?.tvUsername?.text = user?.profile?.username

        if(product?.detail?.images?.size!=0){
            val urlImage = product?.detail?.images?.get(0)
            setImage(urlImage.toString(),holder?.ivProductImage)
        }

        val harga = product?.detail?.price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        val k = df.format(harga)
        if (product?.detail?.discount == 0L) {
            holder?.btnDiscount?.visibility = View.GONE
            holder?.tvProductPrice?.text = "Rp$k".dropLast(3)
            holder?.tvProductUnit?.text = product.detail?.weight.toString() + " " + product.detail?.unit
        } else {
            holder?.btnDiscount?.visibility = View.VISIBLE
            holder?.tvProductDiscount?.visibility = View.VISIBLE
            holder?.btnDiscount?.text = product?.detail?.discount.toString() + " %"
            val price = product?.detail?.price.toString().toInt()
            val discount = product?.detail?.discount.toString().toInt()
            val total = discount * price / 100
            val total2 = price - total
            val amount = total2.toString().toDouble()
            holder?.tvProductDiscount?.paintFlags =
                holder?.tvProductDiscount?.paintFlags!! or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvProductDiscount.text =
                "Rp" + k + " / " + product?.detail?.weight + " " + product?.detail?.unit
            holder.tvProductUnit.text = product?.detail?.weight.toString() + " " + product?.detail?.unit
            val harga = df.format(amount).dropLast(3)
            holder.tvProductPrice.text = "Rp$harga /"
        }
        holder?.btnBuy?.setOnClickListener {
          //  mOnClickListener.onClickDetail(position,product)
        }

        holder?.cvProduct?.setOnClickListener {
           // mOnClickListener.onClickDetail(position,product)
        }

        setupLayout(holder?.llinear)

       /* if(isSeller()||isAdmin()||isSuperAdmin()){
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
        }*/

       /* if(user?.profile?.avatar!=""){
            Picasso.get()
                .load(user?.profile?.avatar.toString())
                .into(holder?.ivUser)
        }*/
    }

    private fun setupLayout(linearLayout: LinearLayout?){
        val windowManager = context?.getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        val display = windowManager.defaultDisplay
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (display.width /2)-50//- adjustScreen;  // deprecated
        val height = display.height
        val params = linearLayout?.layoutParams //as LinearLayout.LayoutParams
        params?.width = width
        params?.height = height
        linearLayout?.layoutParams = params
    }

    private fun setImage(url : String, imageView : ImageView?){
        Picasso.get()
            .load(url)
            .into(imageView)

    }

}