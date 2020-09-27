package sembako.sayunara.android.ui.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.main.MainMenuActivity

open class BaseFragment : Fragment() {
    fun isEmailValid(email: String?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getVersionName(context: Context): String? {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = requireActivity().currentFocus
        if (v != null) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun setToast(tipe: String?) {
        Toast.makeText(activity, tipe, Toast.LENGTH_SHORT).show()
    }

    fun setToast(tipe: Int) {
        Toast.makeText(activity, getString(tipe), Toast.LENGTH_SHORT).show()
    }

    fun isLogin() : Boolean{
        return (activity as BaseActivity?)!!.isLogin()
    }

    fun convertPhone(phoneNumber: String): String {
        var phoneNumber = phoneNumber
        phoneNumber = phoneNumber.replace("\\+62".toRegex(), "62")
        if (phoneNumber.startsWith("08")) {
            phoneNumber = phoneNumber.replace("08".toRegex(), "628")
        }
        return phoneNumber
    }

    fun confirmSignout() {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.dialog_info, null)
        val messageTv = view.findViewById<TextView>(R.id.tv_message)
        messageTv.text = "Apakah anda akan keluar aplikasi ?"
        val builder = getBuilder(activity)
        builder.setView(view)
                .setNegativeButton("Tidak") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .setPositiveButton("Ya"
                ) { dialog: DialogInterface?, which: Int ->
                    (activity as BaseActivity?)!!.clearUser()
                    val intent = Intent(activity, MainMenuActivity::class.java)
                    startActivity(intent)
                }
        builder.create().show()
    }

    fun getBuilder(context: Context?): AlertDialog.Builder {
        val builder: AlertDialog.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
        } else {
            AlertDialog.Builder(context)
        }
        return builder
    }

    fun saveUser(user: User) {
        return (activity as BaseActivity?)!!.saveUser(user)
    }


    open val getUsers: User?
        get() {
            return (activity as BaseActivity?)!!.getUsers
        }

}