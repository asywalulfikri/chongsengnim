package sembako.sayunara.apk

import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import java.util.ArrayList

interface ApkView {


    interface ViewList {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(arrayList: ArrayList<ConfigSetup>)
        fun onRequestFailed(code: Int?)
    }

    interface ViewDetail {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}