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
import android.content.Intent
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_product.*
import sembako.sayunara.android.helper.Preview
import java.lang.StringBuilder
import java.util.*

class UserDetailActivity : BaseActivity(),UserView.ViewDetail{

    val services = UserServices()
    var user = User()
    var choose : String? = null
    var load = false
    private var preview: Preview? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail_admin)

        setupToolbar(toolbar)
        user = intent.getSerializableExtra("user") as User

        setupViews()

    }

    private fun generateSearch(){

        val arrayType: List<String>
        val search = user.profile.username.toString().toLowerCase().trim()+","+user.profile.email.toString().trim().toLowerCase()+","+user.profile.phoneNumber.toString().trim().toLowerCase()
        val xx = search.replace(" ",",").toLowerCase()
        Log.d("isinya",xx+"---")
        arrayType = xx.split(",")
        services.generateSearch(this,user.profile.userId.toString(),arrayType)

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
            layout_progress.visibility = View.VISIBLE
        }else{
            layout_progress.visibility = View.GONE
        }
    }

    override fun onRequestSuccess() {
        setToast(getString(R.string.text_success_update_profile))
        load = true
    }

    override fun onDeleteSuccess(message: String) {
        setToast(message)
        onBackPressed()
    }

    override fun onAvatarSuccess() {
        updatedAvatar(etAvatar.text.toString())
    }

    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    private fun updatedAvatar(url : String){
        if(url!=""){

           if(getSuperAdmin()||getAdmin()){
               val color = resources.getColor(R.color.grey_200)
               val previewView: View = layoutInflater.inflate(R.layout.preview_layout, null)
               val previewImageView = previewView.findViewById<ImageView>(R.id.image_preview)

               Picasso.get()
                   .load(url)
                   .into(previewImageView)

               Picasso.get()
                   .load(url)
                   .into(ivAvatar)

               preview = Preview.Builder(activity)
                   .setContentView(previewView)
                   .setBackground(color)
                   .build()

               ivAvatar.setOnClickListener {
                   preview?.show()
               }
           }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun setupViews() {

        updatedAvatar(user.profile.avatar.toString())

        etUsername.setText(user.profile.username)
        etPhoneNumber.setText(user.profile.phoneNumber)

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
        etAvatar.text = user.profile.avatar

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

        etAvatar.setOnClickListener {
            dialogUrl()
        }

        if(user.profile.type==Constant.userType.typeSuperAdmin){
            llSwitch.visibility = View.GONE
            llType.visibility = View.GONE
        }else if(user.profile.type==Constant.userType.typeAdmin){

            if(getSuperAdmin()){
                llSwitch.visibility = View.VISIBLE
                etType.isClickable = true
                llType.visibility = View.VISIBLE
            }else{
                llSwitch.visibility = View.GONE
                etType.isClickable = false
                llType.visibility = View.GONE
            }
        }else{
            llSwitch.visibility = View.VISIBLE
            llType.visibility = View.VISIBLE
        }

        if(getSuperAdmin()){
            if(user.profile.userId==getUsers?.profile?.userId){
                btnDelete.visibility = View.GONE
            }else{
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener {
                    dialogDelete()
                }
            }
            etEmail.setText(user.profile.email)
        }else{
            if(user.profile.type==Constant.userType.typeSuperAdmin){
                etEmail.setText(setMasking(user.profile.email.toString()))
                etUserId.setText(setMasking(user.profile.userId.toString()))
                etPhoneNumber.setText(setMasking(user.profile.phoneNumber.toString()))
                etFirebaseToken.setText(setMasking(user.profile.firebaseToken.toString()))
                etMarketLocation.setText(setMasking(user.profile.marketLocation.toString()))
                etAvatar.text = setMasking(user.profile.avatar.toString())
                etAvatar.isClickable = false
                etType.isClickable = false
                etDeviceInfo.setText(setMasking(user.devices.detailDevices.toString()))
                etRt.setText(setMasking(user.locations.rt.toString()))
                etRw.setText(setMasking(user.locations.rw.toString()))
                etProvince.setText(setMasking(user.locations.province.toString()))
                etSubDistrict.setText(setMasking(user.locations.subDistrict.toString()))
                etPostalCode.setText(setMasking(user.locations.postalCode.toString()))
                etCity.setText(setMasking(user.locations.city.toString()))
            }else{
                etEmail.setText(user.profile.email)
            }
        }

    }

    private fun setNameMasking(str: String): String? {
        return maskString(str, 1, str.length, 'x')
    }

    private fun maskString(strText: String?, start: Int, end: Int, maskChar: Char): String? {
        var start = start
        var end = end
        if (strText == null || strText == "") return ""
        if (start < 0) start = 0
        if (end > strText.length) end = strText.length
        if (start > end) {
        }
        val maskLength = end - start
        if (maskLength == 0) return strText
        val sbMaskString = StringBuilder(maskLength)
        for (i in 0 until maskLength) {
            sbMaskString.append(maskChar)
        }
        return (strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength))
    }


    private fun setMasking(str: String): String {
        val strArray = str.split(" ").toTypedArray()
        val builder = StringBuilder()
        for (s in strArray) {
            val cap = s.substring(0, 1).uppercase(Locale.getDefault()) + s.substring(1)
            val stm = setNameMasking(cap)
            builder.append("$stm ")
        }
        return builder.toString()
    }


    private fun dialogDelete(){
        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_confirm_delete_user)+" "+user.profile.username +"? "+"Data akan terhapus permanen ")
            .setCancalable(false)
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
            .setCancalable(true)
            .setNegativeButtonHide(true) //hide cancel button
            .setPositiveButton(getString(R.string.text_delete)) { smartDialog ->
                services.deleteAccount(this,user.profile.userId.toString())
                smartDialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { smartDialog ->
                smartDialog.dismiss()
            }.build().show()
    }



    @SuppressLint("InflateParams")
    fun dialogUrl() {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_input_url, null)
        val etUrl = view.findViewById<EditText>(R.id.etUrlImage)
        val ivClear = view.findViewById<ImageView>(R.id.ivClear)

        ivClear.setOnClickListener {
            etUrl.setText("")
        }

        etUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()){
                    ivClear.visibility = View.VISIBLE
                }else{
                    ivClear.visibility = View.GONE
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int,
                                           count: Int, after: Int) {
                if(s.toString().isNotEmpty()){
                    ivClear.visibility = View.VISIBLE
                }else{
                    ivClear.visibility = View.GONE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int, count: Int) {

            }
        })


        etUrl.setText(user.profile.avatar)

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
            .setNegativeButton(getString(R.string.text_cancel)) {
                    dialog, _ ->
                dialog.dismiss()
                hideKeyboard()
            }
            .setPositiveButton(getString(R.string.text_save)
            ) { dialog, _ ->
                services.editStatus(this, "avatar", etUrl.text.toString(),user.profile.userId.toString())
                etAvatar.text = etUrl.text.toString()
                hideKeyboard()
                dialog.dismiss()

            }
        builder.create().show()
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


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("load",load)
        setResult(RESULT_OK,intent)
        finish()

    }


}
