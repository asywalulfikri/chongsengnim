package sembako.sayunara.android.ui.component.articles

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.util.TimeUtils
import java.util.*


class ArticlesAdapter : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    private var resultList: List<Articles> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener
    private var isAdmin : Boolean ? =null
    private var context : Context? =null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val articles = resultList[position]


        if(getSeller()){
            if(articles.status?.draft==true){
                holder.ivAction.visibility =View.VISIBLE
            }else{
                holder.ivAction.visibility = View.GONE
            }
        }

        if(isAdmin==true){
            holder.llCostumer.visibility = View.GONE
            holder.viewAdmin.visibility = View.VISIBLE

            if(articles.images.size!=0){
                if(articles.images[0].isNotEmpty()){
                    setImage(articles.images[0],holder.ivArticlesAdmin)
                }
            }

            holder.tvTitleAdmin.text = articles.title
            holder.tvContentAdmin.text = articles.content
            holder.tvTimeAdmin.text = TimeUtils().formatDateToIndonesia(articles.createdAt?.iso)

            var messageStatus : String

            if(articles.status?.active==true){

                if(articles.status?.publish==true){
                    messageStatus = "Status Publish "

                    if(articles.status?.moderation==true){
                        messageStatus = "On Process Review"

                        if(articles.status?.draft==true){
                            messageStatus = "On Draft"
                        }
                    }else{
                        if(articles.status?.draft==true){
                            messageStatus = "On Draft"
                        }
                    }

                }else{
                    messageStatus = "Status not publish "
                }

            }else{
                messageStatus = "Non Active"
            }


            holder.tvStatus.text = messageStatus

        }else{
            holder.llCostumer.visibility = View.VISIBLE
            holder.viewAdmin.visibility = View.GONE

        }

        when (articles.viewType?.toInt()) {
            1 -> {
                holder.tvTitle1.text = articles.title
                holder.tvContent1.text = articles.content
                holder.tvTime1.text = TimeUtils().formatDateToIndonesia(articles.createdAt?.iso)

                holder.view1.visibility = View.VISIBLE
                holder.view2.visibility = View.GONE
                holder.view3.visibility = View.GONE

                if(articles.images.size!=0){
                    if(articles.images[0].isNotEmpty()){
                        setImage(articles.images[0],holder.ivArticles1)
                    }
                }

            }
            2 -> {
                holder.tvTitle2.text = articles.title
                holder.tvContent2.text = articles.content
                holder.tvTime2.text = TimeUtils().formatDateToIndonesia(articles.createdAt?.iso)

                holder.view1.visibility = View.GONE
                holder.view2.visibility = View.VISIBLE
                holder.view3.visibility = View.GONE

                if(articles.images.size!=0){
                    if(articles.images.size==1){
                        if(articles.images[0].isNotEmpty()){
                            setImage(articles.images[0],holder.ivArticles21)
                        }
                    }else if (articles.images.size==2){
                        if(articles.images[1].isNotEmpty()){
                            setImage(articles.images[0],holder.ivArticles21)
                            setImage(articles.images[1],holder.ivArticles22)
                        }else{
                            holder.ivArticles22.visibility = View.GONE
                        }
                    }else if(articles.images.size==3){
                        if(articles.images[2].isNotEmpty()){

                            setImage(articles.images[0],holder.ivArticles21)
                            setImage(articles.images[1],holder.ivArticles22)
                            setImage(articles.images[2],holder.ivArticles23)
                        }else{
                            holder.ivArticles23.visibility = View.GONE
                        }
                    }
                }

            }
            else -> {
                holder.tvTitle3.text = articles.title
                holder.tvContent3.text = articles.content
                holder.tvTime3.text = TimeUtils().formatDateToIndonesia(articles.createdAt?.iso)

                holder.view1.visibility = View.GONE
                holder.view2.visibility = View.GONE
                holder.view3.visibility = View.VISIBLE

                if(articles.images.size!=0){
                    if(articles.images[0].isNotEmpty()){
                        setImage(articles.images[0],holder.ivArticles3)
                    }
                }

            }
        }






        holder.viewAdmin.setOnClickListener {
            mOnClickListener.onClickDetail(position,articles)
        }
        holder.view1.setOnClickListener {
            mOnClickListener.onClickDetail(position,articles)
        }
        holder.view2.setOnClickListener {
            mOnClickListener.onClickDetail(position,articles)
        }
        holder.view3.setOnClickListener {
            mOnClickListener.onClickDetail(position,articles)
        }

        holder.ivAction.setOnClickListener {
            mOnClickListener.onActionClick(position,articles)
        }

    }


    private fun setImage(url : String, imageView : ImageView){
        Picasso.get()
            .load(url)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                  //  holder?.progressBar?.visibility = View.GONE
                }

                override fun onError(e: Exception) {
                   // holder?.progressBar?.visibility = View.VISIBLE
                }
            })

    }

    private fun getSeller() : Boolean{
        return (context as BaseActivity?)!!.getSeller()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setItems(context: Context,resultList: List<Articles>, isAdmin : Boolean, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        this.isAdmin = isAdmin
        this.context = context
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvTitle1: TextView = view.findViewById(R.id.tvTitle1)
        var tvContent1: TextView = view.findViewById(R.id.tvContent1)
        var tvTime1: TextView = view.findViewById(R.id.tvTime1)
        var view1 : CardView = view.findViewById(R.id.itemArticle1)
        var ivArticles1 : ImageView = view.findViewById(R.id.ivArticles1)

        var tvTitle2: TextView = view.findViewById(R.id.tvTitle2)
        var tvContent2: TextView = view.findViewById(R.id.tvContent2)
        var tvTime2: TextView = view.findViewById(R.id.tvTime2)
        var view2 : CardView = view.findViewById(R.id.itemArticle2)
        var ivArticles21 : ImageView = view.findViewById(R.id.ivArticles21)
        var ivArticles22 : ImageView = view.findViewById(R.id.ivArticles22)
        var ivArticles23 : ImageView = view.findViewById(R.id.ivArticles23)


        var tvTitle3: TextView = view.findViewById(R.id.tvTitle3)
        var tvContent3: TextView = view.findViewById(R.id.tvContent3)
        var tvTime3: TextView = view.findViewById(R.id.tvTime3)
        var view3 : ConstraintLayout = view.findViewById(R.id.itemArticle3)
        var ivArticles3 : ImageView = view.findViewById(R.id.ivArticles3)


        var tvTitleAdmin: TextView = view.findViewById(R.id.tvTitleAdmin)
        var tvContentAdmin: TextView = view.findViewById(R.id.tvContentAdmin)
        var tvTimeAdmin: TextView = view.findViewById(R.id.tvTimeAdmin)
        var viewAdmin : ConstraintLayout = view.findViewById(R.id.itemArticleAdmin)
        var ivArticlesAdmin : ImageView = view.findViewById(R.id.ivArticlesAdmin)

        var llCostumer : LinearLayout = view.findViewById(R.id.llCostumer)
        var tvStatus: TextView = view.findViewById(R.id.tvStatus)
        var ivAction : ImageView = view.findViewById(R.id.ivAction)

    }

    interface OnClickListener {
        fun onClickDetail(position: Int,articles: Articles)
        fun onActionClick(position: Int,articles: Articles)
    }

}