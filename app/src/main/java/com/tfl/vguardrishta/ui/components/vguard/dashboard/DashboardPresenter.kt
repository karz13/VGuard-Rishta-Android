package com.tfl.vguardrishta.ui.components.vguard.dashboard

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.GetMonthWiseEarningUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import io.paperdb.Paper
import javax.inject.Inject

class DashboardPresenter @Inject constructor(
    val context: Context,
    val getMonthWiseEarningUseCase: GetMonthWiseEarningUseCase,
    val authenticateUserCaseCase: AuthenticateUserCaseCase
) : BasePresenter<DashboardContract.View>(), DashboardContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getMonthWiseEarning(month: Int, year: Int) {
        val pair = Pair(month.toString(), year.toString())

        disposables?.add(getMonthWiseEarningUseCase.execute(pair).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.showPointsSummary(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getUserDetails() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    saveUserShowPoints(it)
                }
            }, {

                getView()?.showToast(context.getString(R.string.something_wrong))

            })
        )
    }

    private fun saveUserShowPoints(it: VguardRishtaUser) {
        Paper.book().write(Constants.KEY_RISHTA_USER, it)
        getView()?.showUserDetails()
        CacheUtils.refreshView(false)
    }
}