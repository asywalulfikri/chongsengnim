package sembako.sayunara.apk

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.seller.activity_apk_detail.*
import sembako.sayunara.android.R
import sembako.sayunara.android.helper.blur.BlurBehind
import sembako.sayunara.android.helper.blur.OnBlurCompleteListener
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFirstActivity
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import com.google.firebase.firestore.FirebaseFirestore


class ApkDetailActivity : BaseActivity(),ApkView.ViewDetail{

    val services = ApkServices()
    var configSetup = ConfigSetup()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk_detail)

        setupToolbar(toolbar)
        configSetup = intent.getSerializableExtra("config") as ConfigSetup

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

    }

    override fun onRequestSuccess() {
        setToast("sukse")
    }


    @SuppressLint("InflateParams")
    override fun showDialogLogin(message: String?) {
        BlurBehind.instance!!.execute(activity as AppCompatActivity?, object : OnBlurCompleteListener {
            override fun onBlurComplete() {
                val intent = Intent(activity, LoginFirstActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivityForResult(intent, 1313)
            }
        })
    }


    override fun onRequestFailed(message: String) {
        setToast(message)
    }

    override fun setupViews() {
        etAppId.setText(configSetup.appId)
        etAppName.setText(configSetup.appName)
        etVersionName.setText(configSetup.config?.versionName)
        etSessionName.setText(configSetup.config?.newSession)
        etVersionCode.setText(configSetup.config?.versionCode.toString())
        switchForceUpdate.isChecked = configSetup.config!!.forceUpdate
        switchMaintenance.isChecked = configSetup.config!!.status


        switchForceUpdate.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "forceUpdate", isChecked,configSetup.appId.toString())
        }

        switchMaintenance.setOnCheckedChangeListener { _, isChecked ->
            services.editStatus(this, "status", isChecked, configSetup.appId.toString())
        }
    }


    fun updateData(status : Boolean){

    }



}
