package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject


class GetWhatsNewUseCase @Inject constructor(private val dataRepository: DataRepository,
val remoteDataSource: RemoteDataSource) :
    UseCase<Unit, Single<List<WhatsNew>>>() {

    override fun execute(parameters: Unit): Single<List<WhatsNew>> = dataRepository.getWhatsNew()
    fun getBonusRewards(): Single<List<CouponResponse>> {
        return dataRepository.getBonusRewards()
    }

    fun getWelfarePdfList(): Single<List<DownloadData>> {
        return dataRepository.getWelfarePdfList()
    }

    fun getDailyWinner(date: DailyWinner?): Single<List<DailyWinner>> {
        return remoteDataSource.getDailyWinner(date)
    }

    fun getDailyWinnerDates(): Single<List<DailyWinner>> {
        return remoteDataSource.getDailyWinnerDates()
    }

    fun getAy(): Single<List<String>> = dataRepository.getAy()
    fun getTdsList(accessmentYear : String): Single<List<TdsData>> {
        return dataRepository.getTdsList(accessmentYear)
    }
    fun getFiscalYear(): Single<List<String>> = dataRepository.getFiscalYear()
    fun getMonth():Single<List<MonthData>> = dataRepository.getMonth()
    fun getTdsStatementList(month: MonthData):  Single<List<TdsStatement>>  = dataRepository.getTdsStatementList(month)

}