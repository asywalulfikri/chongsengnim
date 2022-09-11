package sembako.sayunara.android.ui.component.banner

import com.google.firebase.firestore.QuerySnapshot

interface BannerView {


    interface List {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestFailed(message: String)
        fun onRequestSuccess(querySnapshot: QuerySnapshot)
    }

    interface ViewDetail {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onDeleteSuccess(message: String)
        fun onRequestFailed(message : String)
        fun onRequestDetailSuccess(banner: Banner?)
        fun setupViews(banner: Banner?)
    }

    interface Post {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onRequestImageSuccess(url : String)
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}