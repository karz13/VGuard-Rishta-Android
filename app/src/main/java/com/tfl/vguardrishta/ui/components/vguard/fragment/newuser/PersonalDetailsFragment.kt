package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.view.View
import android.widget.AdapterView
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CityMaster
import com.tfl.vguardrishta.models.DistrictMaster
import com.tfl.vguardrishta.models.PincodeDetails
import com.tfl.vguardrishta.models.StateMaster
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_new_user_personal_details.*
import java.util.*
import javax.inject.Inject

class PersonalDetailsFragment :
    BaseFragment<NewUserRegContract.View, NewUserRegContract.Presenter>(), NewUserRegContract.View,
    View.OnClickListener {
    private var pinCodeDetails: PincodeDetails? = null

    private var pinCodeId: String? = null


    @Inject
    lateinit var newUserRegPresenter: NewUserRegPresenter

    var userSelect = false
    private lateinit var progress: Progress

    private lateinit var searchableBaseAdapter: SearchableBaseAdapter
    override fun initPresenter(): NewUserRegContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_new_user_personal_details
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)

        searchableBaseAdapter = SearchableBaseAdapter()

        etPinCode.setAdapter(searchableBaseAdapter)

        etPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etPinCode.setText(any.toString())
            etPinCode.tag = any
            this.pinCodeDetails = any
            etPinCode.setSelection(any.toString().length)
            newUserRegPresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            pinCodeId = any.pinCodeId.toString()
            etPinCode.clearFocus()

        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                newUserRegPresenter.getPincodeList(etPinCode.text.toString())
            }
        }

        etPinCode.setOnFocusChangeListener { view, focus ->
            if (!focus) {
                if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                    newUserRegPresenter.getPincodeList(etPinCode.text.toString())
                }
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etPinCode.showDropDown()
        }

