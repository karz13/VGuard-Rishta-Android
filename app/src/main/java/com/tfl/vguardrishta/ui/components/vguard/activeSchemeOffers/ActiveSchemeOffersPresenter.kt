package com.tfl.vguardrishta.ui.components.vguard.activeSchemeOffers

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.CategoryListUseCase
import com.tfl.vguardrishta.domain.DownloadDataUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/9/2019.
 */
class ActiveSchemeOffersPresenter @Inject constructor(
    private val context: Context,
    private var categoryListUseCase: CategoryListUseCase,
    private val downloadDataUseCase: DownloadDataUseCase
) :
    BasePresenter<ActiveSchemeOffersContract.View>(),
    ActiveSchemeOffersContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }






    override fun getActiveSchemeOffers() {
        disposables?.add(downloadDataUseCase.getActiveSchemeOffers().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                setListToDownLoadAdapter(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }



    private fun setListToDownLoadAdapter(it: List<DownloadData>?) {
        if (it.isNullOrEmpty()) {
            getView()?.showNoData()
        } else {
            getView()?.setDownLoadsToAdapter(it)
        }
    }

    override fun openSchemesFile(it: DownloadData) {
        getView()?.openFile(it)
    }

}