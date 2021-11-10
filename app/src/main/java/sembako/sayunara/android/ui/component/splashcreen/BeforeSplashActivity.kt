package sembako.sayunara.android.ui.component.splashcreen

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.main.MainActivity
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigApp

class BeforeSplashActivity : BaseActivity() {

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
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    public override fun onResume() {
        super.onResume()
        fetchMainConfig()
    }
}