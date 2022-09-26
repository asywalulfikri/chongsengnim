package sembako.sayunara.android.ui.component.account.address

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list_address.*
import kotlinx.android.synthetic.main.activity_list_address.recyclerView
import kotlinx.android.synthetic.main.activity_list_address.swipeRefresh
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import sembako.sayunara.android.ui.base.BaseActivity
import java.util.ArrayList
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.layout_empty.*
import sembako.sayunara.android.R


class ListAddressActivity : BaseActivity(),AddressView.ViewList,AddressView.DetailList,AddressAdapter.OnClickListener{

    private var mAdapter =  AddressAdapter()
    val services = AddressServices()
    val arrayList: ArrayList<Address> = ArrayList()
    var idFirstAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_address)

        setupToolbar(toolbar)


        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        swipeRefresh.setOnRefreshListener {
           refresh()
        }


        btnAddAddress.setOnClickListener {
            val intent = Intent(activity, AddAddressActivity::class.java)
            startForResult.launch(intent)
        }

        loadData()

    }

    private fun refresh(){
        arrayList.clear()
        loadData()
    }

    private fun loadData(){
        services.getListDetail(this,FirebaseFirestore.getInstance(),getUsers)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadingIndicator(isLoading: Boolean) {
        if(isLoading){
            layoutProgress.visibility = View.VISIBLE
        }else{
            layoutProgress.visibility = View.GONE
        }
    }

    override fun onRequestDetailSuccess(querySnapshot: DocumentSnapshot) {
        val detailAddress =querySnapshot.toObject(Address::class.java)
        idFirstAddress = detailAddress?.firstAddress.toString()
        services.getList(this, FirebaseFirestore.getInstance(),getUsers)
    }

    override fun onRequestDetailFailed(message: String) {
       setToast(message)
    }

    override fun onRequestSuccess(querySnapshot: QuerySnapshot) {
        for (doc in querySnapshot) {
            val articles = doc.toObject(Address::class.java)
            arrayList.add(articles)
        }
        swipeRefresh.isRefreshing = false
        updateList(arrayList)
        btnAddAddress.visibility = View.VISIBLE

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(arrayList: ArrayList<Address>) {
        mAdapter.setItems(arrayList,this,idFirstAddress)
        if(mAdapter.itemCount==0){
            layoutEmpty.visibility = View.VISIBLE
        }else{
            layoutEmpty.visibility = View.GONE
        }

        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }


    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    override fun onClick(position: Int, address: Address) {
        val intent = Intent()
        intent.putExtra("data",address)
        setResult(RESULT_OK,intent)
        finish()
    }

    override fun onClickEdit(position: Int, address: Address) {
        val intent = Intent(this,AddAddressActivity::class.java)
        if(idFirstAddress==address.id.toString()){
           address.address?.isChecked = true
        }
        intent.putExtra("data",address)
        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val load = data?.getBooleanExtra("load", false)
            if (load==true) {
                refresh()
            }
        }
    }
}
