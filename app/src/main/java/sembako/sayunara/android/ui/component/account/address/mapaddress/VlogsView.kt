package sembako.sayunara.android.ui.component.account.address.mapaddress


interface VlogsView {

    fun loadingIndicator(isLoading: Boolean)
    fun onRequestSuccess(`object`: Any,type :String)
    fun onRequestFailed(code: Int?,type : String)
    fun onNetworkError(code: Int?)
}
