package com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer
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
import com.tfl.vguardrishta.models.KycRetailerDetails
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

class UpdateKycReatilerActivity : BaseActivity<UpdateKycRetailerContract.View, UpdateKycRetailerContract.Presenter>(),
    UpdateKycRetailerContract.View,
    View.OnClickListener, ActivityFinishListener {

    private var userSelectId: Boolean = false
    private var kycDetails: KycRetailerDetails = KycRetailerDetails()
    private var retailerD: KycDetails = KycDetails()
    @Inject
    lateinit var updateKycRetailerPresenter: UpdateKycRetailerPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): UpdateKycRetailerContract.Presenter {
        return updateKycRetailerPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_kyc_retailer
    }

    override fun initUI() {

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.update_pan), "")
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        hideKeyBoard()
        CacheUtils.setFileUploader(FileUploader())
       // btnSubmit.alpha = 0.4f
       // btnSubmit.isEnabled = false
       // btnSubmit.isClickable = false
        setOnClicks()

        // setKycTypesSpinnerListener()

        if (CacheUtils.getRishtaUser().roleId == Constants.RET_USER_TYPE) {


        }
        updateKycRetailerPresenter.getKycDetails()
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



        llUpdatePanFront.setOnClickListener {
            selectOrCaptureImage(FileUtils.panCardFront)
        }


        ivPanCardFront.setOnClickListener {
            val panCardFront = ApiService.panCardUrlRet + retailerD.panCardFront
            val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
            if (pandCardFront != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivPanCardFront.setImageURI(Uri.parse(pandCardFront.toString()));
                } else {
                    ivPanCardFront.setImageURI(Uri.fromFile(pandCardFront));
                }
                FileUtils.zoomFileImage(this, pandCardFront)
            } else{
                FileUtils.zoomImage(this, panCardFront!!)
            }
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
        val panCardManual = etPanCardManual.text.toString()
        val rishtaUser = CacheUtils.getRishtaUser()
        kycDetails.userId = rishtaUser.userId
        kycDetails.source = "App"
        kycDetails.panCardNo = panCardManual
        updateKycRetailerPresenter.uploadKycDetails(kycDetails)
    }


    override fun showKycDetails(it: KycDetails) {
        runOnUiThread {
            retailerD =it
            val panCardFront = ApiService.panCardUrlRet + it.panCardFront
            Glide.with(this).load(panCardFront)
                .placeholder(R.drawable.no_image).into(ivPanCardFront)
            etPanCardManual.setText(it.panCardNo)
        }

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
       /* if (requestCode == FileUtils.selfie) {
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
        } else*/
        if (requestCode == FileUtils.panCardFront) {
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