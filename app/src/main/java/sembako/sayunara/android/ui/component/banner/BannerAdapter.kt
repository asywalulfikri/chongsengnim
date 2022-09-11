package sembako.sayunara.android.ui.component.banner

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.utils.StorageUtils
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import java.util.*


class BannerAdapter : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    private var resultList: List<Banner> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener
    private var context : Context ? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Banner = resultList[position]

        holder.tvTitle.text = data.detail?.title

        if(data.detail?.image!=""){
           /* Picasso.get()
                .load(data.detail?.image)
                .into(holder.ivBanner)*/
            Glide
                .with(context as Context)
                .load(data.detail?.image)
                .centerCrop()
                .into(holder.ivBanner);

        }

        holder.cvDetail.setOnClickListener {
            mOnClickListener.onClickDetail(position,data)
        }

        if(data.status?.draft==true){
            holder.ivDraftImage.visibility = View.VISIBLE
        }else{
            holder.ivDraftImage.visibility = View.GONE
        }

        if(getAdmin()||getSuperAdmin()){
            holder.btnStatus.visibility = View.VISIBLE

            if(data.status?.active == true){
                if(data.status?.draft==true){
                    holder.btnStatus.text = "Draft"
                }else{
                    holder.btnStatus.text = "Publish"
                }
            }else{
                holder.btnStatus.text = "UnPublish"
            }
        }
    }


    fun initImageLoader(context: Context?) {

        val defaultOptions = DisplayImageOptions.Builder()
            .cacheInMemory(false)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheOnDisk(true)
            .build()

        val config = ImageLoaderConfiguration.Builder(context)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .defaultDisplayImageOptions(defaultOptions)
            .denyCacheImageMultipleSizesInMemory()
            .diskCacheFileNameGenerator(Md5FileNameGenerator())
            .diskCache(
                UnlimitedDiskCache(
                    StorageUtils.getOwnCacheDirectory(
                        context,
                        "dadadad"
                    )
                )
            )
            .diskCacheSize(100 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO)
            .memoryCache(LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
            .threadPoolSize(3)
            .build()
        ImageLoader.getInstance().init(config)
    }


    private fun getAdmin() : Boolean{
        return (context as BaseActivity).getAdmin()
    }

    private fun getSuperAdmin() : Boolean{
        return (context as BaseActivity).getSuperAdmin()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(context: Context,resultList: List<Banner>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        this.context = context
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var tvTitle: TextView = view.findViewById(R.id.tvTitle)
        var ivBanner : ImageView = view.findViewById(R.id.ivBanner)
        var ivDraftImage : ImageView = view.findViewById(R.id.ivDraftImage)
        var cvDetail : CardView = view.findViewById(R.id.cvDetail)
        var btnStatus : Button = view.findViewById(R.id.btnStatus)

    }

    interface OnClickListener {
        fun onClickDetail(position: Int,banner: Banner)
    }

}