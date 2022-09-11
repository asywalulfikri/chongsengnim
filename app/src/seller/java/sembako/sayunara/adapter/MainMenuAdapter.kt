package sembako.sayunara.adapter

/**
 * Created by Asywalul Fikri on 7 April 2021.
 */
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import sembako.sayunara.home.Home
import java.util.*

internal class MainMenuAdapter(private val mContext: Context, var mDataset: ArrayList<Home>, private val mlayout: Int) : RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var tvTitle: TextView = v.findViewById(R.id.tvTitle)
        var ivIcon: ImageView = v.findViewById(R.id.ivIcon)
        var mItemLayout: View = v.findViewById(R.id.item_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mlayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mDataset[position].icon?.let { holder.ivIcon.setImageResource(it) }
        holder.tvTitle.text = mDataset[position].title
    }


    override fun getItemCount(): Int {
        return mDataset.size
    }
}