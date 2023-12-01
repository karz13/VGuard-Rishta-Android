package com.tfl.vguardrishta.models

import okhttp3.MultipartBody
import java.io.File

/**
 * Created by Shanmuka on 2/7/2018.
 */
class FileUploader {
    private var idProofBackFile: File? = null
    private var panCardBackFile: File? = null
    private var pancardFrontFile: File? = null
    private var gstFile: File? = null

    private var idProofFront: MultipartBody.Part? = null
    private var idProofBack: MultipartBody.Part? = null
    private var pandCardFront: MultipartBody.Part? = null
    private var panCardBack: MultipartBody.Part? = null
    private var userPhoto: MultipartBody.Part? = null
    private var shopPhoto: MultipartBody.Part? = null
    private var chequeBookPhoto: MultipartBody.Part? = null
    private var issuePhoto: MultipartBody.Part? = null
    private var uploadOrder: MultipartBody.Part? = null
    private var uploadOrderFile: File? = null
    private var userPhotoFile: File? = null
    private var idProofFileFront: File? = null
    private var shopPhotoFile: File? = null
    private var chequeBookPhotoFile: File? = null
    private var invoicePhotoFile: File? = null
    private var issuePhotoFile: File? = null
    private var errorCouponPhotoFile: File? = null
    private var contestFirstPhotoFile: File? = null
    private var contestSecondPhotoFile: File? = null
    private var billDetailsFile: File? = null
    private var warrantyPhotoFile: File? = null

    fun getUploadOrderFile(): File? = uploadOrderFile
    fun getUserPhotoFile(): File? = userPhotoFile
    fun getIdProofFileFront(): File? = idProofFileFront
    fun getShopPhotoFile(): File? = shopPhotoFile
    fun getChequeBookPhotoFile(): File? = chequeBookPhotoFile
    fun getInvoicePhotoFile(): File? = invoicePhotoFile
    fun getContestFirstPhotoFile(): File? = contestFirstPhotoFile
    fun getContestSecondPhotoFile(): File? = contestSecondPhotoFile
    fun getIdProofBackFile(): File? = idProofBackFile
    fun getIssuePhotoFile(): File? = issuePhotoFile


    fun setIdProofFront(idProof: MultipartBody.Part) {
        this.idProofFront = idProof
    }

    fun setIdProofBack(idProof: MultipartBody.Part) {
        this.idProofBack = idProof
    }

    fun setPanCardBack(idProof: MultipartBody.Part) {
        this.panCardBack = idProof
    }

    fun setPanCardFront(idProof: MultipartBody.Part) {
        this.pandCardFront = idProof
    }

    fun setUserPhoto(userPhoto: MultipartBody.Part) {
        this.userPhoto = userPhoto
    }

    fun setShopPhoto(shopPhoto: MultipartBody.Part) {
        this.shopPhoto = shopPhoto
    }

    fun setIssuePhoto(issuePhoto: MultipartBody.Part) {
        this.issuePhoto = issuePhoto
    }

    fun setChequeBook(chequeBookPhoto: MultipartBody.Part?) {
        this.chequeBookPhoto = chequeBookPhoto
    }


    fun setUploadOrder(uploadOrder: MultipartBody.Part) {
        this.uploadOrder = uploadOrder
    }


    fun setIssuePhotoFile(file: File) {
        this.issuePhotoFile = file
    }

    fun setUploadOrderFile(file: File) {
        this.uploadOrderFile = file
    }

    fun setUserPhotoFile(file: File) {
        this.userPhotoFile = file
    }

    fun setIdProofFile(file: File) {
        this.idProofFileFront = file
    }

    fun setShopPhotoFile(file: File) {
        this.shopPhotoFile = file
    }

    fun setChequeBookPhotoFile(file: File) {
        this.chequeBookPhotoFile = file
    }

    fun setInvoicePhotoFile(file: File) {
        this.invoicePhotoFile = file
    }

    fun setContestFirstPhotoFile(file: File) {
        this.contestFirstPhotoFile = file
    }

    fun setContestSecondPhotoFile(file: File) {
        this.contestSecondPhotoFile = file
    }

    fun setPanCardFrontFile(file: File) {
        this.pancardFrontFile = file
    }

    fun setPanCardBackFile(file: File) {
        this.panCardBackFile = file
    }

    fun setIdProofBackFile(file: File) {
        this.idProofBackFile = file
    }

    fun getPanCardBackFile(): File? = this.panCardBackFile
    fun getPanCardFrontFile(): File? = this.pancardFrontFile

    fun setErrorCouponFile(file: File) {
        this.errorCouponPhotoFile = file
    }

    fun getGstFile(): File? = this.gstFile

    fun getErrorCouponFile(): File? = this.errorCouponPhotoFile


    fun setBillDetailsFile(billDetails: File) {
        this.billDetailsFile = billDetails
    }

    fun getBillDetailsFile(): File? = billDetailsFile

    fun setWarrantyPhotoFile(warrantyFile: File) {
        this.warrantyPhotoFile = warrantyFile
    }

    fun getWarrantyFile(): File? {
        return warrantyPhotoFile
    }

    fun setGSTPhotoFile(file: File) {
        this.gstFile = file
    }
}