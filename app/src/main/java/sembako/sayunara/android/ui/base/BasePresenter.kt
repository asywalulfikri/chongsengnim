package sembako.sayunara.android.ui.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<V : BaseView>() : IBasePresenter<V> {
    var view : V? = null

    private val mCompositeDisposable by lazy {
        CompositeDisposable()
    }


    protected fun addDisposable(subscription: Disposable) {
        mCompositeDisposable.add(subscription)
    }

    protected fun stopDisposable(){
        mCompositeDisposable.dispose()
    }


    override fun attachView(view: V) {
        view.setPresenter(this)
        this.view = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
    }
}