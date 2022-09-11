package sembako.sayunara.android.ui.component.product.listproduct

import com.google.firebase.firestore.QuerySnapshot
import sembako.sayunara.android.ui.component.product.listproduct.model.Product

interface ProductView {


    interface List {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(querySnapshot:QuerySnapshot)
        fun onRequestFailed(message: String)
        fun onStatusChange(position : Int ,param : String, value : Boolean, isPublish : String, publish : Boolean)
    }

    interface Detail {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(product: Product?)
        fun onRequestFailed(message: String)
        fun onStatusChange(param : String,position : Int , value : Boolean)
    }

    interface Action {
        fun loadingIndicator(isLoading: Boolean)
        fun onRequestSuccess(message : String, draft : Boolean, id : String)
        fun onRequestFailed(message : String)
        fun setupViews()
    }
}