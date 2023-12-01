package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.DownloadData
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/9/2019.
 */
class DownloadDataUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<List<DownloadData>>>() {
    override fun execute(parameters: String): Single<List<DownloadData>> = dataRepository.getDownloads(parameters)
    fun getVguardInfoDownloads(): Single<List<DownloadData>> {
        return dataRepository.getVguardInfoDownloads()
    }

    fun getVguardCatlogDownloads(): Single<List<DownloadData>> {
        return dataRepository.getVguardCatlogDownloads()
    }

    fun getActiveSchemeOffers(): Single<List<DownloadData>> {
        return dataRepository.getActiveSchemeOffers()
    }
}