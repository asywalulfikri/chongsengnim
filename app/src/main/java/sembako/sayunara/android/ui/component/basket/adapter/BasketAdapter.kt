package sembako.sayunara.android.ui.component.basket.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BasketAdapter : RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

    private var resultList: List<ListBasket> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_basket, parent, false)
        return ViewHolder(view, mOnClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val basket: ListBasket = resultList[position]

        holder.tvName.text = basket.basketName
        holder.tvDate.text = formatDateToIndonesia(basket.updatedAt
            .timestamp?.let { getDate(it) }, "dd/MM/yyyy HH:mm:ss", "dd-M-yyyy-hh-mm-ss")





        holder.llDetail.setOnClickListener {
            mOnClickListener.onClickDetail(position,basket)
        }

        holder.btnDelete.setOnClickListener {
            mOnClickListener.onDeleteBasket(position,basket)

        }
    }

    fun formatDateToIndonesia(dateTime: String?, format: String?, originFormat: String?): String? {
        val fmt = SimpleDateFormat(originFormat)
        var date: Date? = null
        try {
            date = fmt.parse(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val fmtOut = SimpleDateFormat(format, Locale("ID"))
        return fmtOut.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun createDate(timestamp: Long): CharSequence? {
        val c = Calendar.getInstance()
        c.timeInMillis = timestamp
        val d = c.time
        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss")
        return sdf.format(d)
    }

    private fun getDate(time: Long): String? {
        val date = Date(time * 1000L) // *1000 is to convert seconds to milliseconds
        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss") // the format of your date
        sdf.timeZone = TimeZone.getTimeZone("GMT+7")
        return sdf.format(date)
    }

    fun setItems(resultList: List<ListBasket>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvName: TextView = view.findViewById(R.id.tvBasketName)
        var tvDate: TextView = view.findViewById(R.id.tvBasketDate)
        var llDetail : CardView = view.findViewById(R.id.ll_detail)
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
        fun onClickDetail(position: Int,basket: ListBasket)
        fun onDeleteBasket(position: Int,basket: ListBasket)
    }

}