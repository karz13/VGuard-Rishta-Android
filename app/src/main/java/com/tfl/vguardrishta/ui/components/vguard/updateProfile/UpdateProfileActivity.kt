package com.tfl.vguardrishta.ui.components.vguard.updateProfile

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
import com.tfl.vguardrishta.extensions.hideSoftKeyBoard
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_update_rishta_user_profile.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import java.util.*
import javax.inject.Inject

class UpdateProfileActivity :
    BaseActivity<UpdateProfileContract.View, UpdateProfileContract.Presenter>(),
    UpdateProfileContract.View,
    View.OnClickListener, ActivityFinishListener {

    private var tempCurrentPincode: String? = ""
    private var currPinCodeId: String = ""
    private var pinCodeDetails: PincodeDetails? = null

    private var tempCurrStateId: String? = ""
    private var tempCurrDistId: String? = ""
    private var tempCurrCityId: String? = ""


    private lateinit var searchableBaseAdapter: SearchableBaseAdapter

    @Inject
    lateinit var updateProfilePresenter: UpdateProfilePresenter

    private lateinit var progress: Progress

    override fun initPresenter(): UpdateProfileContract.Presenter {
        return updateProfilePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_rishta_user_profile
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.edit_profile), "")
        progress = Progress(this, R.string.please_wait)
        val rishtaUser = CacheUtils.getRishtaUser()
        tvUserName.text = rishtaUser.name
        tvRishtaId.text = rishtaUser.userCode
        ivBack.setOnClickListener(this)
        llCheckPhoto.setOnClickListener(this)
        llNomDob.setOnClickListener(this)
        hideKeyBoard()
        val currentFocus = this.currentFocus
        currentFocus?.hideSoftKeyBoard()
        etAnnualBusiness.addTextChangedListener(NumberTextWatcher(etAnnualBusiness))
        setCurrentPincodeListeners()
        CacheUtils.setFileUploader(FileUploader())

        llUpdateSelfie.setOnClickListener {
            selectOrCaptureImage(FileUtils.selfie)
        }

        llDOB.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

        spProfession.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val profession = spProfession.selectedItem as Profession
                    updateProfilePresenter.getSubProfessions(profession.professionId.toString())
                } else {
                    flSubProfession.visibility = View.GONE
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
                        updateProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
                    } else {
                        llCurrentAdd.visibility = View.GONE
                    }
                } else {
                    llCurrentAdd.visibility = View.GONE
                }
            }
        }

        addMore2.setOnClickListener(this)
        addMore3.setOnClickListener(this)
        addMore4.setOnClickListener(this)
        addMore5.setOnClickListener(this)
        setStatesSpinner(CacheUtils.getFirstState(this!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(this!!))
        setCitySpinner(CacheUtils.getFirstCity(this!!))
        setInputs()
//        updateProfilePresenter.getStates()
        updateProfilePresenter.getBanks();
        updateProfilePresenter.getProfessions()
        updateProfilePresenter.getKycIdTypes()

    }


    private fun setCurrentPincodeListeners() {
        searchableBaseAdapter = SearchableBaseAdapter()

        etCurrentPinCode.setAdapter(searchableBaseAdapter)

        etCurrentPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etCurrentPinCode.setText(any.toString())
            etCurrentPinCode.tag = any
            this.pinCodeDetails = any
            etCurrentPinCode.setSelection(any.toString().length)
            updateProfilePresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()

        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                updateProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
            }
        }

//        etCurrentPinCode.setOnFocusChangeListener { view, focus ->
//            if (!focus) {
//                if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
//                    updateProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
//                }
//            }
//        }

        ivCurrentPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etCurrentPinCode.showDropDown()
        }
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableBaseAdapter.mList = it
        searchableBaseAdapter.tempList = it
        searchableBaseAdapter.notifyDataSetChanged()
        etCurrentPinCode.showDropDown()
