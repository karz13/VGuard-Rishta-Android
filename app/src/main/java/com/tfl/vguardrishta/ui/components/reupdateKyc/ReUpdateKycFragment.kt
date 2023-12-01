package com.tfl.vguardrishta.ui.components.reupdateKyc

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
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.hideSoftKeyBoard
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_re_update_kyc_and_profile.*
import java.io.File
import java.util.*
import javax.inject.Inject

class ReUpdateKycFragment : BaseFragment<ReUpdateKycContract.View, ReUpdateKycContract.Presenter>(),
    ReUpdateKycContract.View,
    View.OnClickListener, ActivityFinishListener {
    private var pinCodeId: String? = null
    private var tempCurrentPincode: String? = ""
    private var tempPermPincode: String? = ""

    private var currPinCodeId: String = ""
    private var pinCodeDetails: PincodeDetails? = null

    private var tempCurrStateId: String? = ""
    private var tempCurrDistId: String? = ""
    private var tempCurrCityId: String? = ""

    private var tempPermStateId: String? = ""
    private var tempPermDistId: String? = ""
    private var tempPermCityId: String? = ""


    private lateinit var searchableCurrPincodeBaseAdapter: SearchableBaseAdapter
    private lateinit var searchableBaseAdapter: SearchableBaseAdapter

    @Inject
    lateinit var reUpdateKycPresenter: ReUpdateKycPresenter

    lateinit var rejectionReasonListAdapter: RejectionReasonListAdapter

    private lateinit var progress: Progress

    override fun initPresenter(): ReUpdateKycContract.Presenter {
        return reUpdateKycPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_re_update_kyc_and_profile
    }

    override fun initUI() {
//        setSupportActionBar(toolbar_main)
//        customToolbar.updateToolbar("", getString(R.string.edit_profile), "")
        progress = Progress(context!!, R.string.please_wait)
        rejectionReasonListAdapter = RejectionReasonListAdapter()
        svUserDet.scrollTo(0, 0)
        rcvRejectionRemarks.adapter = rejectionReasonListAdapter
//        val rishtaUser = CacheUtils.getRishtaUser()
//        tvUserName.text = rishtaUser.name
//        tvRishtaId.text = rishtaUser.userCode
//        ivBack.setOnClickListener(this)
        llCheckPhoto.setOnClickListener(this)
        llNomDob.setOnClickListener(this)
        hideKeyBoard()
        val currentFocus = activity?.currentFocus
        currentFocus?.hideSoftKeyBoard()
        etAnnualBusiness.addTextChangedListener(NumberTextWatcher(etAnnualBusiness))
        setPermPinCodeListeners()
        setCurrentPincodeListeners()

        llUpdateSelfie.setOnClickListener {
            selectOrCaptureImage(FileUtils.selfie)
        }

        llIdFront.setOnClickListener {
            selectOrCaptureImage(FileUtils.idCardPhotoFront)
        }

        llIdBack.setOnClickListener {
            selectOrCaptureImage(FileUtils.idCardPhotoBack)
        }

        llPanFront.setOnClickListener {
            selectOrCaptureImage(FileUtils.panCardFront)
        }

        llDOB.setOnClickListener(this)
        btnPreview.setOnClickListener(this)

        spProfession.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val profession = spProfession.selectedItem as Profession
                    reUpdateKycPresenter.getSubProfessions(profession.professionId.toString())
                } else {
                    flSubProfession.visibility = View.GONE
                }
            }
        }
        spIsWhatsappAddSame.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val rishtaUser = CacheUtils.getRishtaUser()
                if (position != 0) {
                    if (position == 1) {
                        etWhatsAppNumber.setText(rishtaUser.mobileNo)
                        etWhatsAppNumber.isEnabled = false
                    } else {
                        etWhatsAppNumber.setText(rishtaUser.whatsappNo)
                        etWhatsAppNumber.isEnabled = true
                    }
                } else {
                    etWhatsAppNumber.isEnabled = true
                    etWhatsAppNumber.setText(rishtaUser.whatsappNo)
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
                        reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
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
        setCurrStatesSpinner(CacheUtils.getFirstState(activity!!))
        setCurrDistrictSpinner(CacheUtils.getFirstDistrict(activity!!))
        setCurrCitySpinner(CacheUtils.getFirstCity(activity!!))
//        updateProfilePresenter.getStates()
       // reUpdateKycPresenter.getUserDetails();

        showUserDetails()

    }


    override fun showUserDetails() {
        setInputs()

        reUpdateKycPresenter.getBanks();
        reUpdateKycPresenter.getProfessions()
     //   reUpdateKycPresenter.getKycIdTypes()

        svUserDet.scrollTo(0, 0)

    }

    private fun setPermPinCodeListeners() {
        searchableBaseAdapter = SearchableBaseAdapter()

        etPinCode.setAdapter(searchableBaseAdapter)

        etPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etPinCode.setText(any.toString())
            etPinCode.tag = any
            this.pinCodeDetails = any
            etPinCode.setSelection(any.toString().length)
            reUpdateKycPresenter.getStateDistCitiesByPermPincodeId(any.pinCodeId.toString())
            pinCodeId = any.pinCodeId.toString()
            etPinCode.clearFocus()

        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                reUpdateKycPresenter.getPermPincodeList(etPinCode.text.toString())
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etPinCode.showDropDown()
        }
    }


    private fun setCurrentPincodeListeners() {
        searchableCurrPincodeBaseAdapter = SearchableBaseAdapter()

        etCurrentPinCode.setAdapter(searchableCurrPincodeBaseAdapter)

        etCurrentPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableCurrPincodeBaseAdapter.mList[position] as PincodeDetails
            etCurrentPinCode.setText(any.toString())
            etCurrentPinCode.tag = any
            this.pinCodeDetails = any
            etCurrentPinCode.setSelection(any.toString().length)
            reUpdateKycPresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()

        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
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
            val tempList = searchableCurrPincodeBaseAdapter.tempList
            searchableCurrPincodeBaseAdapter.mList = tempList
            etCurrentPinCode.showDropDown()
        }
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableCurrPincodeBaseAdapter.mList = it
        searchableCurrPincodeBaseAdapter.tempList = it
        searchableCurrPincodeBaseAdapter.notifyDataSetChanged()
        etCurrentPinCode.showDropDown()
