package sembako.sayunara.android.ui.component.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import java.util.*
import kotlin.collections.ArrayList


class ArticlesAdapter : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    private var resultList: ArrayList<Articles> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view, mOnClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result: Articles = resultList[position]

        if(result.type==1){
            holder.tvTitle1.text = result.title
            holder.tvContent1.text = result.description
        }


/*
        holder.btnDelete.setOnClickListener {
            mOnClickListener.onClickDelete(position,basket)
        }
*/



    }

    fun setItems(resultList: java.util.ArrayList<Articles>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    fun getData(): ArrayList<Articles>? {
        return this.resultList
    }

    inner class ViewHolder(view: View, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var tvTitle1: TextView = view.findViewById(R.id.tv_title1)
        var tvTitle2: TextView = view.findViewById(R.id.tv_title2)
        var tvContent1: TextView = view.findViewById(R.id.tv_content1)
        var tvContent2: TextView = view.findViewById(R.id.tv_content2)



        private var mOnClickListener: OnClickListener = onClickListener

        override fun onClick(view: View) {
            mOnClickListener.onClickArticles(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)

        }
    }

    interface OnClickListener {
        fun onClickArticles(position: Int)
    }

}