//        etPinCode.requestFocus()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            updateProfilePresenter.getStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etCurrentPinCode.dismissDropDown()
        }
    }

    override fun setCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            if (it != null && it.isNotEmpty()) {
                val cityMaster = it[0]
                tempCurrStateId = cityMaster.stateId?.toString()
                tempCurrDistId = cityMaster.districtId?.toString()
                tempCurrCityId = cityMaster.id?.toString()
            }
        }
    }

    override fun processKycIdTypes(arrayList: List<KycIdTypes>?) {
        val rishtaUser = CacheUtils.getRishtaUser()
        if (rishtaUser.kycDetails.kycId != null) {
            val kycType = arrayList?.find { it.kycId == rishtaUser.kycDetails.kycId!! }
            etIdProofType.setText(kycType?.kycIdName.toString())
        }
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
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.PROFESSION
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (rishtaUser.subProfessionId != null && rishtaUser.subProfessionId != -1) {
            val profession = arrayList.find { it.professionId == rishtaUser.subProfessionId }
            val index = arrayList.indexOf(profession)
            spSubProfession.setSelection(index)
        }

    }


    override fun setProfessionSpinner(arrayList: ArrayList<Profession>) {
        spProfession?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.PROFESSION
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (rishtaUser.professionId != null && rishtaUser.professionId != -1) {
            val profession = arrayList.find { it.professionId == rishtaUser.professionId }
            val index = arrayList.indexOf(profession)
            spProfession.setSelection(index)
        }
    }


    override fun showBanks(arrayList: ArrayList<BankDetail>) {
        spBanks?.adapter =
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
            spBanks.setSelection(index)
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

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spCurrState?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currStateId != null && rishtaUser.currStateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrState.setSelection(index)
        } else if (!tempCurrStateId.isNullOrEmpty()) {
            val stateMaster = arrayList.find { it.id == tempCurrStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrState.setSelection(index)
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
//                    updateProfilePresenter.getDistrict(stateMaster.id)
//                }
            }
        }
    }

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spCurrDist?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currDistId != null && rishtaUser.currDistId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currDistId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrDist.setSelection(index)
        } else if (!tempCurrDistId.isNullOrEmpty()) {
            val distMaster = arrayList.find { it.id == tempCurrDistId!!.toLong() }
            val index = arrayList.indexOf(distMaster)
            spCurrDist.setSelection(index)
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
//                    updateProfilePresenter.getCities(distmaster.id)
//                }
            }
        }
    }

    override fun setCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCurrCity?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currCityId != null && rishtaUser.currCityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currCityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrCity.setSelection(index)
        } else if (!tempCurrCityId.isNullOrEmpty()) {
            val cityMaster = arrayList.find { it.id == tempCurrCityId!!.toLong() }
            val index = arrayList.indexOf(cityMaster)
            spCurrCity.setSelection(index)
        }

        spCurrCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val cityMaster = parent?.selectedItem as CityMaster
//                }

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

    private fun setInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()

        val selfieUrl = AppUtils.getSelfieUrl() + rishtaUser.kycDetails.selfie
        Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        etPreferredLanguage.setText(rishtaUser.preferredLanguage)
        etReferralCode.setText(rishtaUser.referralCode)
        etNameOfReferee.setText(rishtaUser.nameOfReferee)
        etName.setText(rishtaUser.name)
        val position = AppUtils.getPosition(this, R.array.select_gender, rishtaUser.gender?.trim());
        spGender.setSelection(position)
//        spGender.setText(rishtaUser.gender)
        tvDob.setText(rishtaUser.dob)
        etContactNumber.setText(rishtaUser.contactNo)
        etWhatsAppNumber.setText(rishtaUser.whatsappNo)
        etEmail.setText(rishtaUser.emailId)
        etPermanentAddress.setText(rishtaUser.permanentAddress)
        etStreetLocality.setText(rishtaUser.streetAndLocality)
        etLandmark.setText(rishtaUser.landmark)
        etCity.setText(rishtaUser.city)
        etDistrict.setText(rishtaUser.dist)
        etState.setText(rishtaUser.state)
        etPinCode.setText(rishtaUser.pinCode)

        if (rishtaUser.addDiff != -1) {
            if (rishtaUser.addDiff == 0) {
                spIsCurrentAddSame.setSelection(1)
            } else {
                spIsCurrentAddSame.setSelection(2)
            }
        }


        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)
        etCurrentPinCode.setText(rishtaUser.currPinCode)
        updateProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
        tempCurrentPincode = rishtaUser.currPinCode
//        if (rishtaUser.professionId != null) {
//            spProfession.setSelection(rishtaUser.professionId!!)
//        }

        if (rishtaUser.maritalStatus != null) {
            spMaritalStatus.setSelection(rishtaUser.maritalStatus!!.toInt())
        }