//        etPinCode.requestFocus()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            reUpdateKycPresenter.getStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etCurrentPinCode.dismissDropDown()
        }
    }

    override fun setPermPinCodeList(it: List<PincodeDetails>) {
        searchableBaseAdapter.mList = it
        searchableBaseAdapter.tempList = it
        searchableBaseAdapter.notifyDataSetChanged()
        etPinCode.showDropDown()
//        etPinCode.requestFocus()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            reUpdateKycPresenter.getStateDistCitiesByPermPincodeId(pincodeDetails.pinCodeId.toString())
            etPinCode.dismissDropDown()
        }
    }

    override fun setPermStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spState?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.stateId != null && rishtaUser.stateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.stateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spState.setSelection(index)
        } else if (!tempPermStateId.isNullOrEmpty()) {
            val sm = arrayList.find { it.id == tempPermStateId!!.toLong() }
            val index = arrayList.indexOf(sm)
            spState.setSelection(index)
        }

        spState.onItemSelectedListener = object : SpinnerUtils() {
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

    override fun setPermDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spDist?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.distId != null && rishtaUser.distId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.distId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spDist.setSelection(index)
        } else if (!tempPermDistId.isNullOrEmpty()) {
            val dm = arrayList.find { it.id == tempPermDistId!!.toLong() }
            val index = arrayList.indexOf(dm)
            spDist.setSelection(index)
        }

        spDist.onItemSelectedListener = object : SpinnerUtils() {
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

    override fun setPermCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCity?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.cityId != null && rishtaUser.cityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.cityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCity.setSelection(index)
        } else if (!tempPermCityId.isNullOrEmpty()) {
            val cm = arrayList.find { it.id == tempPermCityId!!.toLong() }
            val index = arrayList.indexOf(cm)
            spCity.setSelection(index)
        }

        spCity.onItemSelectedListener = object : SpinnerUtils() {
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
                        tlCity.visibility = View.VISIBLE
                        etCity.setText(newRishtaUser.otherCity)
                    }
                    if (cityMaster.id != -2L) {
                        tlCity.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun setCurrCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            if (it != null && it.isNotEmpty()) {
                val cityMaster = it[0]
                tempCurrStateId = cityMaster.stateId?.toString()
                tempCurrDistId = cityMaster.districtId?.toString()
                tempCurrCityId = cityMaster.id?.toString()
            }
        }
    }

    override fun setPermCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            if (it != null && it.isNotEmpty()) {
                val cityMaster = it[0]
                tempPermStateId = cityMaster.stateId?.toString()
                tempPermDistId = cityMaster.districtId?.toString()
                tempPermCityId = cityMaster.id?.toString()
            }
        }
    }

    override fun processKycIdTypes(arrayList: List<KycIdTypes>) {
      /*  spIdProofs?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.ID_TYPES
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (rishtaUser.kycDetails.kycId != null) {
            val kycTypes = arrayList.find { it.kycId == rishtaUser.kycDetails.kycId!!.toInt() }
            val index = arrayList.indexOf(kycTypes)
            spIdProofs.setSelection(index)
        }*/
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
                activity!!,
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
                activity!!,
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
        val dialog = AlertDialog.Builder(activity)
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

    override fun setCurrStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spCurrState?.adapter =
            CustomSpinner(
                activity!!,
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

    override fun setCurrDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spCurrDist?.adapter =
            CustomSpinner(
                activity!!,
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


    override fun setCurrCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCurrCity?.adapter =
            CustomSpinner(
                activity!!,
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
        spPreferredLanguage.setSelection(AppUtils.getSelectedLangPos())
        spPreferredLanguage.isEnabled = false

        etReferralCode.setText(rishtaUser.referralCode)
        etNameOfReferee.setText(rishtaUser.nameOfReferee)
        etName.setText(rishtaUser.name)
        val position =
            AppUtils.getPosition(activity!!, R.array.select_gender, rishtaUser.gender?.trim());
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
        etPinCode.setText(rishtaUser.pinCode)

        if (rishtaUser.addDiff != -1) {
            if (rishtaUser.addDiff == 0) {
                spIsCurrentAddSame.setSelection(1)
            } else {
                spIsCurrentAddSame.setSelection(2)
            }
        }

        if (rishtaUser.isWhatsAppSame == 1) {
            spIsWhatsappAddSame.setSelection(1)
        } else if (rishtaUser.isWhatsAppSame == 0) {
            spIsWhatsappAddSame.setSelection(2)
        }


        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)
        etCurrentPinCode.setText(rishtaUser.currPinCode)
        reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
        tempCurrentPincode = rishtaUser.currPinCode
        tempPermPincode = rishtaUser.pinCode
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


        val selfie = CacheUtils.getFileUploader().getUserPhotoFile()

        setImageForProofs(selfie, selfieUrl, ivSelfie)


        val idFrontUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDLFront
        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
        setImageForProofs(idProofFront, idFrontUrl, ivIdFront)

        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
        val idBackUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDlBack
        setImageForProofs(idProofBack, idBackUrl, ivIdBack)

        etIdNoManualEntered.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)

        val panCardUrl = ApiService.panCardUrl + rishtaUser.kycDetails.panCardFront
        val panCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        setImageForProofs(panCardFront, panCardUrl, ivPanCardFront)

        etPanNoManualEntered.setText(rishtaUser.kycDetails.panCardNo)
        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
//        etTypeOfAccount.setText(rishtaUser.bankDetail?.bankAccType)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)


        val checkPhotoUrl = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto
        val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        setImageForProofs(checkBookFile, checkPhotoUrl, ivChequePhoto)

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
            AppUtils.getPosition(
                activity!!,
                R.array.account_type,
                rishtaUser.bankDetail?.bankAccType
            );
        spAccountType.setSelection(position2)
        if (!rishtaUser.rejectedReasonsStr.isNullOrEmpty()) {
            if (rishtaUser.rejectedReasonsStr!!.contains(",")) {
                val split = rishtaUser.rejectedReasonsStr!!.split(",")
                rejectionReasonListAdapter.mList = split
                rejectionReasonListAdapter.notifyDataSetChanged()
            } else {
                val strReasons = arrayListOf<String>()
                strReasons.add(rishtaUser.rejectedReasonsStr!!)
                rejectionReasonListAdapter.mList = strReasons
                rejectionReasonListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setImageForProofs(file: File?, proofUrl: String, imageView: ImageView) {
        if (file != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageView.setImageURI(Uri.parse(file.toString()));
            } else {
                imageView.setImageURI(Uri.fromFile(file));
            }
        } else {
            Glide.with(this).load(proofUrl)
                .placeholder(R.drawable.no_image).into(imageView)
        }

        imageView.setOnClickListener {
            if (file != null) {
                FileUtils.zoomFileImage(activity!!, file)
            } else
                FileUtils.zoomImage(activity!!, proofUrl!!)
        }
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

    override fun hideKeyBoard() = AppUtils.hideKeyboard(activity!!)


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
                compressImage = FileUtils.getFilePath(bitmap, context!!)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)

        if (requestCode == FileUtils.selfie) {
            AppUtils.checkNullAndSetText(tvSelfie, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivSelfie)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getSelfieUrl(), ivSelfie)
        } else if (requestCode == FileUtils.idCardPhotoFront) {
            AppUtils.checkNullAndSetText(tvIdFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivIdFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getIdCardUrl(), ivIdFront)

        } else if (requestCode == FileUtils.idCardPhotoBack) {
            AppUtils.checkNullAndSetText(tvIdBack, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivIdBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getIdCardUrl(), ivIdBack)
        } else if (requestCode == FileUtils.panCardFront) {
            AppUtils.checkNullAndSetText(tvPanCardFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivPanCardFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, ApiService.panCardUrl, ivPanCardFront)

        } else if (requestCode == FileUtils.chequeBookPhoto) {
            AppUtils.checkNullAndSetText(tvChequePhoto, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivChequePhoto)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getChequeUrl(), ivChequePhoto)

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
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {


            R.id.llNomDob -> {
                AppUtils.getDate(activity!!, tvNomineeDob, false)
            }

            R.id.llCheckPhoto -> {
                selectOrCaptureImage(FileUtils.chequeBookPhoto)
            }
            R.id.llDOB -> {
                showToast(getString(R.string.click_year_to_change))
                AppUtils.getDate(activity!!, tvDob, true)
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

            R.id.btnPreview -> {
                getInputs()
            }
        }
    }

    private fun getInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()
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
        val isWhatsAppSame = if (spIsWhatsappAddSame.selectedItemPosition == 0) {
            null
        } else if (spIsWhatsappAddSame.selectedItemPosition == 1) {
            1
        } else {
            0
        }
        val permAddress = etPermanentAddress.text.toString()
        val permStreetLocality = etPermanentAddress.text.toString()
        val permLandmark = etLandmark.text.toString()
//        val permCity = etCity.text.toString()
        val permPincode = etPinCode.text.toString()
        val currAddress = etCurrentAddress.text.toString()
        val currStreetLocality = etCurrentStreetLocality.text.toString()
        val currLandmark = etCurrentLandmark.text.toString()

        val currentCity = etCurrentCity.text.toString()
        val currentPinCOde = etCurrentPinCode.text.toString()

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

      //  val kycIdType = spIdProofs.selectedItem as KycIdTypes

        val kycDetails = rishtaUser.kycDetails
        val lang = spPreferredLanguage.selectedItem as String
        rishtaUser.preferredLanguage = lang.toString()
        rishtaUser.preferredLanguagePos = AppUtils.getSelectedLangId().toString()
        rishtaUser.gender = gender
        rishtaUser.genderPos = genderPos.toString()
        rishtaUser.dob = dob
        rishtaUser.emailId = email
        rishtaUser.addDiff = isAddDiff
        rishtaUser.permanentAddress = permAddress
        rishtaUser.landmark = permLandmark
        rishtaUser.pinCode = permPincode
        rishtaUser.currentAddress = currAddress
        rishtaUser.currStreetAndLocality = currStreetLocality
        rishtaUser.currLandmark = currLandmark
        rishtaUser.whatsappNo = etWhatsAppNumber.text.toString()
        rishtaUser.isWhatsAppSame = isWhatsAppSame

        if (spState.selectedItem is StateMaster) {
            val stateMaster = spState.selectedItem as StateMaster
            rishtaUser.state = stateMaster.stateName.toString()
            rishtaUser.stateId = stateMaster.id.toString()
        }

        if (spDist.selectedItem is DistrictMaster) {
            val distMaster = spDist.selectedItem as DistrictMaster
            rishtaUser.distId = distMaster.id.toString()
            rishtaUser.dist = distMaster.districtName.toString()
        }

        if (spCity.selectedItem is CityMaster) {
            val cityMaster = spCity.selectedItem as CityMaster
            rishtaUser.cityId = cityMaster.id.toString()
            if (cityMaster.id != -2L) {
                rishtaUser.city = cityMaster.cityName.toString()
                rishtaUser.otherCity = null
            }
            if (cityMaster.id == -2L) {
                rishtaUser.city = null
                rishtaUser.otherCity = etCity.text.toString()
            }
        }
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
            rishtaUser.currPinCode = currentPinCOde
            if (spCurrCity.selectedItem is CityMaster) {
                rishtaUser.currCityId = cityMaster.id.toString()

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
            rishtaUser.currStateId = rishtaUser.stateId
            rishtaUser.currDistId = rishtaUser.distId
//            rishtaUser.currDist = distMaster.districtName
            rishtaUser.currCityId = rishtaUser.cityId
//            rishtaUser.currCity = cityMaster.cityName
            rishtaUser.currPinCode = currentPinCOde
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
        kycDetails.aadharOrVoterOrDlNo = etIdNoManualEntered.text.toString()
        kycDetails.panCardNo = etPanNoManualEntered.text.toString()
        kycDetails.kycId = 1
        kycDetails.kycIdName = "Aadhar Card"
    /*    if (kycIdType.kycId != -1) {
            kycDetails.kycIdName = kycIdType.kycIdName
        }*/
        rishtaUser.kycDetails = kycDetails

        var bankDetail = rishtaUser.bankDetail
        if (bankDetail == null) {
            bankDetail = BankDetail()
        }

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
        reUpdateKycPresenter.validate(rishtaUser)
    }

    override fun navgateToPreview() {
        (activity as ReUpdateKycActivity).navigateToPreview()
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, activity!!, message, this)
    }

    override fun finishView() {
        activity!!.finish()
    }
}