package sembako.sayunara.android.ui.component.articles

import com.google.firebase.firestore.QuerySnapshot

interface ArticleView {


    interface ViewList {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(querySnapshot:QuerySnapshot)
        fun onRequestFailed(message: String)
        fun onStatusChange(param : String,position : Int , value : Boolean)
    }

    interface DetailArticle {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(articles: Articles?)
        fun onRequestFailed(message: String)
        fun onStatusChange(param : String,position : Int , value : Boolean)
    }

    interface ViewArticle {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(message : String, draft : Boolean, id : String)
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}