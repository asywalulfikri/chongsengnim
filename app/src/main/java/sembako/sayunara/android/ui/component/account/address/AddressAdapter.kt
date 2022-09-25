package sembako.sayunara.android.ui.component.account.address

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sembako.sayunara.android.R
import java.util.*


class AddressAdapter : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private var resultList: List<Address> = ArrayList()
    private lateinit var mOnClickListener: OnClickListener
    private var isAdmin : Boolean ? =null
    private var context : Context? =null
    private var firstAddress = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_address, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = resultList[position]

        holder.tvName.text = address.contact?.name.toString()
        var phone = address.contact?.phoneNumber.toString()
        if (phone.startsWith("62")) "0" + phone.substring(2, phone.length) else phone.also {
            phone = it
        }
        holder.tvPhoneNumber.text = "(+62) "+ phone.removePrefix("0")
        holder.tvFullAddress.text = address.address?.fullAddress.toString()
        holder.tvProvince.text = address.address?.village.toString() +", "+address.address?.subDistrict.toString() + ","+address.address?.province.toString()

        if(firstAddress == address.id){
            holder.tvFirstAddress.visibility = View.VISIBLE
        }


        holder.rlAddress.setOnClickListener {
            mOnClickListener.onClick(position,address)
        }

        holder.tvEdit.setOnClickListener {
            mOnClickListener.onClickEdit(position,address)
        }

        if(position==0){
            holder.checkbox.isChecked = true
        }

        holder.checkbox.setOnClickListener {
            mOnClickListener.onClick(position,address)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(resultList: List<Address>, onClickListener: OnClickListener,firstAddress : String) {
        this.resultList = resultList
        this.mOnClickListener = onClickListener
        this.firstAddress = firstAddress
    }
    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvPhoneNumber: TextView = view.findViewById(R.id.tvPhoneNumber)
        var tvFullAddress: TextView = view.findViewById(R.id.tvFullAddress)
        var tvProvince : TextView = view.findViewById(R.id.tvProvince)
        var rlAddress : RelativeLayout = view.findViewById(R.id.rlAddress)
        var tvFirstAddress : TextView = view.findViewById(R.id.tvFirstAddress)
        var tvEdit : TextView = view.findViewById(R.id.tvEdit)
        var checkbox : CheckBox  = view.findViewById(R.id.checkbox)


    }

    interface OnClickListener {
        fun onClick(position: Int,articles: Address)
        fun onClickEdit(position: Int,articles: Address)
    }

}