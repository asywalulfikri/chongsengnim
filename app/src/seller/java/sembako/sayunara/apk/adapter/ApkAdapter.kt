package sembako.sayunara.apk.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.desmond.squarecamera.SquareImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.android.ui.util.TimeUtils
import java.util.*


class ApkAdapter : RecyclerView.Adapter<ApkAdapter.ViewHolder>() {

    private var resultList: List<ConfigSetup> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_apk, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val configSetup: ConfigSetup= resultList[position]

        holder.tvAppName.text = configSetup.appName
        holder.tvLastUpdate.text = callStringColor("Last Update ",TimeUtils().formatDateToIndonesia(configSetup.updatedAt?.iso.toString()))
        holder.tvVersionCode.text = callStringColor("Version Code " , configSetup.config?.versionCode.toString())
        holder.tvVersionName.text = callStringColor("Version Name " , configSetup.config?.versionName.toString())
        holder.tvAppId.text = callStringColor("AppId ",configSetup.appId.toString())

        if (configSetup.icon?.isNotEmpty() == true) {
            Picasso.get()
                .load(configSetup.icon)
                .into(holder.ivIcon, object : Callback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception) {
                    }
                })
        }

        holder.rlDetail.setOnClickListener {
            mOnClickListener.onClickDetail(position,configSetup)
        }
    }

    private fun callStringColor(param : String, value : String):Spanned{
        return (Html.fromHtml(param + "<b><i><font color = '" + "#43A047" + "'>" + value + " "+"</font></i></b>"))
    }


    fun setItems(resultList: List<ConfigSetup>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvAppName: TextView = view.findViewById(R.id.tvAppName)
        var tvLastUpdate: TextView = view.findViewById(R.id.tvLastUpdate)
        var tvVersionCode: TextView = view.findViewById(R.id.tvVersionCode)
        var tvVersionName : TextView = view.findViewById(R.id.tvVersionName)
        var tvAppId : TextView = view.findViewById(R.id.tvAppId)
        var rlDetail : RelativeLayout = view.findViewById(R.id.rlDetail)
        var ivIcon : SquareImageView = view.findViewById(R.id.ivIcon)

    }

    interface OnClickListener {
        fun onClickDetail(position: Int,configSetup: ConfigSetup)
    }

}