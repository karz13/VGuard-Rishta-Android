package com.tfl.vguardrishta.ui.base

import androidx.lifecycle.LifecycleObserver
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference

abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V>, LifecycleObserver {

    protected var disposables: CompositeDisposable? = null
    protected var view: WeakReference<V>? = null

    override fun getView(): V? = view?.get()

    override fun attachView(view: V) {
        this.view = WeakReference(view)
        this.disposables = CompositeDisposable()
    }

    override fun detachView() {
        view?.clear()
        view = null
        disposables?.dispose()
        disposables = null
    }

    override fun onCreated() = Unit

    override fun onStarted() = Unit

    override fun onResumed() = Unit

    override fun onDestroyed() = Unit

    override fun onStopped() = Unit

    override fun onPaused() = Unit
}