package com.tfl.vguardrishta.ui.components.vguard.updateBank

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_update_bank.*
import kotlinx.android.synthetic.main.activity_update_kyc.customToolbar
import kotlinx.android.synthetic.main.activity_update_retailer_profile.*
import kotlinx.android.synthetic.main.activity_update_rishta_user_profile.*
import kotlinx.android.synthetic.main.activity_update_rishta_user_profile.ivChequePhoto
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import java.lang.NullPointerException
import javax.inject.Inject

class UpdateBankActivity : BaseActivity<UpdateBankContract.View, UpdateBankContract.Presenter>(),
    UpdateBankContract.View,
    View.OnClickListener, ActivityFinishListener {

    private var userSelectId: Boolean = false
    private var kycDetails: KycDetails = KycDetails()

    private var tempAadharOrVoterOrDLFront: String? = ""
    private var tempAadharOrVoterOrDlBack: String? = ""
    private var tempAadharOrVoterOrDlNo: String? = ""
    private var tempKycId: Int? = 0

    @Inject
    lateinit var updatebankPresenter: UpdateBankPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): UpdateBankContract.Presenter {
        return updatebankPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_bank
    }

    override fun initUI() {

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.update_bank), "")
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        hideKeyBoard()
        CacheUtils.setFileUploader(FileUploader())
        //updateKycPresenter.getKycDetails()
        setOnClicks()
        populateBankDetails()
    }

    private fun populateBankDetails() {
        val rishtaUser = CacheUtils.getRishtaUser()
        if (rishtaUser.roleId == Constants.RET_USER_TYPE) {


        }

        if (rishtaUser.updateAccount == "0") {
            btnSubmit2.alpha = 0.4f
            btnSubmit2.isEnabled = false
            btnSubmit2.isClickable = false
        } else if (rishtaUser.updateAccount == "1") {
            btnSubmit2.alpha = 1.0f
            btnSubmit2.isEnabled = true
            btnSubmit2.isClickable = true
        }
        etAccountNumber2.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName2.setText(rishtaUser.bankDetail?.bankAccHolderName)
        updatebankPresenter.getBanks()
        etIfscCode2.setText(rishtaUser.bankDetail?.bankIfsc)
        val position2 =
            AppUtils.getPosition(this, R.array.account_type, rishtaUser.bankDetail?.bankAccType);
        spAccountType2.setSelection(position2)
        val checkPhoto = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto
        Glide.with(this).load(checkPhoto)
            .placeholder(R.drawable.no_image).into(ivChequePhoto2)
        ivChequePhoto2.setOnClickListener {
            FileUtils.zoomImage(this!!, checkPhoto!!)
        }
        AppUtils.getPermission(this)

        if(CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE){
            flspAccountType2.visibility = View.GONE
            textAccountType.visibility =View.GONE
        }
    }

    override fun showBanks(arrayList: java.util.ArrayList<BankDetail>) {
        spBanks2?.adapter =
            CustomSpinner(
                this!!,
                R.layout.custom_simple_spinner_item2,
                arrayList.toTypedArray(),
                Constants.BANKS
            )
        val rishtaUser = CacheUtils.getRishtaUser()

        val bd = rishtaUser.bankDetail

        if (bd?.bankId != null) {
            val stateMaster = arrayList.find { it.bankId == bd.bankId!! }
            val index = arrayList.indexOf(stateMaster)
            spBanks2.setSelection(index)
        }

    }

    private fun choosePhotoFromGallery(IMAGE_REQUEST: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_REQUEST)
    }

    private fun selectOrCaptureImage(IMAGE_REQUEST: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.select_action))
        val items = arrayOf(
            getString(R.string.select_from_gallery),
            getString(R.string.capture_from_camera)
        )
        dialog.setItems(
            items
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery(IMAGE_REQUEST)
                1 -> takePhotoFromCamera(IMAGE_REQUEST)
            }
        }
        dialog.show()
    }

    private fun setOnClicks() {
        /*llCheckPhoto.setOnClickListener {

        }*/
        btnSubmit2.setOnClickListener {
            getInputs()
        }
        llCheckPhoto2.setOnClickListener {
            selectOrCaptureImage(FileUtils.chequeBookPhoto)
        }
    }


    override fun setKycTypesSpinner(arrayList: ArrayList<KycIdTypes>) {
        /*  spIdProofs?.adapter =
              CustomSpinner(
                  this!!,
                  android.R.layout.simple_list_item_1,
                  arrayList.toTypedArray(),
                  Constants.ID_TYPES
              )

          if (kycDetails.kycId != null && kycDetails.kycId != -1) {
              val kycIdType = arrayList.find { it.kycId == kycDetails.kycId }
              val index = arrayList.indexOf(kycIdType)
              spIdProofs.setSelection(index)
          }*/
    }

    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)

    override fun showKycDetails(it: KycDetails) {

        this.kycDetails = it

        tempAadharOrVoterOrDLFront = it.aadharOrVoterOrDLFront
        tempAadharOrVoterOrDlBack = it.aadharOrVoterOrDlBack
        tempAadharOrVoterOrDlNo = it.aadharOrVoterOrDlNo
        tempKycId = it.kycId
        /*if (it.kycFlag == "1") {
            btnSubmit.alpha = 1.0f
            btnSubmit.isEnabled = true
            btnSubmit.isClickable = true
            disableView(false)

        } else {
            btnSubmit.alpha = 0.4f
            btnSubmit.isEnabled = false
            btnSubmit.isClickable = false
            disableView(true)
        }

        etAadharVoterDlManual.setText(it.aadharOrVoterOrDlNo)
        etPanCardManual.setText(it.panCardNo)
        etGstYesNo.setText(it.gstYesNo)
        etGstNo.setText(it.gstNo)*/
        val selfieUrl = AppUtils.getSelfieUrl() + kycDetails.selfie
        val aadharFront = AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDLFront
        val aadharBack = AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDlBack
        val panCardFront = ApiService.panCardUrl + kycDetails.panCardFront
        val gstFrontUrl = ApiService.gstPicUrl + kycDetails.gstFront


        /*Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.no_image).into(ivSelfie)

        Glide.with(this).load(aadharFront)
            .placeholder(R.drawable.no_image).into(ivAadharVoterDlFront)
        Glide.with(this).load(aadharBack)
            .placeholder(R.drawable.no_image).into(ivAadharVoterDlBack)
        Glide.with(this).load(panCardFront)
            .placeholder(R.drawable.no_image).into(ivPanCardFront)
        Glide.with(this).load(gstFrontUrl)
            .placeholder(R.drawable.no_image).into(ivGst)*/

        withDelay(200) {
            updatebankPresenter.getKycIdTypes()
        }
    }

    private fun setUploadedData() {
        /* kycDetails.aadharOrVoterOrDLFront = tempAadharOrVoterOrDLFront
         kycDetails.aadharOrVoterOrDlBack = tempAadharOrVoterOrDlBack
         kycDetails.aadharOrVoterOrDlNo = tempAadharOrVoterOrDlNo
         val aadharFront =AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDLFront
         val aadharBack =AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDlBack
         Glide.with(this).load(aadharFront)
             .placeholder(R.drawable.no_image).into(ivAadharVoterDlFront)
         Glide.with(this).load(aadharBack)
             .placeholder(R.drawable.no_image).into(ivAadharVoterDlBack)
         if (kycDetails.aadharOrVoterOrDlNo != null) {
             etAadharVoterDlManual.setText(kycDetails.aadharOrVoterOrDlNo)
         }*/
    }

    private fun setEmptyData() {
        /*ivAadharVoterDlFront.setImageResource(R.drawable.no_image)
        ivAadharVoterDlBack.setImageResource(R.drawable.no_image)
        etAadharVoterDlManual.setText("")
        kycDetails.aadharOrVoterOrDLFront = null
        kycDetails.aadharOrVoterOrDlBack = null
        kycDetails.aadharOrVoterOrDlNo = null*/
    }

    private fun disableView(b: Boolean) {
        /*  if (b) {
              llUpdateSefie.isClickable = false
              llUpdateIdFront.isClickable = false
              llUpdateIdBack.isClickable = false
              etAadharVoterDlManual.isEnabled = false
              llUpdatePanFront.isClickable = false
              etPanCardManual.isEnabled = false
              //   spIdProofs.isEnabled = false
              etGstYesNo.isEnabled = false
              etGstNo.isEnabled = false
          } else {

          }*/
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun finishView() {
        this.finish()
    }

    private fun takePhotoFromCamera(IMAGE_REQUEST: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionGranted =
                PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)
            if (permissionGranted) {
//                if (Build.VERSION.SDK_INT >= 29) {
//                    Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE).also { takePictureIntent ->
//                        takePictureIntent.resolveActivity(this!!.packageManager)?.also {
//                            if (IMAGE_REQUEST == FileUtils.selfie) {
//                                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
//                            }
//                            startActivityForResult(takePictureIntent, IMAGE_REQUEST)
//                        }
//                    }
//
//                } else {
//                    val startCustomCameraIntent = Intent(this!!, CameraActivity::class.java)
//                    startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
//                }
                val startCustomCameraIntent = Intent(this!!, CameraActivity::class.java)
                startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
            } else {
                ActivityCompat.requestPermissions(
                    this!!,
                    arrayOf(Manifest.permission.CAMERA),
                    PermissionUtils.REQUEST_CAMERA_PERMISSION
                )
            }
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, IMAGE_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        var photoUri: Uri?
        photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            val bitmap = data.extras?.get("data") as Bitmap
            photoUri = AppUtils.getImageUri(this!!, bitmap)
        }
        try {
            compressImage = FileUtils.compressImage(this!!, photoUri.toString())
        } catch (e: Exception) {
            if (bitmap != null)
                compressImage = FileUtils.getFilePath(bitmap, this)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)
        if (requestCode == FileUtils.chequeBookPhoto) {
            AppUtils.checkNullAndSetText(tvChequePhoto2, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivChequePhoto2)
            FileUtils.setToFileUploader(requestCode, compressImage)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
      toast(toast)
    }

    override fun showError() {
        // toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }

        }
    }

    private fun getInputs() {
      val deviceMobileNumber =  AppUtils.getOwenerInfo(applicationContext,this)
      val ipAddress =  AppUtils.getIpAddress(applicationContext)
        val bankDeatils = BankDetail()
        try{
        bankDeatils.bankAccType = if (spAccountType2.selectedItemPosition == 0) {
            ""
        } else {
            spAccountType2.selectedItem as String
        }}
        catch ( e :Exception){}

        if (spBanks2.selectedItem is BankDetail) {
            val bd = spBanks2.selectedItem as BankDetail
            if (bd.bankId != -1) {
                bankDeatils.bankNameAndBranch = bd.bankNameAndBranch
                bankDeatils.bankId = bd.bankId
            }
            if (bd.bankId == -1) {
                bankDeatils.bankNameAndBranch = null
            }
        }
        bankDeatils.bankAccNo = etAccountNumber2.text.toString()
        bankDeatils.bankAccHolderName = etAccountHolderName2.text.toString()
        bankDeatils.bankIfsc = etIfscCode2.text.toString()
        bankDeatils.nomineeMobileNo =deviceMobileNumber
        bankDeatils.nomineeAdd =ipAddress
        val rishtaUser = CacheUtils.getRishtaUser()
        bankDeatils.checkPhoto = rishtaUser.bankDetail?.checkPhoto
        updatebankPresenter.validate(bankDeatils)

    }
}