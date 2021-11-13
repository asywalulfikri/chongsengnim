package sembako.sayunara.android.ui.component.splashcreen

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginActivity
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigApp
import sembako.sayunara.android.ui.util.*
import sembako.sayunara.constant.valueApp
import sembako.sayunara.main.MainActivity

class SplashScreenActivity : BaseActivity() {

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    private fun fetchMainConfig() {
        FirebaseApp.initializeApp(activity)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task: Task<Boolean?> ->
                if (task.isSuccessful) {
                    setLog("fetch", "success")
                } else {
                    setLog("fetch", "failed")
                }
                updateConfig()
            }
    }

    private fun updateConfig() {
        val json = mFirebaseRemoteConfig?.getString("update_config_sayunara")
        val configApp = Gson().fromJson(json, ConfigApp::class.java)

        setLogResponse(json.toString())

        val versionCode = BuildConfig.VERSION_CODE

        if (versionCode < configApp.versionCode!! && configApp.requiredUpdate) {

            showUpdateDialog()

        } else {
            toDashboard()
        }
    }

    public override fun onResume() {
        super.onResume()
        fetchMainConfig()
    }


    private fun toDashboard() {
        if(valueApp.AppInfo.versionName == "sembako.sayunara.android"){
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