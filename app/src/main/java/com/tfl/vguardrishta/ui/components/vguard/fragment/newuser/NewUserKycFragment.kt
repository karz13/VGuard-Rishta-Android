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
import android.view.WindowManager
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_new_user_kyc_details.*
import java.io.File
import java.util.*
import javax.inject.Inject

class NewUserKycFragment :
    BaseFragment<NewUserRegContract.View, NewUserRegContract.Presenter>(), NewUserRegContract.View,
    View.OnClickListener {
    @Inject
    lateinit var newUserRegPresenter: NewUserRegPresenter
    private lateinit var searchableBaseAdapter: SearchableBaseAdapter
    private var currPinCodeId: String? = null
    private lateinit var progress: Progress

    override fun initPresenter(): NewUserRegContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_new_user_kyc_details
    }


    override fun hideKeyBoard() {
        activity?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        btnNext.setOnClickListener(this)
        setInputs()
        searchableBaseAdapter = SearchableBaseAdapter()

        etCurrentPinCode.setAdapter(searchableBaseAdapter)
        newUserRegPresenter.getKycIdTypes()

        newUserRegPresenter.getProfessions()

        hideKeyBoard()

        spIsCurrentAddSame.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    if (position == 1) {
                        llCurrentAdd.visibility = View.VISIBLE
                        setCurrAddressToDefault()

                    } else {
                        llCurrentAdd.visibility = View.VISIBLE
//                        newUserRegPresenter.getStates()
                        setCurrentAddToPermAdd()
                    }
                } else {
                    llCurrentAdd.visibility = View.GONE
                    setCurrAddressToDefault()
                }
            }
        }

        spAlreadyEnrolledLoyalty.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    if (position == 1) {
                        llOtherSchemeEnrolledDetails.visibility = View.VISIBLE
                    } else {
                        resetOtherSchemeDetail()
                    }
                } else {
                    resetOtherSchemeDetail()
                }
            }
        }

        spProfession.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val profession = spProfession.selectedItem as Profession
                    newUserRegPresenter.getSubProfessions(profession.professionId.toString())
                } else {
                    flSubProfession.visibility = View.GONE
                    tvSubProfession.visibility = View.GONE
                }
            }
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

        addMore2.setOnClickListener(this)
        addMore3.setOnClickListener(this)
        addMore4.setOnClickListener(this)
        addMore5.setOnClickListener(this)

        setStatesSpinner(CacheUtils.getFirstState(context!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(context!!))
        setCitySpinner(CacheUtils.getFirstCity(context!!))

        etAnnualBusiness.addTextChangedListener(NumberTextWatcher(etAnnualBusiness))

        etCurrentPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etCurrentPinCode.setText(any.toString())
            etCurrentPinCode.tag = any
            etCurrentPinCode.setSelection(any.toString().length)
            newUserRegPresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()
        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                newUserRegPresenter.getPincodeList(etCurrentPinCode.text.toString())
            }
        }

        etCurrentPinCode.setOnFocusChangeListener { view, focus ->
            if (!focus) {
                if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                    newUserRegPresenter.getPincodeList(etCurrentPinCode.text.toString())
                }
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etCurrentPinCode.showDropDown()
        }
    }

    override fun setDetailsByPinCode(it: PincodeDetails?) {
        if (it != null) {
            val rishtaUser = CacheUtils.getNewRishtaUser()
            rishtaUser.currStateId = it.stateId?.toString()
            rishtaUser.currDistId = it.distId?.toString()
            rishtaUser.currCityId = it.cityId?.toString()
            newUserRegPresenter.getStates()
        }
    }

    override fun setCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            val cityMaster = it[0]
            val newRishtaUser = CacheUtils.getNewRishtaUser()
            newRishtaUser.currStateId = cityMaster.stateId?.toString()
            newRishtaUser.currDistId = cityMaster.districtId?.toString()
            newRishtaUser.currCityId = cityMaster.id?.toString()
        }
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableBaseAdapter.mList = it
        searchableBaseAdapter.tempList = it
        searchableBaseAdapter.notifyDataSetChanged()

        etCurrentPinCode.showDropDown()
