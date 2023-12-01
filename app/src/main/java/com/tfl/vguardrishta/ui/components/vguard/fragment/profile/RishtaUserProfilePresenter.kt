package com.tfl.vguardrishta.ui.components.vguard.fragment.profile

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.*
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class RishtaUserProfilePresenter @Inject constructor(
    val context: Context,
    val authenticateUserCaseCase: AuthenticateUserCaseCase,
    val kycUseCase: KycUpdateUseCase,
     val stateMasterUseCase: StateMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val getRishtaUserProfileUseCase: GetRishtaUserProfileUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val professionTypesUseCase: ProfessionTypesUseCase,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase

) : BasePresenter<RishtaUserProfileContract.View>(), RishtaUserProfileContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getProfile() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.showRishtaUser(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


















}