//        if (rishtaUser.isEnrolledOtherScheme == 1) {
//            spAlreadyEnrolledLoyalty.setSelection(1)
//        } else {
//            spAlreadyEnrolledLoyalty.setSelection(2)
//        }

        if (rishtaUser.enrolledOtherScheme == 1) {
            spAlreadyEnrolledLoyalty.setSelection(1)
            llOtherSchemeEnrolledDetails.visibility = View.VISIBLE
            etOtherSchemeBrandName.setText(rishtaUser.otherSchemeBrand)
            etWhatLikedAbtOtherScheme.setText(rishtaUser.abtOtherSchemeLiked)

            if (!rishtaUser.otherSchemeBrand2.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails2.visibility = View.VISIBLE
                etOtherSchemeBrandName2.setText(rishtaUser.otherSchemeBrand2)
                etWhatLikedAbtOtherScheme2.setText(rishtaUser.abtOtherSchemeLiked2)

            }

            if (!rishtaUser.otherSchemeBrand3.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails3.visibility = View.VISIBLE
                etOtherSchemeBrandName3.setText(rishtaUser.otherSchemeBrand3)
                etWhatLikedAbtOtherScheme3.setText(rishtaUser.abtOtherSchemeLiked3)
            }

            if (!rishtaUser.otherSchemeBrand4.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails4.visibility = View.VISIBLE
                etOtherSchemeBrandName4.setText(rishtaUser.otherSchemeBrand4)
                etWhatLikedAbtOtherScheme4.setText(rishtaUser.abtOtherSchemeLiked4)
            }

            if (!rishtaUser.otherSchemeBrand5.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails5.visibility = View.VISIBLE
                etOtherSchemeBrandName5.setText(rishtaUser.otherSchemeBrand5)
                etWhatLikedAbtOtherScheme5.setText(rishtaUser.abtOtherSchemeLiked5)
            }
        } else if (rishtaUser.enrolledOtherScheme == 0) {
            spAlreadyEnrolledLoyalty.setSelection(2)
        }

        etAnnualBusiness.setText(rishtaUser.annualBusinessPotential)
        etIdProofType.setText(rishtaUser.kycDetails.kycIdName)

//        val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
//        if (selfie != null) {
////            tvSelfie.setText(idProofFront?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivSelfie.setImageURI(Uri.parse(selfie.toString()));
//            } else {
//                ivSelfie.setImageURI(Uri.fromFile(selfie));
//            }
//        }

        Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.no_image).into(ivSelfie)

        ivSelfie.setOnClickListener {
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

        val idFrontUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDLFront
        Glide.with(this).load(idFrontUrl)
            .placeholder(R.drawable.no_image).into(ivIdFront)
        ivIdFront.setOnClickListener {
            FileUtils.zoomImage(this, idFrontUrl!!)
        }

        val idBackUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDlBack

        Glide.with(this).load(idBackUrl)
            .placeholder(R.drawable.no_image).into(ivIdBack)
        ivIdBack.setOnClickListener {
            FileUtils.zoomImage(this, idBackUrl!!)
        }
//        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
//        if (idProofFront != null) {
////            tvIdFront.setText(idProofFront?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivIdFront.setImageURI(Uri.parse(idProofFront.toString()));
//            } else {
//                ivIdFront.setImageURI(Uri.fromFile(idProofFront));
//            }
//            llIdFront.setOnClickListener { FileUtils.zoomFileImage(this!!, idProofFront) }
//        }

//        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
//        if (idProofBack != null) {
////            tvIdBack.setText(idProofBack?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivIdBack.setImageURI(Uri.parse(idProofBack.toString()));
//            } else {
//                ivIdBack.setImageURI(Uri.fromFile(idProofBack));
//            }
//            llIdBack.setOnClickListener { FileUtils.zoomFileImage(this!!, idProofBack) }
//        }

        etIdNoManualEntered.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)

        val panCardUrl = ApiService.panCardUrl + rishtaUser.kycDetails.panCardFront
        Glide.with(this).load(panCardUrl)
            .placeholder(R.drawable.no_image).into(ivPanCardFront)
        ivPanCardFront.setOnClickListener {
            FileUtils.zoomImage(this, panCardUrl!!)
        }


//        val panCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
//        if (panCardFront != null) {
////            tvPanCardFront.setText(panCardFront?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivPanCardFront.setImageURI(Uri.parse(panCardFront.toString()));
//            } else {
//                ivPanCardFront.setImageURI(Uri.fromFile(panCardFront));
//            }
//            llPanFront.setOnClickListener { FileUtils.zoomFileImage(this!!, panCardFront) }
//        }

