package sembako.sayunara.android.ui.component.product.editProduct

import sembako.sayunara.android.ui.base.BaseView
import sembako.sayunara.android.ui.component.account.login.data.model.User

/**
 * Created by asywalulfikri
 * Android Developer
 * Sayunara
 */
class PostProductContract {

    interface PostProductView : BaseView {
        val mProductId : String?
        val mProductName: String?
        val mProductType: String?
        val mProductWeight: String?
        val mProductUnit: String?
        val mProductPrice: String?
        val mProductStock: String?
        val mProductDescription: String?
        val mProductDiscount : String?
        val mLatitude:String?
        val mLongitude:String?
        val mVersionName : String?
        val mImages : List<String>
        val mImagesHasDelete : List<String>
        val isHighLight: Boolean?
        val mArrayType : List<String>
        fun showErrorValidation(message: Int)
        fun loadingIndicator(isLoading: Boolean)
        fun setColorButton(color: Int)
        val getUser : User?
        fun onBack()

    }

    interface PostActionListener {
        fun checkData(isEdit: Boolean)
        fun postProduct(isEdit: Boolean, isDraft: Boolean)
        fun onRequestSuccess()
        fun onRequestFailed(error: Int)
    }

}