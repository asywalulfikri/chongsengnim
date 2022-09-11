package sembako.sayunara.android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.product.editProduct.model.ImageData

class ImagesAdapter() : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {


    private lateinit var mOnClickListener: OnClickListener
    private var imagesFiles: List<ImageData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.view_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imagesFiles = imagesFiles[position]
        Log.d("yamete",imagesFiles.path+"--")
        Glide.with(holder.imageView)
            .asBitmap()
            .load(imagesFiles.path)
            .transition(BitmapTransitionOptions.withCrossFade())
           // .apply(GrayscaleImageLoader.REQUEST_OPTIONS)
            .into(holder.imageView)

        holder.ivDelete.setOnClickListener {
            mOnClickListener.onClickDelete(position)
        }
    }

    fun setItems(context: Context, images: ArrayList<ImageData>, onClickListener: OnClickListener) {
        this.imagesFiles = images
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return imagesFiles.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var ivDelete: ImageView

        init {
            imageView = itemView.findViewById(R.id.image_view)
            ivDelete = itemView.findViewById(R.id.iv_delete)
        }
    }

    interface OnClickListener {
        fun onClickDelete(position: Int)
    }
}