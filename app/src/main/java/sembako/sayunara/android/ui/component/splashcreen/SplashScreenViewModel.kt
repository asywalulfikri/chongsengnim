package sembako.sayunara.android.ui.component.splashcreen

import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import sembako.sayunara.android.App
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.constant.valueApp
import com.google.firebase.firestore.DocumentSnapshot

import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.firestore.DocumentReference
import java.io.StringReader


class SplashScreenViewModel : ViewModel() {

    val state = MutableLiveData<SplashScreenState>()


    fun loadTask() {
        state.postValue(SplashScreenState.Requesting)
        val mFireBaseFireStore = FirebaseFirestore.getInstance()
        val docRef = mFireBaseFireStore.collection(Constant.Collection.COLLECTION_CONFIG).document(valueApp.AppInfo.applicationId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val configApp = task.result.toObject(ConfigSetup::class.java)
                state.postValue(SplashScreenState.OnSuccess(configApp!!))
                saveConfig(configApp)

            } else {
                state.postValue(SplashScreenState.OnFailed(R.string.text_server_error))
            }
        }




        /*docRef.get().addOnSuccessListener { documentSnapshot ->
            val configSetup: ConfigSetup = documentSnapshot.toObject(ConfigSetup::class.java)!!
            Log.d("psdk",Gson().toJson(configSetup))
            state.postValue(SplashScreenState.OnSuccess(configSetup))
            saveConfig(configSetup)
        }.addOnFailureListener {
            state.postValue(SplashScreenState.OnFailed(R.string.text_server_error))
        }
*/

    }



    private fun saveConfig(configSetup: ConfigSetup) {
        App.tinyDB?.putObject(Constant.Session.configApp, configSetup)
    }
}
