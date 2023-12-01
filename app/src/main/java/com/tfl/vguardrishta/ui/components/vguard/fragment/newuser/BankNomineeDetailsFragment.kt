package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

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
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_bank_nominee_details.*
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

class BankNomineeDetailsFragment :
    BaseFragment<NewUserRegContract.View, NewUserRegContract.Presenter>(), NewUserRegContract.View,
    View.OnClickListener {

    @Inject
    lateinit var newUserRegPresenter: NewUserRegPresenter


    private lateinit var progress: Progress

    private var isAgreed = false

    override fun initPresenter(): NewUserRegContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_bank_nominee_details
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        setInputs()
        btnPreview.setOnClickListener(this)
        llCheckPhoto.setOnClickListener(this)
        ibAgree.setOnClickListener(this)
        ibAgree.alpha = 0.4f
        ivCancel.setOnClickListener(this)
        llNomDob.setOnClickListener(this)
        newUserRegPresenter.getBanks();

        tvTermsDescription.setOnTouchListener { view, motionEvent ->
            AppUtils.showTerms(context!!)
            return@setOnTouchListener false
        }

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(context!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(context!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(context!!, tvWhatsappNo.text.toString())
        }

    }

    private fun setInputs() {

        val rishtaUser = CacheUtils.getNewRishtaUser()
        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
        rishtaUser.bankDetail?.bankAccTypePos?.toInt()?.let { spAccountType.setSelection(it) }

//        etBankNameBranch.setText(rishtaUser.bankDetail?.bankNameAndBranch)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)
        etNameOfNominee.setText(rishtaUser.bankDetail?.nomineeName)
        tvNomineeDob.setText(rishtaUser.bankDetail?.nomineeDob)
        etNomineeMobileNo.setText(rishtaUser.bankDetail?.nomineeMobileNo)
        etNomineeEmail.setText(rishtaUser.bankDetail?.nomineeEmail)
        etNomineeAddress.setText(rishtaUser.bankDetail?.nomineeAdd)
        tvNomineeRelation.setText(rishtaUser.bankDetail?.nomineeRelation)

        val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        if (checkBookFile != null) {
//            tvChequePhoto.setText(checkBookFile?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivChequePhoto.setImageURI(Uri.parse(checkBookFile.toString()));
            } else {
                ivChequePhoto.setImageURI(Uri.fromFile(checkBookFile));
            }
            llCheckPhoto.setOnClickListener { FileUtils.zoomFileImage(context!!, checkBookFile) }
        }
    }


    private fun choosePhotoFromGallery(IMAGE_REQUEST: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_REQUEST)
    }

    private fun selectOrCaptureImage(IMAGE_REQUEST: Int) {
        val dialog = AlertDialog.Builder(context)
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
                PermissionUtils.isPermissionGranted(activity!!, Manifest.permission.CAMERA)
            if (permissionGranted) {
                val startCustomCameraIntent = Intent(activity!!, CameraActivity::class.java)
                startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
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
        var photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            bitmap = data.extras?.get("data") as Bitmap
            photoUri = AppUtils.getImageUri(activity!!, bitmap)
        }
        try {
            compressImage = FileUtils.compressImage(activity!!, photoUri.toString())
        } catch (e: Exception) {
            if (bitmap != null)
                compressImage = FileUtils.getFilePath(bitmap,context!!)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)
        when (requestCode) {
            FileUtils.chequeBookPhoto -> {
                FileUtils.setToFileUploader(requestCode, compressImage)
                AppUtils.checkNullAndSetText(tvChequePhoto, file.name, getString(R.string.file))
                Glide.with(activity!!).load(photoUri).into(ivChequePhoto)
            }
        }
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun showBanks(arrayList: ArrayList<BankDetail>) {
        spBanks?.adapter =
            CustomSpinner(
                context!!,
                R.layout.custom_simple_spinner_item2,
                arrayList.toTypedArray(),
                Constants.BANKS
            )
        val newRishtaUser = CacheUtils.getNewRishtaUser()
        if (newRishtaUser.bankDetail?.bankId != null) {
             val stateMaster = arrayList.find {
                it.bankId==newRishtaUser.bankDetail?.bankId!!
            }
            val index = arrayList.indexOf(stateMaster)
            spBanks.setSelection(index)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPreview -> {
                getInputs()
            }

            R.id.llCheckPhoto -> {
                selectOrCaptureImage(FileUtils.chequeBookPhoto)
            }

            R.id.llNomDob->{
                showToast(getString(R.string.click_year_to_change))
                AppUtils.getDate(activity!!, tvNomineeDob, false)
            }

            R.id.ibAgree->{
                if(isAgreed){
                    isAgreed=false
                    ibAgree.alpha=0.4f
                }else{
                    isAgreed=true
                    ibAgree.alpha=1f
                }
            }

            R.id.ivCancel->{
                activity?.finish()
                activity?.launchActivity<LogInActivity> {  }
            }
        }
    }

    private fun getInputs() {

        if(!cbTermsAndConditions.isChecked){
            showToast(getString(R.string.please_agree_toterms))
            return
        }


        val rishtaUser = CacheUtils.getNewRishtaUser()
        var bankDetail = rishtaUser.bankDetail
        if (bankDetail == null) {
            bankDetail = BankDetail()
        }
        val accNo = etAccountNumber.text.toString()
        val accHolderName = etAccountHolderName.text.toString()
        var accType = ""
        if(spAccountType.selectedItemPosition!=0){
            accType=spAccountType.selectedItem as String
        }
        val ifsc = etIfscCode.text.toString()
         val nomineeName = etNameOfNominee.text.toString()
        val nomineeDob = tvNomineeDob.text.toString()
        val nomineeMobile = etNomineeMobileNo.text.toString()
        val nomineeEmail = etNomineeEmail.text.toString()
        val nomineeAdd = etNomineeAddress.text.toString()
        val nomineeRelation = tvNomineeRelation.text.toString()

        bankDetail.bankAccNo = accNo
        bankDetail.bankAccHolderName = accHolderName
        bankDetail.bankAccType = accType
        bankDetail.bankAccTypePos = spAccountType.selectedItemPosition.toString()



        if(spBanks.selectedItem is BankDetail){
            val bd = spBanks.selectedItem as BankDetail
            if(bd.bankId!=-1){
                bankDetail.bankNameAndBranch = bd.bankNameAndBranch
                bankDetail.bankId=bd.bankId
            }
            if(bd.bankId==-1){
                bankDetail.bankNameAndBranch=null
            }
        }

        bankDetail.bankIfsc = ifsc
         bankDetail.nomineeName = nomineeName
        bankDetail.nomineeDob = nomineeDob
        bankDetail.nomineeMobileNo = nomineeMobile
        bankDetail.nomineeEmail = nomineeEmail
        bankDetail.nomineeAdd = nomineeAdd
        bankDetail.nomineeRelation = nomineeRelation
        rishtaUser.bankDetail = bankDetail
        newUserRegPresenter.verifyDetails(rishtaUser)

    }

    override fun showUserDetailsPreview() {
        (activity as NewUserRegistrationActivity).navigateUserDetailsPreview()

    }
}