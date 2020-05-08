package sembako.sayunara.android.ui.base

interface IBasePresenter<in V : BaseView> {

    fun attachView(view: V)
    fun detachView()
}