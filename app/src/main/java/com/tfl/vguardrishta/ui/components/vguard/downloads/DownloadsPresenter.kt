package com.tfl.vguardrishta.ui.components.vguard.downloads

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
class DownloadsPresenter @Inject constructor(
    private val context: Context,
    private var categoryListUseCase: CategoryListUseCase,
    private val downloadDataUseCase: DownloadDataUseCase
) :
    BasePresenter<DownloadsContract.View>(),
    DownloadsContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getCategory() {
        disposables?.add(categoryListUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processList(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processList(it: List<Category>?) {
        if (it.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.no_category_found))
        } else {
            val arrayList = CacheUtils.getFirstCategory(context)
            arrayList.addAll(it)
            getView()?.setToSpinner(arrayList)
        }
    }



    override fun getDownloads() {
        disposables?.add(downloadDataUseCase.execute("").applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                setListToDownLoadAdapter(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun getVguardInfoDownloads() {

        disposables?.add(downloadDataUseCase.getVguardInfoDownloads().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                setListToDownLoadAdapter(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun getVguardCatlogDownloads() {
        disposables?.add(downloadDataUseCase.getVguardCatlogDownloads().applySchedulers()
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

    override fun openDownLoadData(it: DownloadData) {
        getView()?.openFile(it)
    }

}