//        val panCardBack = CacheUtils.getFileUploader().getPanCardBackFile()
//        if (panCardBack != null) {
////            tvPanCardBack.setText(panCardBack?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivPanCardBack.setImageURI(Uri.parse(panCardBack.toString()));
//            } else {
//                ivPanCardBack.setImageURI(Uri.fromFile(panCardBack));
//            }
//            llPanBack.setOnClickListener { FileUtils.zoomFileImage(this!!, panCardBack) }
//        }
        etPanNoManualEntered.setText(rishtaUser.kycDetails.panCardNo)
        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
//        etTypeOfAccount.setText(rishtaUser.bankDetail?.bankAccType)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)

//        val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
//        if (checkBookFile != null) {
////            tvChequePhoto.setText(checkBookFile?.name)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ivChequePhoto.setImageURI(Uri.parse(checkBookFile.toString()));
//            } else {
//                ivChequePhoto.setImageURI(Uri.fromFile(checkBookFile));
//            }
//            ivChequePhoto.setOnClickListener { FileUtils.zoomFileImage(this!!, checkBookFile) }
//        }

        val checkPhoto = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto

        ivChequePhoto.setOnClickListener {
            val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
            if (checkBookFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivChequePhoto.setImageURI(Uri.parse(checkBookFile.toString()));
                } else {
                    ivChequePhoto.setImageURI(Uri.fromFile(checkBookFile));
                }
                FileUtils.zoomFileImage(this, checkBookFile)
            } else {
                FileUtils.zoomImage(this, checkPhoto!!)
            }
        }
        Glide.with(this).load(checkPhoto)
            .placeholder(R.drawable.no_image).into(ivChequePhoto)


        etNameOfNominee.setText(rishtaUser.bankDetail?.nomineeName)
        tvNomineeDob.setText(rishtaUser.bankDetail?.nomineeDob)
        etNomineeMobileNo.setText(rishtaUser.bankDetail?.nomineeMobileNo)
        etNomineeEmail.setText(rishtaUser.bankDetail?.nomineeEmail)
        etNomineeAddress.setText(rishtaUser.bankDetail?.nomineeAdd)
        etNomineeRelationship.setText(rishtaUser.bankDetail?.nomineeRelation)
        if (rishtaUser.addDiff == 1) {
            llCurrentAdd.visibility = View.VISIBLE
        } else {
            llCurrentAdd.visibility = View.GONE
        }
        val position2 =
            AppUtils.getPosition(this, R.array.account_type, rishtaUser.bankDetail?.bankAccType);
        spAccountType.setSelection(position2)
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

    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)


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

        if (requestCode == FileUtils.selfie) {
            AppUtils.checkNullAndSetText(tvSelfie, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivSelfie)
            FileUtils.setToFileUploader(requestCode, compressImage)
        }

        if (requestCode == FileUtils.chequeBookPhoto) {
            AppUtils.checkNullAndSetText(tvChequePhoto, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivChequePhoto)
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
        toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }

            R.id.llNomDob -> {
                AppUtils.getDate(this, tvNomineeDob, false)
            }

            R.id.llCheckPhoto -> {
                selectOrCaptureImage(FileUtils.chequeBookPhoto)
            }
            R.id.llDOB -> {
                showToast(getString(R.string.click_year_to_change))
                AppUtils.getDate(this!!, tvDob, true)
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

            R.id.btnSubmit -> {
                getInputs()
            }
        }
    }

    private fun getInputs() {
        val rishtaUser = VguardRishtaUser()
        val genderPos = spGender.selectedItemPosition
        val gender = spGender.selectedItem as String
        val dob = tvDob.text.toString()
        val email = etEmail.text.toString()
        var isAddDiff = -1
        if (spIsCurrentAddSame.selectedItemPosition == 1) {
            isAddDiff = 1
        } else if (spIsCurrentAddSame.selectedItemPosition == 2) {
            isAddDiff = 0
        }


        val currAddress = etCurrentAddress.text.toString()
        val streetLocality = etCurrentStreetLocality.text.toString()
        val landmark = etCurrentLandmark.text.toString()

        val city = etCurrentCity.text.toString()
        val pinCode = etCurrentPinCode.text.toString()

//        val profession = if (spProfession.selectedItemPosition == 0) {
//            null
//        } else {
//            spProfession.selectedItemPosition
//        }
        rishtaUser.maritalStatusId = if (spMaritalStatus.selectedItemPosition == 0) {
            null
        } else {
            spMaritalStatus.selectedItemPosition
        }

        if (spProfession.selectedItemPosition != 0) {
            if (spProfession.selectedItem is Profession) {
                val selectedProfession = spProfession.selectedItem as Profession
                rishtaUser.professionId = selectedProfession.professionId
                rishtaUser.profession = selectedProfession.professionName
            }
        }

        if (spSubProfession.selectedItemPosition != 0) {
            if (spSubProfession.selectedItem is Profession) {
                val selectedSubProfession = spSubProfession.selectedItem as Profession
                rishtaUser.subProfessionId = selectedSubProfession.professionId
                rishtaUser.subProfession = selectedSubProfession.professionName
            }
        }

        if (spAlreadyEnrolledLoyalty.selectedItemPosition != 0) {
            rishtaUser.enrolledOtherScheme = spAlreadyEnrolledLoyalty.selectedItemPosition
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


        val kycDetails = KycDetails()
        rishtaUser.gender = gender
        rishtaUser.genderPos = genderPos.toString()
        rishtaUser.dob = dob
        rishtaUser.emailId = email
        rishtaUser.addDiff = isAddDiff
        rishtaUser.currentAddress = currAddress
        rishtaUser.currStreetAndLocality = streetLocality
        rishtaUser.currLandmark = landmark
        rishtaUser.whatsappNo = etWhatsAppNumber.text.toString()

        if (isAddDiff == 1) {
            val cityMaster = spCurrCity.selectedItem as CityMaster
            val distMaster = spCurrDist.selectedItem as DistrictMaster
            val stateMaster = spCurrState.selectedItem as StateMaster
            rishtaUser.currState = stateMaster.stateName
            rishtaUser.currStateId = stateMaster.id.toString()
            rishtaUser.currDistId = distMaster.id.toString()
            rishtaUser.currDist = distMaster.districtName
            rishtaUser.currCityId = cityMaster.id.toString()
            rishtaUser.currCity = cityMaster.cityName
            rishtaUser.currPinCode = pinCode
            if (spCurrCity.selectedItem is CityMaster) {
                rishtaUser.cityId = cityMaster.id.toString()
                if (cityMaster.id != -2L) {
                    rishtaUser.currCity = cityMaster.cityName.toString()
                    rishtaUser.otherCurrCity = null
                }
                if (cityMaster.id == -2L) {
                    rishtaUser.currCity = null
                    rishtaUser.otherCurrCity = etCurrentCity.text.toString()
                }
            }
        } else if (isAddDiff == 0) {
//            rishtaUser.currState = stateMaster.stateName
            rishtaUser.currStateId = CacheUtils.getRishtaUser().stateId
            rishtaUser.currDistId = CacheUtils.getRishtaUser().distId
//            rishtaUser.currDist = distMaster.districtName
            rishtaUser.currCityId = CacheUtils.getRishtaUser().cityId
//            rishtaUser.currCity = cityMaster.cityName
            rishtaUser.currPinCode = pinCode
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
        rishtaUser.kycDetails = kycDetails

        var bankDetail = rishtaUser.bankDetail
        if (bankDetail == null) {
            bankDetail = BankDetail()
        }

        try{
        if (spAccountType.selectedItemPosition != 0) {
            bankDetail.bankAccType = spAccountType.selectedItem as String
            bankDetail.bankAccTypePos = spAccountType.selectedItemPosition.toString()
        }

        if (spBanks.selectedItem is BankDetail) {
            val bd = spBanks.selectedItem as BankDetail
            if (bd.bankId != -1) {
                bankDetail.bankNameAndBranch = bd.bankNameAndBranch
                bankDetail.bankId = bd.bankId
            }
            if (bd.bankId == -1) {
                bankDetail.bankNameAndBranch = null
            }
        }}
        catch (e :Exception){
            
        }

        bankDetail.bankAccNo = etAccountNumber.text.toString()
        bankDetail.bankAccHolderName = etAccountHolderName.text.toString()
        bankDetail.bankIfsc = etIfscCode.text.toString()

        bankDetail.nomineeName = etNameOfNominee.text.toString()
        bankDetail.nomineeAdd = etNomineeAddress.text.toString()
        bankDetail.nomineeEmail = etNomineeEmail.text.toString()
        bankDetail.nomineeDob = tvNomineeDob.text.toString()
        bankDetail.nomineeMobileNo = etNomineeMobileNo.text.toString()
        bankDetail.nomineeRelation = etNomineeRelationship.text.toString()
        bankDetail.checkPhoto = CacheUtils.getRishtaUser().bankDetail?.checkPhoto
        rishtaUser.bankDetail = bankDetail
        updateProfilePresenter.validate(rishtaUser)
    }

    override fun showMsgDialog(message: String) {

        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun finishView() {
        this.finish()
    }
}