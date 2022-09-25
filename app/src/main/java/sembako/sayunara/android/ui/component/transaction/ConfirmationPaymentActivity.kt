package sembako.sayunara.android.ui.component.transaction

import android.content.Intent
import android.os.Bundle
import sembako.sayunara.android.databinding.ActivityConfirmationBasketBinding
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.address.AddAddressActivity
import sembako.sayunara.android.ui.component.account.address.ListAddressActivity
import sembako.sayunara.android.ui.component.account.address.mapaddress.MapsPickAddressActivity


class ConfirmationPaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityConfirmationBasketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)

        binding.rlLocation.setOnClickListener {
            val intent = Intent(activity, ListAddressActivity::class.java)
            startActivityForResult(intent,1313)
        }
    }
}

