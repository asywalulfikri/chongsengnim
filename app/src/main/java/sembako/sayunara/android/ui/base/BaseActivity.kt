package sembako.sayunara.android.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codemybrainsout.ratingdialog.RatingDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.rahman.dialog.Utilities.SmartDialogBuilder
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.blur.BlurBehind
import sembako.sayunara.android.helper.blur.OnBlurCompleteListener
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFirstActivity
import sembako.sayunara.android.ui.component.account.register.LocationGet
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.system.exitProcess

@SuppressLint("Registered")
 open class BaseActivity : AppCompatActivity() {
    var mFireBaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var latitude = "0.0"
    var longitude = "0.0"
    private var sharedPreferences: SharedPreferences? =null
    var login = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("login", 0)
        //isLogin = sharedPreferences!!.getBoolean("isLogin", false)
    }

    open fun showFragment(fragment: Fragment?, fragmentResourceID: Int) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(fragmentResourceID, fragment)
            fragmentTransaction.detach(fragment)
            fragmentTransaction.attach(fragment)
            fragmentTransaction.commit()
        }
    }

    open fun showFragmentAllowingStateLoss(fragment: Fragment?, fragmentResourceID: Int) {
        if (fragment != null) {
            val fragmentManager = this.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(fragmentResourceID, fragment)
            fragmentTransaction.detach(fragment)
            fragmentTransaction.attach(fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
    fun saveUser(user: User) {
        val editor = sharedPreferences!!.edit()
        editor.putString(Constant.UserKey.userId, user.profile.userId)
        editor.putString(Constant.UserKey.type, user.profile.type)
        editor.putString(Constant.UserKey.username, user.profile.username)
        editor.putString(Constant.UserKey.phoneNumber, user.profile.phoneNumber)
        editor.putString(Constant.UserKey.email, user.profile.email)
        editor.putBoolean(Constant.UserKey.isPartner, user.profile.isPartner)
        editor.putBoolean(Constant.UserKey.isActive, user.profile.isActive)
        editor.putString(Constant.UserKey.avatar, user.profile.avatar)
        editor.putBoolean(Constant.UserKey.isLogin, true)
        editor.apply()
    }

     fun getDevicesId():String{
         return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
     }

    @SuppressLint("InflateParams")
    open fun showDialogLogin(message: String?) {
        BlurBehind.instance!!.execute(activity, object : OnBlurCompleteListener {
            override fun onBlurComplete() {
                val intent = Intent(activity, LoginFirstActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivityForResult(intent, 1313)
            }
        })
    }

    fun clearUser() {
        val editor = sharedPreferences!!.edit()
        editor.putString(Constant.UserKey.userId, "")
        editor.putString(Constant.UserKey.type, "")
        editor.putString(Constant.UserKey.username, "")
        editor.putString(Constant.UserKey.phoneNumber, "")
        editor.putString(Constant.UserKey.email, "")
        editor.putBoolean(Constant.UserKey.isPartner, false)
        editor.putBoolean(Constant.UserKey.isActive, false)
        editor.putString(Constant.UserKey.avatar, "")
        editor.putBoolean(Constant.UserKey.isLogin, false)
        editor.apply()
    }

    fun getUserTinyDB() : User{
        return App.tinyDB!!.getObject(Constant.Session.userSession, User::class.java)
    }

    fun setToast(message: Int) {
        Toast.makeText(activity, getString(message), Toast.LENGTH_SHORT).show()
    }

    fun setToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun setLog(logName: String, message: String){
        Log.d(logName, message)
    }

    open val getUsers: User?
        get() {
            val user = User()
            user.profile.userId = sharedPreferences!!.getString(Constant.UserKey.userId, "")
            user.profile.type = sharedPreferences!!.getString(Constant.UserKey.type, "")
            user.profile.username = sharedPreferences!!.getString(Constant.UserKey.username, "")
            user.profile.phoneNumber = sharedPreferences!!.getString(Constant.UserKey.phoneNumber, "")
            user.profile.email = sharedPreferences!!.getString(Constant.UserKey.email, "")
            user.profile.avatar = sharedPreferences!!.getString(Constant.UserKey.avatar, "")
            user.profile.isPartner = (sharedPreferences!!.getBoolean(Constant.UserKey.isPartner, false))
            user.profile.isActive = sharedPreferences!!.getBoolean(Constant.UserKey.isActive, false)
            user.profile.avatar = sharedPreferences!!.getString(Constant.UserKey.avatar, "")
            user.isLogin = sharedPreferences!!.getBoolean(Constant.UserKey.isLogin, false)
            return user
        }

    fun isEmailValid(email: String?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
    }

     open fun hideKeyboard() {
         val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         val v: View = activity.currentFocus!!
         if (v != null) {
             imm.hideSoftInputFromWindow(v.windowToken, 0)
         }
     }


     fun isLogin():Boolean{
        login = sharedPreferences!!.getBoolean("isLogin", false)
        return login
    }


    fun getColors(@NonNull context: Context, @ColorRes id: Int): Int {
        return if (Build.VERSION.SDK_INT >= 23) {
            context.getColor(id)
        } else {
            context.resources.getColor(id)
        }
    }
    fun getVersionName(context: Context): String? {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun convertPhone(phoneNumber: String): String {
        var phoneNumber = phoneNumber
        phoneNumber = phoneNumber.replace("\\+62".toRegex(), "62")
        if (phoneNumber.startsWith("08")) {
            phoneNumber = phoneNumber.replace("08".toRegex(), "628")
        }
        return phoneNumber
    }

    @SuppressLint("ResourceAsColor", "PrivateResource")
    fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp)
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setTitleTextColor(R.color.black)
        toolbar.setTitleTextAppearance(this, R.style.textSizeToolbar)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }



    fun setupLinearLayoutManager() : LinearLayoutManager{
        return LinearLayoutManager(this)
    }

    @SuppressLint("NewApi")
    fun toolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp)
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    @SuppressLint("NewApi", "ResourceAsColor")
    fun setupToolbar(toolbar: Toolbar, title: String?) {
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp)
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)
        toolbar.setTitleTextColor(Color.GREEN)
        // toolbar.setTitleTextColor(R.color.green_1);
        toolbar.setTitleTextAppearance(this, R.style.textSizeToolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun getBuilder(context: Context?): AlertDialog.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
        } else {
            AlertDialog.Builder(context)
        }
    }

    fun convertPrice(price: Int): String {
        val harga = price.toString().toDouble()
        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs
        return df.format(harga)
    }


    fun setColorTintButton(button: MaterialButton, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val stateList = ColorStateList.valueOf(color)
            button.backgroundTintList = stateList
        } else {
            button.background.current.colorFilter = PorterDuffColorFilter(color,
                    PorterDuff.Mode.MULTIPLY)
        }
    }


    fun dialogExit() {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.dialog_info, null)
        val messageTv = view.findViewById<TextView>(R.id.tv_message)
        messageTv.text = activity
                .getString(R.string.text_confirm_exit)
        val builder = getBuilder(activity)
        builder.setView(view)
                .setNegativeButton(getString(R.string.text_no)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.text_yes)) { _, _ ->
                    finish()
                    finishAffinity()
                    exitProcess(0)
                }
        builder.create().show()
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION)
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION)
        }
    }


    open fun setLocation(lat: Double, lng: Double): LocationGet? {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val locationGet = LocationGet()
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            locationGet.province = addresses[0].adminArea
            locationGet.city = (addresses[0].subAdminArea)
            locationGet.district = (addresses[0].locality)
            locationGet.address = (addresses[0].getAddressLine(0))
            locationGet.latitude = (lat.toString())
            locationGet.longitude = (lng.toString())
            locationGet.zipCode = (addresses[0].postalCode)
            locationGet.subDistrict = (addresses[0].subLocality)
            Log.d("lera city", locationGet.toString() + "--")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return locationGet
    }

    @SuppressLint("NewApi")
    fun showSettingsDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.text_permission))
        builder.setMessage(HtmlCompat.fromHtml(getString(R.string.app_name), HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setPositiveButton(getString(R.string.text_goto_permission)) { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.show()
    }

    val activity: BaseActivity
        get() = this

    companion object {
        const val REQUEST_LOCATION = 0
        var PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        var PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA)
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }


    fun rating(){
        val ratingDialog: RatingDialog = RatingDialog.Builder(this)
                //.icon(drawable)
                .session(7)
                .threshold(3F)
                .title("How was your experience with us?")
                .titleTextColor(R.color.black)
                .positiveButtonText("Not Now")
                .negativeButtonText("Never")
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText(getString(R.string.text_cancel))
                .ratingBarColor(R.color.colorApp)
                .playstoreUrl("https://play.google.com/store/apps/details?id=sembako.sayunara.android")
                .onThresholdCleared { ratingDialog, rating, thresholdCleared -> //do something
                    ratingDialog.dismiss()
                }
                .onThresholdFailed { ratingDialog, rating, thresholdCleared -> //do something
                    ratingDialog.dismiss()
                }
                .onRatingChanged { rating, thresholdCleared ->

                }
                .onRatingBarFormSumbit {

                }.build()

        ratingDialog.show()
    }


    fun comingSoon(){
        SmartDialogBuilder(this)
                .setTitle(getString(R.string.text_notification))
                .setSubTitle(getString(R.string.text_coming_soon))
                .setCancalable(false)
                .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
                .setSubTitleFont(Typeface.SANS_SERIF) //set sub title font
                .setCancalable(true)
                .setNegativeButtonHide(true) //hide cancel button
                .setPositiveButton(getString(R.string.text_ok)) { smartDialog ->
                    smartDialog.dismiss()
                }.build().show()
    }
}