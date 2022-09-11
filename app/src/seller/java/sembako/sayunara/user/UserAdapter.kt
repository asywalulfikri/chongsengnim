package sembako.sayunara.user

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.util.TimeUtils
import java.util.*


class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var resultList: List<User> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = resultList[position]

        holder.tvUsername.text = user.profile.username
        holder.tvJoinDate.text = "Bergabung " + TimeUtils().formatDateToIndonesia(user.createdAt.iso)
        holder.tvNumber.text = (position + 1).toString()

        if(user.profile.avatar!=""){
            Picasso.get()
                .load(user.profile.avatar)
                .into(holder.ivAvatar)
        }

        if(user.profile.verified==true){
            holder.ivVerified.visibility = View.VISIBLE
        }else{
            holder.ivVerified.visibility = View.GONE
        }

        holder.rlDetail.setOnClickListener {
            mOnClickListener.onClickDetail(position,user)
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    fun setItems(resultList: List<User>, onClickListener: OnClickListener) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var tvUsername: TextView = view.findViewById(R.id.tvUsername)
        var tvJoinDate: TextView = view.findViewById(R.id.tvJoinDate)
        var ivAvatar : CircleImageView = view.findViewById(R.id.ivAvatar)
        var tvNumber: TextView = view.findViewById(R.id.tvNumber)
        var ivVerified : ImageView = view.findViewById(R.id.ivVerified)
        var rlDetail : RelativeLayout = view.findViewById(R.id.rlDetail)

    }

    interface OnClickListener {
        fun onClickDetail(position: Int,user: User)
    }

}