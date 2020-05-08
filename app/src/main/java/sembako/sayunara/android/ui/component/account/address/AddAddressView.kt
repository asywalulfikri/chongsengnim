package sembako.sayunara.android.ui.component.account.address

import android.widget.EditText

interface AddAddressView {
    val location : EditText
    val placeID : String?
    val placeName : EditText
    val latLong : Boolean
    val mPhoneNumber : String
    val mReceiver : String
    fun setColorButton(color: Int)
    fun showErrorValidation(message: Int)
    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess()
    fun onRequestFailed(code: Int?)
    fun setupViews()
}