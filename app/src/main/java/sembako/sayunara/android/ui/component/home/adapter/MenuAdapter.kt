package sembako.sayunara.android.ui.component.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.home.model.Menu
import java.util.*


class MenuAdapter : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var resultList: List<Menu> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view, mOnClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result: Menu = resultList[position]
        holder.tvName.text = result.name


        Log.d("imagesnua","=="+result.image)
        Picasso.get()
                .load(result.image)
                .placeholder(R.drawable.no_banner)
                .into(holder.ivMenu)

        /*if(result.images!!.isNotEmpty()){

            Picasso.get()
                    .load(result.images)
                    .placeholder(R.drawable.no_banner)
                    .into(holder.ivMenu, object : Callback {
                        override fun onSuccess() {
                            // Image is loaded successfully...
                        }

                        override fun onError(e: Exception?) {
                            // TODO("Not yet implemented")
                        }

                    })
        }*/

    }

    fun setItems(resultList: List<Menu>, onClickListener : OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var ivMenu :ImageView = view.findViewById(R.id.ivCategory)

        private var mOnClickListener: OnClickListener = onClickListener

        override fun onClick(view: View) {
            mOnClickListener.onClickMenu(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnClickListener {
        fun onClickMenu(position: Int)
    }

}