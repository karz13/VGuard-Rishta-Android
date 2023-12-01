package com.tfl.vguardrishta.ui.components.vguard.updateKyc

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_update_kyc.*
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import java.util.*
import javax.inject.Inject

class UpdateKycActivity : BaseActivity<UpdateKycContract.View, UpdateKycContract.Presenter>(),
    UpdateKycContract.View,
    View.OnClickListener, ActivityFinishListener {

    private var userSelectId: Boolean = false
    private var kycDetails: KycDetails = KycDetails()

    private var tempAadharOrVoterOrDLFront: String? = ""
    private var tempAadharOrVoterOrDlBack: String? = ""
    private var tempAadharOrVoterOrDlNo: String? = ""
    private var tempKycId: Int? = 0

    @Inject
    lateinit var updateKycPresenter: UpdateKycPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): UpdateKycContract.Presenter {
        return updateKycPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_kyc
    }

    override fun initUI() {

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.update_kyc), "")
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        hideKeyBoard()
        CacheUtils.setFileUploader(FileUploader())
        btnSubmit.alpha = 0.4f
        btnSubmit.isEnabled = false
        btnSubmit.isClickable = false
        updateKycPresenter.getKycDetails()
        setOnClicks()

       // setKycTypesSpinnerListener()

        if (CacheUtils.getRishtaUser().roleId == Constants.RET_USER_TYPE) {
            llRetGstDet.visibility = View.VISIBLE
            //llSPKycType.visibility = View.GONE
            cvSubmit.visibility = View.GONE
            llPanNoManually.visibility = View.GONE
            llUpdatePanFront_1.visibility = View.GONE
            disableView(true)

        }
    }

 /*   private fun setKycTypesSpinnerListener() {

        spIdProofs.setOnTouchListener { view, motionEvent ->
            userSelectId = true
            return@setOnTouchListener false
        }

        spIdProofs.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!userSelectId) {
                    return
                }
                val kycIdType = parent?.selectedItem as KycIdTypes

                if (tempKycId != null && tempKycId == kycIdType.kycId) {
                    setUploadedData()
                } else {
                    setEmptyData()
                }
            }
        }
    }
*/

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


        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }

        llUpdateSefie.setOnClickListener {
            selectOrCaptureImage(FileUtils.selfie)
        }

        llUpdateIdFront.setOnClickListener {
            selectOrCaptureImage(FileUtils.idCardPhotoFront)
        }

        llUpdateIdBack.setOnClickListener {
            selectOrCaptureImage(FileUtils.idCardPhotoBack)
        }

        llUpdatePanFront.setOnClickListener {
            selectOrCaptureImage(FileUtils.panCardFront)
        }

        llUpdatePanBack.setOnClickListener {
            selectOrCaptureImage(FileUtils.panCardBack)
        }



        ivSelfie.setOnClickListener {
            val selfieUrl = AppUtils.getSelfieUrl() + kycDetails.selfie
            val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
            if (selfie != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivSelfie.setImageURI(Uri.parse(selfie.toString()));
                } else {
                    ivSelfie.setImageURI(Uri.fromFile(selfie));
                }
                FileUtils.zoomFileImage(this, selfie)
            } else
                FileUtils.zoomImage(this, selfieUrl!!)
        }


        ivAadharVoterDlFront.setOnClickListener {
            val aadharFront =AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDLFront
            val idFrontFile = CacheUtils.getFileUploader().getIdProofFileFront()
            if (idFrontFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivAadharVoterDlFront.setImageURI(Uri.parse(idFrontFile.toString()));
                } else {
                    ivAadharVoterDlFront.setImageURI(Uri.fromFile(idFrontFile));
                }
                FileUtils.zoomFileImage(this, idFrontFile)
            } else
                FileUtils.zoomImage(this, aadharFront!!)
        }

        ivAadharVoterDlBack.setOnClickListener {
            val aadharBack = AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDlBack
            val idFrontBack = CacheUtils.getFileUploader().getIdProofBackFile()
            if (idFrontBack != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivAadharVoterDlBack.setImageURI(Uri.parse(idFrontBack.toString()));
                } else {
                    ivAadharVoterDlBack.setImageURI(Uri.fromFile(idFrontBack));
                }
                FileUtils.zoomFileImage(this, idFrontBack)
            } else
                FileUtils.zoomImage(this, aadharBack!!)
        }

        ivPanCardFront.setOnClickListener {
            val panCardFront = ApiService.panCardUrl + kycDetails.panCardFront
            val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
            if (pandCardFront != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivPanCardFront.setImageURI(Uri.parse(pandCardFront.toString()));
                } else {
                    ivPanCardFront.setImageURI(Uri.fromFile(pandCardFront));
                }
                FileUtils.zoomFileImage(this, pandCardFront)
            } else
                FileUtils.zoomImage(this, panCardFront!!)
        }

        ivGst.setOnClickListener {
            val gstFront = ApiService.gstPicUrl + kycDetails.gstFront
            val gstFrontFile = CacheUtils.getFileUploader().getGstFile()
            if (gstFrontFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivPanCardFront.setImageURI(Uri.parse(gstFrontFile.toString()));
                } else {
                    ivPanCardFront.setImageURI(Uri.fromFile(gstFrontFile));
                }
                FileUtils.zoomFileImage(this, gstFrontFile)
            } else
                FileUtils.zoomImage(this, gstFront!!)
        }

        btnSubmit.setOnClickListener {
            validateInfo()
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

    private fun validateInfo() {
      //  val kycIdType = spIdProofs.selectedItem as KycIdTypes
        val aadharOrVoterManual = etAadharVoterDlManual.text.toString()
        val panCardManual = etPanCardManual.text.toString()
        val idType = 1

        kycDetails.kycId = idType
        kycDetails.aadharOrVoterOrDlNo = aadharOrVoterManual
        kycDetails.panCardNo = panCardManual
        updateKycPresenter.uploadKycDetails(kycDetails)
    }


    override fun showKycDetails(it: KycDetails) {

        this.kycDetails = it

        tempAadharOrVoterOrDLFront = it.aadharOrVoterOrDLFront
        tempAadharOrVoterOrDlBack = it.aadharOrVoterOrDlBack
        tempAadharOrVoterOrDlNo = it.aadharOrVoterOrDlNo
        tempKycId = it.kycId
        if (it.kycFlag == "1") {
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
        etGstNo.setText(it.gstNo)
        val selfieUrl = AppUtils.getSelfieUrl() + kycDetails.selfie
        val aadharFront = AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDLFront
        val aadharBack = AppUtils.getIdCardUrl() + kycDetails.aadharOrVoterOrDlBack
        val panCardFront = ApiService.panCardUrl + kycDetails.panCardFront
        val gstFrontUrl = ApiService.gstPicUrl + kycDetails.gstFront


        Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.no_image).into(ivSelfie)

        Glide.with(this).load(aadharFront)
            .placeholder(R.drawable.no_image).into(ivAadharVoterDlFront)
        Glide.with(this).load(aadharBack)
            .placeholder(R.drawable.no_image).into(ivAadharVoterDlBack)
        Glide.with(this).load(panCardFront)
            .placeholder(R.drawable.no_image).into(ivPanCardFront)
        Glide.with(this).load(gstFrontUrl)
            .placeholder(R.drawable.no_image).into(ivGst)

        withDelay(200) {
            updateKycPresenter.getKycIdTypes()
        }
    }

    private fun setUploadedData() {
        kycDetails.aadharOrVoterOrDLFront = tempAadharOrVoterOrDLFront
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
        }
    }

    private fun setEmptyData() {
        ivAadharVoterDlFront.setImageResource(R.drawable.no_image)
        ivAadharVoterDlBack.setImageResource(R.drawable.no_image)
        etAadharVoterDlManual.setText("")
        kycDetails.aadharOrVoterOrDLFront = null
        kycDetails.aadharOrVoterOrDlBack = null
        kycDetails.aadharOrVoterOrDlNo = null
    }

    private fun disableView(b: Boolean) {
        if (b) {
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

        }
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
        if (requestCode == FileUtils.selfie) {
            AppUtils.checkNullAndSetText(tvSelfie, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivSelfie)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountSelfie.text = "1"
        } else if (requestCode == FileUtils.idCardPhotoFront) {
            AppUtils.checkNullAndSetText(tvAadharVoterDlFront, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivAadharVoterDlFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountAadharFront.text = "1"

        } else if (requestCode == FileUtils.idCardPhotoBack) {
            AppUtils.checkNullAndSetText(tvAadharVoterDlBack, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivAadharVoterDlBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountAadharBack.text = "1"

        } else if (requestCode == FileUtils.panCardBack) {
            AppUtils.checkNullAndSetText(tvPanCardBack, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivPanCardBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountPanCardBack.text = "1"
        } else if (requestCode == FileUtils.panCardFront) {
            AppUtils.checkNullAndSetText(tvPanCardFront, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivPanCardFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountPanFront.text = "1"
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
        toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }

        }
    }
}