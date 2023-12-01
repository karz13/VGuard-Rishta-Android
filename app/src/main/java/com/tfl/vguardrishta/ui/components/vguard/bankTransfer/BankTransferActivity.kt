package com.tfl.vguardrishta.ui.components.vguard.bankTransfer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.components.vguard.updateBank.UpdateBankActivity
import com.tfl.vguardrishta.ui.components.vguard.updateProfile.UpdateProfileActivity
import com.tfl.vguardrishta.ui.components.vguard.updateRetailerProfile.UpdateRetailerProfileActivity
import com.tfl.vguardrishta.utils.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_bank_transfer.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import javax.inject.Inject

class BankTransferActivity :
    BaseActivity<BankTransferContract.View, BankTransferContract.Presenter>(),
    BankTransferContract.View, View.OnClickListener, ActivityFinishListener {
    private var defaultTracker: Tracker? = null

    @Inject
    lateinit var bankTransferPresenter: BankTransferPresenter

    private lateinit var progress: Progress

    var checquUrl: String? = ""

    override fun initPresenter(): BankTransferContract.Presenter {
        return bankTransferPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_bank_transfer
    }

    override fun initUI() {
        defaultTracker = (application as App).getDefaultTracker()

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.bank_transfer), "")
        progress = Progress(this, R.string.please_wait)
        CacheUtils.setFileUploader(FileUploader())
        ivBack.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
//        llCheckPhoto.setOnClickListener(this)
        hideKeyBoard()


    }

    override fun onResume() {
        super.onResume()
        bankTransferPresenter.getUserBankDetail()
        bankTransferPresenter.getProfile()
        defaultTracker?.setScreenName("Instant Bank Transfer")
        defaultTracker?.setPage("Instant Bank Transfer")
        defaultTracker?.setTitle("Instant Bank Transfer")
        defaultTracker?.send(HitBuilders.ScreenViewBuilder().build())

    }

    override fun setRishtaUser(it: VguardRishtaUser?) {
        Paper.book().write(Constants.KEY_RISHTA_USER, it)
    }

    override fun showBanks(it: List<BankDetail>) {
        withDelay(200) {
            bankTransferPresenter.getUserBankDetail()
        }
        spBanks?.adapter =
            CustomSpinner(
                this,
                R.layout.custom_simple_spinner_item2,
                it.toTypedArray(),
                Constants.BANKS
            )
    }

    override fun showBankDetail(bd: BankDetail) {

        if (bd.bankDataPresent ==1) {
            //update bank data
            showUpdateBankDetails()
        }
        etAccountNumber.setText(bd.bankAccNo)
        etAccountHolderName.setText(bd.bankAccHolderName)
//        val position = AppUtils.getPosition(this, R.array.account_type, bd.bankAccType);
//        spAccountType.setSelection(position)
        etAccountType.setText(bd.bankAccType)
        etIfscCode.setText(bd.bankIfsc)
        checquUrl = AppUtils.getChequeUrl() + bd.checkPhoto;
        if (bd.checkPhoto.isNullOrEmpty()) {
            checquUrl = null
        }
        if (checquUrl != null) {
            Glide.with(this).load(checquUrl)
                .placeholder(R.drawable.no_image).into(ivChequePhoto)
        }

        etBankName.setText(bd.bankNameAndBranch)
        ivChequePhoto.setOnClickListener(this)

//        if (bd.bankNameAndBranch != null) {
//            val banks = CacheUtils.getBanks()
//            val stateMaster = banks.find {
//                it.bankNameAndBranch?.trim().equals(bd.bankNameAndBranch!!.trim(), true)
//            }
//            val index = banks.indexOf(stateMaster)
//            spBanks.setSelection(index)
//        }
        spBanks.isEnabled = false
        spAccountType.isEnabled = false

    }

    private fun showUpdateBankDetails() {

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg_cancel_ok, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val ivOk = dialogView.findViewById(R.id.ivOk) as ImageView

        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = getString(R.string.please_fill_your_bank_details)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialog.setOnCancelListener {
            this.finish()
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
            this.finish()
        }

        ivOk.setOnClickListener {
           if(CacheUtils.getRishtaUser().roleId==Constants.INF_USER_TYPE){
              // launchActivity<UpdateProfileActivity> { }
               launchActivity<UpdateBankActivity> { }
           }else{
               //launchActivity<UpdateRetailerProfileActivity> {  }
               launchActivity<UpdateBankActivity> { }
           }
            dialog.dismiss()
        }

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

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this, message)

    }

    override fun showMsgDialogWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun showError() {
        toast(resources.getString(R.string.something_wrong))
    }

    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnProceed -> {
                getInputs()
            }
            R.id.llCheckPhoto -> {
                selectOrCaptureImage(FileUtils.chequeBookPhoto)
            }
            R.id.ivChequePhoto -> {
                val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
                if (checkBookFile != null) {
//            tvChequePhoto.setText(checkBookFile?.name)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ivChequePhoto.setImageURI(Uri.parse(checkBookFile.toString()));
                    } else {
                        ivChequePhoto.setImageURI(Uri.fromFile(checkBookFile));
                    }
                    FileUtils.zoomFileImage(this, checkBookFile)
                } else if (checquUrl != null) {
                    FileUtils.zoomImage(this, checquUrl!!)
                }
            }
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
                compressImage = FileUtils.getFilePath(bitmap,this)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)
        if (requestCode == FileUtils.chequeBookPhoto) {
            AppUtils.checkNullAndSetText(tvChequePhoto, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivChequePhoto)
            FileUtils.setToFileUploader(requestCode, compressImage)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun getInputs() {

        val amount = etAmount.text.toString()
        val accNo = etAccountNumber.text.toString()
        val accHolderName = etAccountHolderName.text.toString()
        val accType = etAccountType.text.toString()
        val ifscCode = etIfscCode.text.toString()
        val bankName = etBankName.text.toString()
//        val selectedBankDetail = spBanks.selectedItem as BankDetail

        bankTransferPresenter.validateDetail(
            amount,
            accNo,
            accHolderName,
            accType,
            bankName,
            ifscCode,
            bankName
        )

    }

    override fun finishView() {
        this.finish()
    }
}