//        newUserRegPresenter.getStates()
        val rishtaUser = CacheUtils.getNewRishtaUser()

        setInputs()
        llDOB.setOnClickListener(this)
        btnNext.setOnClickListener(this)

        etReferralCode.setOnFocusChangeListener { v, focus ->
            if (!focus) {

                if (!etReferralCode.text.isNullOrEmpty()) {
                    newUserRegPresenter.getReferalName(etReferralCode.text.toString())
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
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableBaseAdapter.mList = it
        searchableBaseAdapter.tempList = it
        searchableBaseAdapter.notifyDataSetChanged()
        etPinCode.showDropDown()
//        etPinCode.requestFocus()
    }

    override fun setDetailsByPinCode(it: PincodeDetails?) {
        if (it != null) {
            val rishtaUser = CacheUtils.getNewRishtaUser()
            rishtaUser.stateId = it.stateId?.toString()
            rishtaUser.distId = it.distId?.toString()
            rishtaUser.cityId = it.cityId?.toString()
            newUserRegPresenter.getStates()
        }
    }

    override fun setCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            val cityMaster = it[0]
            val newRishtaUser = CacheUtils.getNewRishtaUser()
            newRishtaUser.stateId = cityMaster.stateId?.toString()
            newRishtaUser.distId = cityMaster.districtId?.toString()
            newRishtaUser.cityId = cityMaster.id?.toString()
        }
    }

    override fun showNameOfReferee(nameOfReferee: String) {
        etNameOfReferee.setText(nameOfReferee.toUpperCase(Locale.ENGLISH))
    }

    private fun setInputs() {
        val rishtaUser = CacheUtils.getNewRishtaUser()

        spPreferredLanguage.setSelection(AppUtils.getSelectedLangPos())
        spPreferredLanguage.isEnabled = false
        etReferralCode.setText(rishtaUser.referralCode)
        etNameOfReferee.setText(rishtaUser.nameOfReferee)
        etName.setText(rishtaUser.name)

        rishtaUser.genderPos?.toInt()?.let { spGender.setSelection(it) }

        tvDob.setText(rishtaUser.dob)
        etContactNumber.setText(rishtaUser.mobileNo)
        etWhatsAppNumber.setText(rishtaUser.whatsappNo)
        etEmail.setText(rishtaUser.emailId)
        etPermanentAddress.setText(rishtaUser.permanentAddress)
        etStreetLocality.setText(rishtaUser.streetAndLocality)
        etLandmark.setText(rishtaUser.landmark)
        etCity.setText(rishtaUser.city)


//        rishtaUser.stateId?.toInt()?.let {
//            spState.setSelection(it)
//        }
        setStatesSpinner(CacheUtils.getFirstState(context!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(context!!))
        setCitySpinner(CacheUtils.getFirstCity(context!!))
        etPinCode.setText(rishtaUser.pinCode)
        if (rishtaUser.pinCodeId != null) {
            pinCodeId = rishtaUser.pinCodeId
            newUserRegPresenter.getStateDistCitiesByPincodeId(rishtaUser.pinCodeId!!)
        }
        if (rishtaUser.isWhatsAppSame == 1) {
            spIsWhatsappAddSame.setSelection(1)
        } else if (rishtaUser.isWhatsAppSame == 0) {
            spIsWhatsappAddSame.setSelection(0)
        }
    }

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spState?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (rishtaUser.stateId != null && rishtaUser.stateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.stateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
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

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spDist?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (rishtaUser.distId != null && rishtaUser.distId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.distId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
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

    override fun setCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCity?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (rishtaUser.cityId != null && rishtaUser.cityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.cityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
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

            R.id.llDOB -> {
                showToast(getString(R.string.click_year_to_change))
                AppUtils.getDate(activity!!, tvDob, true)
            }
        }
    }

    private fun getInputs() {
        val rishtaUser = CacheUtils.getNewRishtaUser()

        val referralCode = etReferralCode.text.toString()
        val nameOfReferee = etNameOfReferee.text.toString()
        val name = etName.text.toString()


        val dob = tvDob.text.toString()
        val contactNo = etContactNumber.text.toString()
        val waNumber = etWhatsAppNumber.text.toString()
        val email = etEmail.text.toString()
        val permAddress = etPermanentAddress.text.toString()
        val streetLocality = etStreetLocality.text.toString()
        val landmark = etLandmark.text.toString()
        val city = etCity.text.toString()

        val isWhatsAppSame = if (spIsWhatsappAddSame.selectedItemPosition == 0) {
            null
        } else if (spIsWhatsappAddSame.selectedItemPosition == 1) {
            1
        } else {
            0
        }


        val pinCode = etPinCode.text.toString()
        val lang = spPreferredLanguage.selectedItem as String
        rishtaUser.preferredLanguage = lang.toString()
        rishtaUser.preferredLanguagePos = AppUtils.getSelectedLangId().toString()

        rishtaUser.referralCode = referralCode
        rishtaUser.nameOfReferee = nameOfReferee
        rishtaUser.name = name
        val genderPos = spGender.selectedItemPosition

        if (genderPos != 0) {
            val gender = spGender.selectedItem as String
            rishtaUser.gender = gender
            rishtaUser.genderPos = genderPos.toString()
        }

        rishtaUser.dob = dob
        rishtaUser.contactNo = contactNo
        rishtaUser.isWhatsAppSame = isWhatsAppSame
        rishtaUser.whatsappNo = waNumber
        rishtaUser.emailId = email
        rishtaUser.permanentAddress = permAddress
        rishtaUser.streetAndLocality = streetLocality
        rishtaUser.landmark = landmark
        rishtaUser.city = city

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

        rishtaUser.pinCode = pinCode
        rishtaUser.pinCodeId = pinCodeId
        newUserRegPresenter.verifyNewUserData(rishtaUser)
    }

    override fun navigateToNewUserKyc() {
        (activity as NewUserRegistrationActivity).navigateToNewUserKycScreen()
    }

}