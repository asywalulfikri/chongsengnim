package sembako.sayunara.user

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.seller.activity_user_detail_admin.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import android.content.DialogInterface

import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import sembako.sayunara.main.MainActivity





class UserDetailActivity : BaseActivity(),UserView.ViewDetail{

    val services = UserServices()
    var user = User()
    var choose : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail_admin)

        setupToolbar(toolbar)
        user = intent.getSerializableExtra("user") as User

        setupViews()

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadingIndicator(isLoading: Boolean) {
       // TODO("Not yet implemented")
    }

    override fun onRequestSuccess() {
        //TODO("Not yet implemented")
    }

    override fun onRequestFailed(message: String) {
       // TODO("Not yet implemented")
    }


    @SuppressLint("SetTextI18n")
    override fun setupViews() {
        etUsername.setText(user.profile.username)
        etPhoneNumber.setText(user.profile.phoneNumber)
        etEmail.setText(user.profile.email)
        etUserId.setText(user.profile.userId)
        etType.text = user.profile.type
        etMarketLocation.setText(user.profile.marketLocation)
        switchActive.isChecked = user.profile.active == true
        switchSuspend.isChecked = user.profile.suspend == true
        switchVerification.isChecked = user.profile.verified == true
        switchPartner.isChecked = user.profile.partner == true
        etFirebaseToken.setText(user.profile.firebaseToken)
        etDeviceInfo.setText(user.devices.detailDevices + ", SDK "+ user.devices.versionAndroid)
        etProvince.setText(user.locations.province)
        etCity.setText(user.locations.city)
        etSubDistrict.setText(user.locations.subDistrict)
        etRt.setText(user.locations.rt)
        etRw.setText(user.locations.rw)
        etPostalCode.setText(user.locations.postalCode)

        if(user.profile.type==Constant.userType.typeSuperAdmin){
            llSwitch.visibility = View.GONE
            llType.visibility = View.GONE
        }else{
            llSwitch.visibility = View.VISIBLE
            etType.isClickable = true
            llType.visibility = View.VISIBLE
        }
        switchActive.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "active", isChecked,user.profile.userId.toString())
        }

        switchSuspend.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "suspend", isChecked, user.profile.userId.toString())
        }

        switchVerification.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "verified", isChecked, user.profile.userId.toString())
        }

        switchPartner.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "partner", isChecked, user.profile.userId.toString())
        }

        etType.setOnClickListener {
            dialogType(getUsers?.profile?.type.toString())
        }
    }


    private fun dialogType(type : String){

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Change Type To")

        val arrayType : Array<String>
        if(type==Constant.userType.typeSuperAdmin){
            arrayType = arrayOf("superadmin","admin", "seller","user")
        }else{
            arrayType = arrayOf("admin","seller","user")
        }

        val checkedItem = -1 // cow
        builder.setSingleChoiceItems(arrayType, checkedItem,
            DialogInterface.OnClickListener { dialog, which ->
               choose =(arrayType[which])
            })

        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            etType.text = choose
            if(choose!=user.profile.type.toString()){
                services.editStatus(this, "type",choose.toString(), user.profile.userId.toString())
            }
        })
        builder.setNegativeButton(getString(R.string.text_cancel), null)
        val dialog: android.app.AlertDialog? = builder.create()
        dialog?.show()

    }


}
