package com.tfl.vguardrishta.ui.components.vguard.scanCode

import com.tfl.vguardrishta.models.CouponData
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.models.MobileValidation
import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 1/16/2019.
 */
interface ScanCodeContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView()

        fun showProgress()

        fun stopProgress()

        fun showToast(message: String)

        fun showLongToast(message: String)


        fun hideKeyboard()


        fun clearMobileNumber()

        fun clearCoupon()

        fun showScratchCard(it: CouponResponse?)
        fun showPINPopup(it: CouponResponse?)
        fun scanningUnderLocation(isUnderLocation: Boolean)
        fun showErrorDialog(s: String, isSingleScan: Boolean)
        fun showBonusScratchCard(it: CouponResponse)
        fun verifyCategorySelected(cresp: CouponResponse)
        fun showAircoolerDialog(it: CouponResponse?)
        fun processScanCodePopup(it: MobileValidation?)
    }

    interface Presenter : BaseContract.Presenter<View> {

        fun isValidBarcode(
            couponData: CouponData,
            isAirCooler: Int?,
            isPinPopUp: String?,
            isPinRequrired: Int?,
            dealerCategory: String?
        )

        fun fetchMechanicDetails(mobileNum: String, retailerCoupon: Boolean?)
        fun checkLocationUnderRadious(
            longitude: Double,
            latitude: Double,
            mechanicLongitude: Float?,
            mechanicLatitude: Float?
        )

        fun getBonusPoints(transactId: String)

        fun getScanPopUp(userCode: String?)
    }
}