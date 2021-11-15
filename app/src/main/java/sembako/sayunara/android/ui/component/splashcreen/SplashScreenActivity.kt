package sembako.sayunara.android.ui.component.splashcreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rahman.dialog.Utilities.SmartDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.activity_splash_screen.*
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginActivity
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.android.ui.util.*
import sembako.sayunara.constant.valueApp
import sembako.sayunara.main.MainActivity


class SplashScreenActivity : BaseActivity() {

    private lateinit var viewModel: SplashScreenViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        tvVersion.text = getString(R.string.text_version)+" "+BuildConfig.VERSION_NAME

        viewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)

        viewModel.state.observe(this, Observer {
            when (it) {
                is SplashScreenState.Requesting -> {
                    animationView.visibility = View.VISIBLE
                }
                is SplashScreenState.OnFailed -> {
                    setToast(it.message)
                }
                is SplashScreenState.OnSuccess -> {

                    val configApp = it.configSetup
                    val versionCodeApk = BuildConfig.VERSION_CODE
                    val versionServer = configApp.config?.versionCode

                    if(configApp.config?.status== true){
                        showMaintenanceDialog()
                    }else{
                        if (versionCodeApk < versionServer!!) {
                            if(configApp.config?.forceUpdate==true){
                                showUpdateDialog(true)
                            }else{
                                showUpdateDialog(false)
                            }
                        } else {
                            toDashboard()
                        }

                    }
                }

            }
        })

    }

    private fun showUpdateDialog(forceUpdate : Boolean) {
        var message = ""
        message = if(forceUpdate){
            getString(R.string.text_exit)
        }else{
            getString(R.string.text_later)
        }

        SmartDialogBuilder(activity)
            .setTitle(getString(R.string.text_notification))
            .setSubTitle(getString(R.string.text_dialog_update))
            .setTitleFont(Typeface.DEFAULT_BOLD) //set title font
            .setSubTitleFont(Typeface.DEFAULT) //set sub title font
            .setCancalable(false)
            .setNegativeButtonHide(false) //hide cancel button
            .setNegativeButton(message) { smartDialog ->
                smartDialog.dismiss()
                if(forceUpdate){
                    finish()
                }else{
                    toDashboard()
                }

            }
            .setPositiveButton(getString(R.string.text_update)) { smartDialog ->
                smartDialog.dismiss()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)))
            }.build().show()

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTask()
    }

    private fun toDashboard() {
        if(isCustomer()){
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }else{
            if(isLogin()){
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun checkLocationPermission() {
        if (checkCameraPermission() && checkWriteExStoragePermission() && checkReadExtStoragePermission() && checkPermissionFineLocation() && checkPermissionCoarseLocation()) {
            toDashboard()
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            toDashboard()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // permission is denied permanently, navigate user to app settings
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    //TODO change text
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }
}
