package sembako.sayunara.product.editProduct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.util.ZoomableImageView
import sembako.sayunara.product.editProduct.model.PodImage
import java.util.ArrayList

class PodImageAdapter(private val context: Context, private val imageList: ArrayList<PodImage>, var listener: DeletedItemListener, private val type: String) :
    RecyclerView.Adapter<PodImageAdapter.ViewHolder>() {
    interface DeletedItemListener {
        fun onDeleted(model: PodImage?, position: Int, type: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pod_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = imageList[position]
        holder.clearButton.setOnClickListener { view: View? ->
            listener.onDeleted(
                model, position,
                type
            )
        }
        Picasso.get().load(model.source).into(holder.imagePod)
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imagePod: ZoomableImageView = view.findViewById(R.id.image_pod)
        var clearButton: ImageButton = view.findViewById(R.id.image_clear)

    }
}