package sembako.sayunara.android.ui.component.splashcreen

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.gcm.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigApp
import sembako.sayunara.constant.valueApp


class SplashScreenViewModel : ViewModel() {

    val state = MutableLiveData<SplashScreenState>()

    fun loadTask() {
        state.postValue(SplashScreenState.Requesting)
        val mFireBaseFireStore = FirebaseFirestore.getInstance()
        val docRef = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_CONFIG).document(valueApp.AppInfo.applicationId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val configApp = task.result?.toObject(ConfigApp::class.java)
                Log.d("ptk",Gson().toJson(configApp)+"--")
                state.postValue(SplashScreenState.OnSuccess(configApp!!))
                saveConfig(configApp)

            } else {
                state.postValue(SplashScreenState.OnFailed(R.string.text_server_error))
            }
        }
    }

    private fun saveConfig(configApp: ConfigApp) {
        App.tinyDB?.putObject(Constant.Session.configApp, configApp)
    }
}