//        etPinCode.requestFocus()
    }

    override fun setSubProfessionSpinner(arrayList: ArrayList<Profession>) {
        if (arrayList.size > 1) {
            flSubProfession.visibility = View.VISIBLE
            tvSubProfession.visibility = View.VISIBLE
        } else {
            flSubProfession.visibility = View.GONE
            tvSubProfession.visibility = View.GONE
        }

        spSubProfession?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.PROFESSION
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (rishtaUser.subProfessionId != null && rishtaUser.subProfessionId != -1) {
            val profession = arrayList.find { it.professionId == rishtaUser.subProfessionId }
            val index = arrayList.indexOf(profession)
            spSubProfession.setSelection(index)
        }

    }

    override fun setProfessionSpinner(arrayList: ArrayList<Profession>) {
        spProfession?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.PROFESSION
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (rishtaUser.professionId != null && rishtaUser.professionId != -1) {
            val profession = arrayList.find { it.professionId == rishtaUser.professionId }
            val index = arrayList.indexOf(profession)
            spProfession.setSelection(index)
        }

    }

    private fun setCurrentAddToPermAdd() {
        val rishtaUser = CacheUtils.getNewRishtaUser()
        etCurrentAddress.setText(rishtaUser.permanentAddress)
        etCurrentStreetLocality.setText(rishtaUser.streetAndLocality)
        etCurrentLandmark.setText(rishtaUser.landmark)
        if (rishtaUser.cityId == "-2") {
            tlCurrentCity.visibility = View.VISIBLE
        } else {
            tlCurrentCity.visibility = View.GONE
        }
        etCurrentCity.setText(rishtaUser.otherCity)
        etCurrentPinCode.setText(rishtaUser.pinCode)
        currPinCodeId = rishtaUser.pinCodeId
        etCurrentAddress.isEnabled = false
        etCurrentStreetLocality.isEnabled = false
        etCurrentLandmark.isEnabled = false
        etCurrentCity.isEnabled = false
        etCurrentPinCode.isEnabled = false
        spCurrState.isEnabled = false
        spCurrDist.isEnabled = false
        spCurrCity.isEnabled = false
        newUserRegPresenter.getStateDistCitiesByPincodeId(rishtaUser.pinCodeId!!)
    }

    private fun setCurrAddressToDefault() {
        val rishtaUser = CacheUtils.getNewRishtaUser()
        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)
        etCurrentCity.setText(rishtaUser.otherCurrCity)
        etCurrentPinCode.setText(rishtaUser.currPinCode)
        setStatesSpinner(CacheUtils.getFirstState(context!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(context!!))
        setCitySpinner(CacheUtils.getFirstCity(context!!))
        spCurrCity.setSelection(0)
        spCurrDist.setSelection(0)
        spCurrState.setSelection(0)

        etCurrentAddress.isEnabled = true
        etCurrentStreetLocality.isEnabled = true
        etCurrentLandmark.isEnabled = true
        etCurrentCity.isEnabled = true
        spCurrState.isEnabled = true
        spCurrDist.isEnabled = true
        spCurrCity.isEnabled = true
        etCurrentPinCode.isEnabled = true

        if (!rishtaUser.currPinCodeId.isNullOrEmpty()) {
            newUserRegPresenter.getStateDistCitiesByPincodeId(rishtaUser.currPinCodeId!!)
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

    private fun resetOtherSchemeDetail() {
        llOtherSchemeEnrolledDetails.visibility = View.GONE
        llOtherSchemeEnrolledDetails2.visibility = View.GONE
        llOtherSchemeEnrolledDetails3.visibility = View.GONE
        llOtherSchemeEnrolledDetails4.visibility = View.GONE
        llOtherSchemeEnrolledDetails5.visibility = View.GONE

        etOtherSchemeBrandName.setText("")
        etWhatLikedAbtOtherScheme.setText("")

        etOtherSchemeBrandName2.setText("")
        etWhatLikedAbtOtherScheme2.setText("")

        etOtherSchemeBrandName3.setText("")
        etWhatLikedAbtOtherScheme3.setText("")

        etOtherSchemeBrandName4.setText("")
        etWhatLikedAbtOtherScheme4.setText("")

        etOtherSchemeBrandName5.setText("")
        etWhatLikedAbtOtherScheme5.setText("")
    }

    override fun setKycTypesSpinner(arrayList: ArrayList<KycIdTypes>) {
      /*  spIdProofs?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.ID_TYPES
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (rishtaUser.kycDetails.kycId != null) {
            val kycTypes = arrayList.find { it.kycId == rishtaUser.kycDetails.kycId!!.toInt() }
            val index = arrayList.indexOf(kycTypes)
            spIdProofs.setSelection(index)
        }*/
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
        var photoUri: Uri?
        photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            val bitmap = data.extras?.get("data") as Bitmap
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
        if (requestCode == FileUtils.selfie) {
            AppUtils.checkNullAndSetText(tvSelfie, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivSelfie)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountSelfie.text = "1"
        } else if (requestCode == FileUtils.idCardPhotoFront) {
            AppUtils.checkNullAndSetText(tvAadharVoterDlFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivAadharVoterDlFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountAadharFront.text = "1"

        } else if (requestCode == FileUtils.idCardPhotoBack) {
            AppUtils.checkNullAndSetText(tvAadharVoterDlBack, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivAadharVoterDlBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountAadharBack.text = "1"

        } else if (requestCode == FileUtils.panCardBack) {
            AppUtils.checkNullAndSetText(tvPanCardBack, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivPanCardBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountPanCardBack.text = "1"
        } else if (requestCode == FileUtils.panCardFront) {
            AppUtils.checkNullAndSetText(tvPanCardFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivPanCardFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountPanFront.text = "1"
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun setInputs() {
        val rishtaUser = CacheUtils.getNewRishtaUser()
        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)
        etCurrentCity.setText(rishtaUser.otherCurrCity)
        etCurrentPinCode.setText(rishtaUser.currPinCode)

        if (rishtaUser.currPinCodeId != null) {
            currPinCodeId = rishtaUser.currPinCodeId
            newUserRegPresenter.getStateDistCitiesByPincodeId(rishtaUser.currPinCodeId!!)
        }

        etAadharVoterDlManual.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)
        etPanCardManual.setText(rishtaUser.kycDetails.panCardNo)
        if (rishtaUser.maritalStatusId != null) {
            spMaritalStatus.setSelection(rishtaUser.maritalStatusId!!)
        }
        etOtherSchemeBrandName.setText(rishtaUser.otherSchemeBrand)
        etWhatLikedAbtOtherScheme.setText(rishtaUser.abtOtherSchemeLiked)

        etOtherSchemeBrandName2.setText(rishtaUser.otherSchemeBrand2)
        etWhatLikedAbtOtherScheme2.setText(rishtaUser.abtOtherSchemeLiked2)

        etOtherSchemeBrandName3.setText(rishtaUser.otherSchemeBrand3)
        etWhatLikedAbtOtherScheme3.setText(rishtaUser.abtOtherSchemeLiked3)

        etOtherSchemeBrandName4.setText(rishtaUser.otherSchemeBrand4)
        etWhatLikedAbtOtherScheme4.setText(rishtaUser.abtOtherSchemeLiked4)

        etOtherSchemeBrandName5.setText(rishtaUser.otherSchemeBrand5)
        etWhatLikedAbtOtherScheme5.setText(rishtaUser.abtOtherSchemeLiked5)

        etAnnualBusiness.setText(rishtaUser.annualBusinessPotential)

        if (rishtaUser.addDiff == 1) {
            spIsCurrentAddSame.setSelection(1)
            llCurrentAdd.visibility = View.VISIBLE
        } else if (rishtaUser.addDiff == 0) {
            spIsCurrentAddSame.setSelection(2)
            llCurrentAdd.visibility = View.VISIBLE
        }

        if (rishtaUser.enrolledOtherScheme == 1) {
            spAlreadyEnrolledLoyalty.setSelection(rishtaUser.enrolledOtherScheme!!)
        }

        val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
        if (selfie != null) {
//            tvSelfie.setText(idProofFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivSelfie.setImageURI(Uri.parse(selfie.toString()));
            } else {
                ivSelfie.setImageURI(Uri.fromFile(selfie));
            }
            ivSelfie.setOnClickListener { FileUtils.zoomFileImage(context!!, selfie) }
        }

        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
        if (idProofFront != null) {
//            tvIdFront.setText(idProofFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivAadharVoterDlFront.setImageURI(Uri.parse(idProofFront.toString()));
            } else {
                ivAadharVoterDlFront.setImageURI(Uri.fromFile(idProofFront));
            }
            ivAadharVoterDlFront.setOnClickListener {
                FileUtils.zoomFileImage(
                    context!!,
                    idProofFront
                )
            }
        }

        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
        if (idProofBack != null) {
//            tvIdBack.setText(idProofBack?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivAadharVoterDlBack.setImageURI(Uri.parse(idProofBack.toString()));
            } else {
                ivAadharVoterDlBack.setImageURI(Uri.fromFile(idProofBack));
            }
            ivAadharVoterDlBack.setOnClickListener {
                FileUtils.zoomFileImage(
                    context!!,
                    idProofBack
                )
            }
        }


        val panCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        if (panCardFront != null) {
//            tvPanCardFront.setText(panCardFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivPanCardFront.setImageURI(Uri.parse(panCardFront.toString()));
            } else {
                ivPanCardFront.setImageURI(Uri.fromFile(panCardFront));
            }
            ivPanCardFront.setOnClickListener { FileUtils.zoomFileImage(context!!, panCardFront) }
        }


    }

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spCurrState?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (rishtaUser.currStateId != null && rishtaUser.currStateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrState.setSelection(index)
        }

        val selectedItemPosition = spIsCurrentAddSame.selectedItemPosition
        if (selectedItemPosition == 2) {
            if (rishtaUser.stateId != null && rishtaUser.stateId != "-1") {
                val stateMaster = arrayList.find { it.id == rishtaUser.stateId!!.toLong() }
                val index = arrayList.indexOf(stateMaster)
                spCurrState.setSelection(index)
            }
        }

        spCurrState.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val stateMaster = parent?.selectedItem as StateMaster
//
//                    newUserRegPresenter.getDistrict(stateMaster.id)
//                }
            }
        }
    }

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spCurrDist?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (rishtaUser.currDistId != null && rishtaUser.currDistId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currDistId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrDist.setSelection(index)
        }

        val selectedItemPosition = spIsCurrentAddSame.selectedItemPosition

        if (selectedItemPosition == 2) {
            if (rishtaUser.distId != null && rishtaUser.distId != "-1") {
                val stateMaster = arrayList.find { it.id == rishtaUser.distId!!.toLong() }
                val index = arrayList.indexOf(stateMaster)
                spCurrDist.setSelection(index)
            }
        }

        spCurrDist.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val distmaster = parent?.selectedItem as DistrictMaster
