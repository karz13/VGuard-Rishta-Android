package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.CouponData
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.models.CustomerDetailsRegistration
import com.tfl.vguardrishta.models.MobileValidation
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 1/17/2019.
 */
class CaptureSaleCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<CouponData, Single<CouponResponse>>() {
    override fun execute(parameters: CouponData): Single<CouponResponse> =
        dataRepository.uploadBarcodeList(parameters)

    fun sendCouponPin(couponData: CouponData): Single<CouponResponse> =
        dataRepository.sendCouponPin(couponData)

    fun sendAccessoryCoupon(couponData: CouponData): Single<CouponResponse?> =
        dataRepository.sendAccessoryCoupon(couponData)

    fun getBonusPoints(transactId: String): Single<CouponResponse> {
        return  dataRepository.getBonusPoints(transactId)
    }

    fun validateRetailerCoupon(couponData: CouponData): Single<CouponResponse> {
        return dataRepository.validateRetailerCoupon(couponData)
    }

    fun checkScanPopUp(userCode: String?): Single<MobileValidation> {
        return dataRepository.checkScanPopUp(userCode)
    }

}