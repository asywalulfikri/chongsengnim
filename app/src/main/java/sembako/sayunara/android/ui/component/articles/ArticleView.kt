package sembako.sayunara.android.ui.component.articles

import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import java.util.ArrayList

interface ArticleView {


    interface ViewList {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(arrayList: ArrayList<Articles>)
        fun onRequestFailed(code: Int)
    }

    interface ViewArticle {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}