//                    newUserRegPresenter.getCities(distmaster.id)
//                }
            }
        }
    }

    override fun setCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCurrCity?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (rishtaUser.currCityId != null && rishtaUser.currCityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currCityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrCity.setSelection(index)
        }

        val selectedItemPosition = spIsCurrentAddSame.selectedItemPosition
        if (selectedItemPosition == 2) {
            if (rishtaUser.cityId != null && rishtaUser.cityId != "-1") {
                val stateMaster = arrayList.find { it.id == rishtaUser.cityId!!.toLong() }
                val index = arrayList.indexOf(stateMaster)
                spCurrCity.setSelection(index)
            }
        }


        spCurrCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val cityMaster = parent?.selectedItem as CityMaster
                }


                if (position != 0 && parent?.selectedItem is CityMaster) {
                    val cityMaster = parent?.selectedItem as CityMaster
                    if (cityMaster.id == -2L) {
                        val newRishtaUser = CacheUtils.getNewRishtaUser()


                        tlCurrentCity.visibility = View.VISIBLE
                        etCurrentCity.setText(newRishtaUser.otherCurrCity)

                        if (spIsCurrentAddSame.selectedItemPosition != 0) {
                            if (spIsCurrentAddSame.selectedItemPosition == 2) {
                                etCurrentCity.setText(newRishtaUser.otherCity)
                            }
                        }
                    }
                    if (cityMaster.id != -2L) {
                        tlCurrentCity.visibility = View.GONE
                    }
                }
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                getInputs()
            }
            R.id.addMore2 -> {
                llOtherSchemeEnrolledDetails2.visibility = View.VISIBLE
            }
            R.id.addMore3 -> {
                llOtherSchemeEnrolledDetails3.visibility = View.VISIBLE
            }
            R.id.addMore4 -> {
                llOtherSchemeEnrolledDetails4.visibility = View.VISIBLE
            }

            R.id.addMore5 -> {
                llOtherSchemeEnrolledDetails5.visibility = View.VISIBLE
            }

        }
    }

    private fun getInputs() {
        val rishtaUser = CacheUtils.getNewRishtaUser()
        val currAddress = etCurrentAddress.text.toString()
        val streetLocality = etCurrentStreetLocality.text.toString()
        val landmark = etCurrentLandmark.text.toString()
        val city = etCurrentCity.text.toString()
        val pinCode = etCurrentPinCode.text.toString()
        val idProofNo = etAadharVoterDlManual.text.toString()
        val panCardNo = etPanCardManual.text.toString()
     //   val kycIdType = spIdProofs.selectedItem as KycIdTypes
        rishtaUser.maritalStatusId = if (spMaritalStatus.selectedItemPosition == 0) {
            null
        } else {
            spMaritalStatus.selectedItemPosition
        }

        if (rishtaUser.maritalStatusId != null) {
            rishtaUser.maritalStatus = spMaritalStatus.selectedItem as String
        }

        val otherSchemeBrand = etOtherSchemeBrandName.text.toString()
        val abtOtherSchemeLiked = etWhatLikedAbtOtherScheme.text.toString()

        val otherSchemeBrand2 = etOtherSchemeBrandName2.text.toString()
        val abtOtherSchemeLiked2 = etWhatLikedAbtOtherScheme2.text.toString()

        val otherSchemeBrand3 = etOtherSchemeBrandName3.text.toString()
        val abtOtherSchemeLiked3 = etWhatLikedAbtOtherScheme3.text.toString()

        val otherSchemeBrand4 = etOtherSchemeBrandName4.text.toString()
        val abtOtherSchemeLiked4 = etWhatLikedAbtOtherScheme4.text.toString()

        val otherSchemeBrand5 = etOtherSchemeBrandName5.text.toString()
        val abtOtherSchemeLiked5 = etWhatLikedAbtOtherScheme5.text.toString()


        val annualBusinessPotential = etAnnualBusiness.text.toString().replace(",", "")

        var isAddDiff = -1

        if (spIsCurrentAddSame.selectedItemPosition == 1) {
            isAddDiff = 1
        } else if (spIsCurrentAddSame.selectedItemPosition == 2) {
            isAddDiff = 0
        }


        rishtaUser.addDiff = isAddDiff


        if (isAddDiff == 1) {
            rishtaUser.currentAddress = currAddress
            rishtaUser.currStreetAndLocality = streetLocality
            rishtaUser.currLandmark = landmark
            rishtaUser.currCity = city

            if (spCurrCity.selectedItem is CityMaster) {
                val cityMaster = spCurrCity.selectedItem as CityMaster
                rishtaUser.currCityId = cityMaster.id.toString()

                if (cityMaster.id != -2L) {
                    rishtaUser.currCity = cityMaster.cityName.toString()
                    rishtaUser.otherCity = null
                }
                if (cityMaster.id == -2L) {
                    rishtaUser.city = null
                    rishtaUser.otherCurrCity = etCurrentCity.text.toString()
                }
            }

            if (spCurrDist.selectedItem is DistrictMaster) {
                val distMaster = spCurrDist.selectedItem as DistrictMaster
                rishtaUser.currDistId = distMaster.id.toString()
                rishtaUser.currDist = distMaster.districtName
            }

            if (spCurrState.selectedItem is StateMaster) {
                val stateMaster = spCurrState.selectedItem as StateMaster
                rishtaUser.currState = stateMaster.stateName
                rishtaUser.currStateId = stateMaster.id.toString()
            }

            rishtaUser.currPinCode = pinCode
            rishtaUser.currPinCodeId = currPinCodeId
        }

        val selectedProfession = spProfession.selectedItem as Profession
        rishtaUser.professionId = selectedProfession.professionId
        rishtaUser.profession = selectedProfession.professionName


        if (spSubProfession.selectedItem is Profession) {
            val selectedSubProfession = spSubProfession.selectedItem as Profession
            if (selectedSubProfession.professionId != -1) {
                rishtaUser.subProfessionId = selectedSubProfession.professionId
                rishtaUser.subProfession = selectedSubProfession.professionName
            }
        }

        rishtaUser.enrolledOtherScheme = if (spAlreadyEnrolledLoyalty.selectedItemPosition == 1) {
            1
        } else {
            0
        }



        rishtaUser.otherSchemeBrand = otherSchemeBrand
        rishtaUser.abtOtherSchemeLiked = abtOtherSchemeLiked


        rishtaUser.otherSchemeBrand2 = otherSchemeBrand2
        rishtaUser.abtOtherSchemeLiked2 = abtOtherSchemeLiked2

        rishtaUser.otherSchemeBrand3 = otherSchemeBrand3
        rishtaUser.abtOtherSchemeLiked3 = abtOtherSchemeLiked3

        rishtaUser.otherSchemeBrand4 = otherSchemeBrand4
        rishtaUser.abtOtherSchemeLiked4 = abtOtherSchemeLiked4

        rishtaUser.otherSchemeBrand5 = otherSchemeBrand5
        rishtaUser.abtOtherSchemeLiked5 = abtOtherSchemeLiked5

        rishtaUser.annualBusinessPotential = annualBusinessPotential
        val kycDetails = KycDetails()
        kycDetails.aadharOrVoterOrDlNo = idProofNo
        kycDetails.panCardNo = panCardNo
        rishtaUser.panNumber = panCardNo
        kycDetails.kycId = 1
        kycDetails.kycIdName ="Aadhar Card"
       /* kycDetails.kycId = kycIdType.kycId
        if (kycIdType.kycId != -1) {
            kycDetails.kycIdName = kycIdType.kycIdName
        }*/
        rishtaUser.kycDetails = kycDetails
        newUserRegPresenter.verifyNewUserKycData(rishtaUser)
    }

    override fun navigateToBankAndNomineeDetails() {
        (activity as NewUserRegistrationActivity).navigateToBankAndNominee()
    }

}