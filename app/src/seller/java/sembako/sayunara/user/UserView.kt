package sembako.sayunara.user

import com.google.firebase.firestore.QuerySnapshot

interface UserView {


    interface List {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestFailed(message: String)
        fun onRequestSuccess(querySnapshot: QuerySnapshot)
    }

    interface ViewDetail {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess()
        fun onDeleteSuccess(message: String)
        fun onAvatarSuccess()
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}