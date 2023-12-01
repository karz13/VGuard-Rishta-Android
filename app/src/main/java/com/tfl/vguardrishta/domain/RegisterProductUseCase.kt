package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject

class RegisterProductUseCase @Inject constructor(val remoteDataSource: RemoteDataSource) :
    UseCase<Unit, Single<List<ProductDetail>>>() {
    override fun execute(parameters: Unit): Single<List<ProductDetail>> {
        return remoteDataSource.getRetProductCategories()
    }

    fun getRetailerCategoryDealIn(): Single<List<Category>> {
        return remoteDataSource.getRetailerCategoryDealIn()
    }

    fun getCustDetByMobile(mobileNo: String): Single<CustomerDetailsRegistration?> {
        return remoteDataSource.getCustDetByMobile(mobileNo)
    }

    fun validateMobile(mobileNo: String, dealerCategory: String): Single<MobileValidation> {
        return remoteDataSource.validateMobile(mobileNo,dealerCategory)
    }

    fun sendCustomerData(cdr: CustomerDetailsRegistration): Single<CouponResponse> {
        return remoteDataSource.sendCustomerData(cdr)
    }

    fun senAirCoolerData(cdr: CustomerDetailsRegistration)= remoteDataSource.senAirCoolerData(cdr)


}