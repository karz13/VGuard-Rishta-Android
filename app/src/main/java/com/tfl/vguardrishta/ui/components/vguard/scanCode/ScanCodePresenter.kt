package com.tfl.vguardrishta.ui.components.vguard.scanCode

import android.content.Context
import android.location.Location
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.CaptureSaleCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.CouponData
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import javax.inject.Inject

/**
 * Created by Shanmuka on 1/16/2019.
 */
class ScanCodePresenter @Inject constructor(
    private val context: Context,
    private val captureCaseSale: CaptureSaleCase
) : BasePresenter<ScanCodeContract.View>(),
    ScanCodeContract.Presenter {

    private lateinit var searchedMechanic: User

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun isValidBarcode(
        couponData: CouponData,
        isAirCooler: Int?,
        pinFourDigit: String?,
        isPinRequrired: Int?,
        dealerCategory: String?
    ) {
        getView()?.hideKeyboard()

        if (couponData.couponCode!!.isEmpty()) {
            getView()?.showToast(context.getString(R.string.error_barcode))
            return
        }
        if (couponData.couponCode!!.length < 16) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_sixteen_digit))
            return
        }
        if (isPinRequrired ==1 && pinFourDigit =="") {
            getView()?.showToast(context.getString(R.string.please_enter_pin))
            return
        }
        val loginUser = CacheUtils.getRishtaUser()
        couponData.isAirCooler = isAirCooler
        if (dealerCategory != null) {
            couponData.category=dealerCategory
        }

        if (loginUser.roleId == Constants.INF_USER_TYPE) {
            if(pinFourDigit !="")
            {
                couponData.pin = pinFourDigit
                processInfUserCouponForPin(couponData)
            }
            else
            processInfUserCoupon(couponData)
        } else {
            validateRetailerCoupon(couponData)
        }


    }

    private fun processInfUserCoupon(couponData: CouponData) {
        disposables?.add(captureCaseSale.execute(couponData).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    if (it.errorCode == 1) {
                        getView()?.clearCoupon()
                        getView()?.showScratchCard(it)
                        CacheUtils.refreshView(true)
                    }
                    else if (it.errorCode == 2) {
                       // getView()?.showToast("Enter PIN")
                        getView()?.showPINPopup(it)
                    }
                    else {
                        getView()?.showErrorDialog(
                            it.errorMsg
                                ?: context.getString(R.string.something_wrong),
                            false
                        )
                    }
                }
            }, {
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }


    private fun processInfUserCouponForPin(couponData: CouponData) {
        disposables?.add(captureCaseSale.sendCouponPin(couponData).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    if (it.errorCode == 1) {
                        getView()?.clearCoupon()
                        getView()?.showScratchCard(it)
                        CacheUtils.refreshView(true)
                    }
                    else {
                        getView()?.showErrorDialog(
                            it.errorMsg
                                ?: context.getString(R.string.something_wrong),
                            false
                        )
                    }
                }
            }, {
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }

    private fun validateRetailerCoupon(couponData: CouponData) {
        disposables?.add(captureCaseSale.validateRetailerCoupon(couponData).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    if (it.errorCode == 1) {
                        getView()?.verifyCategorySelected(it)
                    } else if (it.errorCode == 2) {
                        getView()?.showAircoolerDialog(it)
                    } else {
                        getView()?.showErrorDialog(
                            it.errorMsg
                                ?: context.getString(R.string.something_wrong),
                            false
                        )
                    }
                }
            }, {
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }

    override fun getScanPopUp(userCode: String?) {
        disposables?.add(captureCaseSale.checkScanPopUp(userCode).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if(it.code == "1")
                getView()?.processScanCodePopup(it)
            }, {
                //getView()?.showError()
            })
        )
    }



    override fun getBonusPoints(transactId: String) {
        disposables?.add(captureCaseSale.getBonusPoints(transactId).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.showBonusScratchCard(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun fetchMechanicDetails(mobileNum: String, retailerCoupon: Boolean?) {

        if (mobileNum.isEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_mobileNo))
            return
        } else if (!AppUtils.isValidMobileNo(mobileNum)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }

        getView()?.hideKeyboard()


    }


    override fun checkLocationUnderRadious(
        longitude: Double,
        latitude: Double,
        mechanicLongitude: Float?,
        mechanicLatitude: Float?
    ) {
        val result = FloatArray(1)
        if (mechanicLongitude != null && mechanicLatitude != null) {
            Location.distanceBetween(
                latitude,
                mechanicLatitude.toDouble(),
                longitude,
                mechanicLongitude.toDouble(),
                result
            )
            getView()?.scanningUnderLocation(result[0] <= 30000)
        }
    }

}