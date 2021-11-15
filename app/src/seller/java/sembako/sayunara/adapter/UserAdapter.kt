package sembako.sayunara.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.util.HFRecyclerViewAdapter
import sembako.sayunara.android.ui.util.TimeUtils
import java.util.ArrayList

class UserAdapter(var context: Context, onClickListener: OnClickListener) : HFRecyclerViewAdapter<User?, UserAdapter.ItemViewHolder?>(context) {

    private var mOnClickListener: OnClickListener
    private var resultList: List<User> = ArrayList()


    init {
        this.mOnClickListener = onClickListener
    }

    override fun footerOnVisibleItem() {}
    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
        return ItemViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindDataItemViewHolder(holder: ItemViewHolder?, position: Int) {
        val user = data[position]
        holder?.tvUsername?.text = user?.profile?.username
        holder?.tvJoinDate?.text = "Bergabung " + TimeUtils().formatDateToIndonesia(user?.createdAt?.iso)
        holder?.tvNumber?.text = (position + 1).toString()

        if(user?.profile?.verified==true){
            holder?.ivVerified?.visibility = View.VISIBLE
        }else{
            holder?.ivVerified?.visibility = View.GONE
        }

        holder?.rlDetail?.setOnClickListener {
            //Toast.makeText(context,"gagaggaga",Toast.LENGTH_LONG).show()
            mOnClickListener.onClickDetail(position,user!!)
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvUsername: TextView = view.findViewById(R.id.tvUsername)
        var tvJoinDate: TextView = view.findViewById(R.id.tvJoinDate)
        var tvNumber: TextView = view.findViewById(R.id.tvNumber)
        var ivVerified : ImageView = view.findViewById(R.id.ivVerified)
        var rlDetail : RelativeLayout = view.findViewById(R.id.rlDetail)


    }

    fun setItems(resultList: List<User>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClickDetail(position: Int,user: User)
    }


}