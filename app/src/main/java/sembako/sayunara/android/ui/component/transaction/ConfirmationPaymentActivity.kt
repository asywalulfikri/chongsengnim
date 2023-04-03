package sembako.sayunara.android.ui.component.transaction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_empty.*
import sembako.sayunara.android.databinding.ActivityConfirmationBasketBinding
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.address.Address
import sembako.sayunara.android.ui.component.account.address.ListAddressActivity
import sembako.sayunara.android.ui.component.basket.model.Basket
import sembako.sayunara.android.ui.component.basket.model.ListBasket
import sembako.sayunara.android.ui.component.product.listproduct.model.Product


class ConfirmationPaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityConfirmationBasketBinding
    var basket : ListBasket? = null
    var productArrayList: ArrayList<Product?> = ArrayList()
    var mAdapter = ConfirmationProductAdapter()
    var basketArrayList =  ArrayList<Basket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar)

        if(intent.hasExtra("basket")&&intent.hasExtra("product")){
            basket = intent.getSerializableExtra("basket") as ListBasket;
            productArrayList = intent.getSerializableExtra("product") as ArrayList<Product?>
            basketArrayList = intent.getSerializableExtra("item") as ArrayList<Basket>
            setupDataProduct()
        }

        binding.rlLocation.setOnClickListener {
            val intent = Intent(activity, ListAddressActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun setupDataProduct(){

        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(activity);
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            adapter = mAdapter
        }

        updateList()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateList() {
        mAdapter.setItems(productArrayList,basketArrayList)
        binding.recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data?.hasExtra("data") == true){
                val address = data.getSerializableExtra("data") as Address
                updateView(address)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(address: Address){
        binding.rlAddress.visibility = View.VISIBLE
        binding.infoLocation.visibility = View.GONE
        binding.tvName.text = address.contact?.name
        var phone = address.contact?.phoneNumber.toString()
        if (phone.startsWith("62")) "0" + phone.substring(2, phone.length) else phone.also {
            phone = it
        }
        binding.tvPhoneNumber.text = "(+62) "+ phone.removePrefix("0")
        binding.tvFullAddress.text = address.address?.fullAddress.toString()
        binding.tvProvince.text = address.address?.village.toString() +", "+address.address?.subDistrict.toString() + ","+address.address?.province.toString()

    }
}

