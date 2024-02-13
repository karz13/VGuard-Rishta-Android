package com.tfl.vguardrishta.utils

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.encryption.Encryption
import com.tfl.vguardrishta.extensions.hideSoftKeyBoard
import com.tfl.vguardrishta.models.Segments
import com.tfl.vguardrishta.remote.ApiService
import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object AppUtils {

    fun showToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun showLongToast(context: Context, message: String) {
//        val password = TestEnum.PASSWORD
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun getEncryption(): Encryption {
        return Encryption.getDefault("", "", ByteArray(16))
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            view.hideSoftKeyBoard()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isValidPinCode(pinCode: String): Boolean {
        val ePattern = "^[1-9][0-9]{5}$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(pinCode.toUpperCase())
        return m.matches()
    }

    fun isValidMobileNo(mobileNumber: String): Boolean {
        val ePattern = "^[3-9][0-9]{9}$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(mobileNumber.toUpperCase())
        return m.matches()
    }

    fun isValidEmailAddress(email: String): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    private fun checkValidDateOfBirth(year: Int): Boolean {
        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)
        val i = currentYear - 18
        return i >= year
    }

    fun dobdateValidate(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            val parseddate: Date = sdf.parse(date)
            val c2 = Calendar.getInstance()
            c2.add(Calendar.YEAR, -18)
            if (parseddate.after(c2.time)) {
                return false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return true
    }

    fun getEighteenYearInMillis(): Long {
        try {
            return (System.currentTimeMillis() - 567648000000L)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }


    fun showComposeMail(context: Context, mailId: String?) {
        val i = Intent(Intent.ACTION_SENDTO)
        i.data = Uri.parse("mailto:")
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailId))
        try {
            context.startActivity(Intent.createChooser(i, "Send Mail"))
//            if (i.resolveActivity(context.packageManager) != null) {
//                context.startActivity(i);
//            }
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }

    fun doPhoneCall(context: Context, string: String?) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionGranted =
                PermissionUtils.isPermissionGranted(context, Manifest.permission.CALL_PHONE)
            if (permissionGranted) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$string"))
                when {
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED -> return
                    else -> context.startActivity(intent)
                }
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PermissionUtils.REQUEST_CALL_PERMISSION
                )
            }
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$string"))
            context.startActivity(intent)
        }
    }

    fun redirectToPlayStore(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.update))
        builder.setPositiveButton("OK") { _, _ ->
            val appPackageName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (_: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun launchWhatsApp(context: Context, phoneNumber: String) {
        val url = "https://api.whatsapp.com/send?phone=+91$phoneNumber"
        try {
            context.packageManager?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            showToast(context, "Whatsapp is not installed in your phone.")
            e.printStackTrace()
        }

//        val intent = Intent(Intent.ACTION_SEND)
//
//        intent.type = "text/plain"
//        intent.setPackage("com.whatsapp")
//
//        intent.putExtra(
//            Intent.EXTRA_TEXT,
//            message)
//        if (intent
//                .resolveActivity(context.packageManager)
//            == null
//        ) {
//            showToast(context, "Whatsapp is not installed in your phone.")
//            return
//        }
//        context.startActivity(intent)
    }

    fun getDate(context: Context, textView: TextView, validate: Boolean) {
        Locale.setDefault(Locale.ENGLISH)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year1, monthOfYear, dayOfMonth ->
                val month1 = monthOfYear + 1
                val day2: String =
                    (if (dayOfMonth.toString().length == 1) "0$dayOfMonth" else dayOfMonth.toString())
                val month2: String =
                    (if (month1.toString().length == 1) "0$month1" else month1.toString())

//                if (validate) {
//                    val valid = dobdateValidate("$year1-$month2-$day2")
//                    if (valid) {
//                        textView.text = "$year1-$month2-$day2"
//                    } else {
//                        showToast(context, context.getString(R.string.age_below_18))
//                    }
//                } else {
//                    textView.text = "$year1-$month2-$day2"
//                }

                textView.text = "$year1-$month2-$day2"

            },
            year,
            month,
            day
        )
        if (validate) {
            dpd.datePicker.maxDate = (getEighteenYearInMillis())
        } else {
            dpd.datePicker.maxDate = System.currentTimeMillis()
        }
        dpd.show()
    }

    fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        return formatter.format(date)
    }

    fun checkNullAndSetText(textView: TextView, string: String?, defaultString: String) {
        if (string != null && string.isNotEmpty() && string != "null") {
            textView.text = string
        } else {
            textView.text = defaultString
        }
    }


    fun openPDF(context: Context, string: String?) {
        val url = ApiService.ecardUrl + Uri.encode(string)
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
//            showToast(context, context.getString(R.string.no_activity_found))
        }
    }


    fun showErrorDialog(layoutInflater: LayoutInflater, context: Context, s: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = s
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }


    fun showErrorDialogLongText(layoutInflater: LayoutInflater, context: Context, s: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg_long, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = s
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }


    fun showErrorDialogWithFinish(
        layoutInflater: LayoutInflater,
        context: Context,
        s: String,
        activityFinishListener: ActivityFinishListener
    ) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = s
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {
            activityFinishListener.finishView()
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
            activityFinishListener.finishView()
        }
    }


    fun openPDFWithUrl(context: Context, url: String?) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
//            showToast(context, context.getString(R.string.no_activity_found))
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        //TODO: changes android 10
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "" + System.currentTimeMillis() + ".jpg",
                null
            )
        return Uri.parse(path)
    }


    fun getOwenerInfo(inContext: Context,activity: Activity) : String{
       /* val manager: AccountManager = AccountManager.get(inContext)
        var accountName :String = ""
        val accounts = manager.getAccountsByType("com.google")
        try {

            for (account in accounts) {
                //Toast.makeText(inContext,account.name,Toast.LENGTH_SHORT).show();
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            return accountName
        }
        return accountName*/
        val tMgr: TelephonyManager =
            inContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mPhoneNumber: String = tMgr.getLine1Number()
        return mPhoneNumber
    }

    fun getPermission(activity: Activity){
        val arrPerm: ArrayList<String> = ArrayList()

        arrPerm.add(Manifest.permission.READ_PHONE_STATE)
        arrPerm.add(Manifest.permission.READ_PHONE_NUMBERS)


        if (!arrPerm.isEmpty()) {
            var permissions: Array<String?>? = arrayOfNulls(arrPerm.size)
            permissions = arrPerm.toArray(permissions)
            ActivityCompat.requestPermissions(activity, permissions,1)
        }
    }
    fun getIpAddress(context :Context) :String {

        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        val sb = StringBuilder()
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.getInetAddresses())
            for (addr in addrs) {
                if (!addr.isLoopbackAddress()) {
                    val sAddr: String = addr.getHostAddress().toUpperCase()
                    val isIPv4 : Boolean= sAddr.indexOf(':') < 0
                    if (isIPv4) {
                       val str = sAddr
                        sb.append(str+"\n")
                    }
                    else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            if (delim < 0)
                            {
                                val str2 = sAddr.toUpperCase()
                            }
                            else{
                                val str3 =sAddr.substring(0, delim)
                                .toUpperCase()}
                        }
                    }

                }
            }
        }

        return sb.toString()
    /*    val context: Context =context.getApplicationContext()
        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val ip: String = Formatter().format(wm.getConnectionInfo().getIpAddress().toString()).toString()
        val ip2: String = wm.getConnectionInfo().getIpAddress().toString()*/
    }


    fun openTermsAndConditions(context: Context) {
        val pdfURL = ApiService.downLoadURL + Constants.PDF
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfURL))
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(context, context.getString(R.string.no_activity_found))
        }
    }

    fun isLocationPermissionGiven(activity: Activity): Boolean {
        val permissionGranted = PermissionUtils.isPermissionGranted(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (!permissionGranted) {
            try {
                val permissionUtils = PermissionUtils(activity)
                permissionUtils.requestForAllMandatoryPermissions()
            } catch (e: Exception) {
            }

//            showToast(getString(R.string.enable_location_permission))

        }
        return permissionGranted
    }

    fun checkTotalSelectedSegment(arrayList: List<Segments>): Int {
        var count = 0
        for (segment in arrayList) {
            if (segment.isChecked)
                count++
        }
        return count;
    }

    fun getAppName(context: Context): String? {
        var appName: String = ""
        val applicationInfo = context.getApplicationInfo()
        val stringId = applicationInfo.labelRes
        if (stringId == 0) {
            appName = applicationInfo.nonLocalizedLabel.toString()
        } else {
            appName = context.getString(stringId)
        }
        return appName
    }

    fun getPosition(
        context: Context,
        resArray: Int,
        value: String?
    ): Int {
        val array = context.resources.getStringArray(resArray)
        for (i in array.indices) {
            if (value != null && array[i].toUpperCase(Locale.getDefault())
                    .equals(value.toUpperCase(Locale.getDefault()), ignoreCase = true)
            ) return i
        }
        return -1
    }

    fun showTerms(context: Context) {
        val selectedLanguage = CacheUtils.getSelectedLanguage()
        var url = ApiService.tAndCUrl
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            when (selectedLanguage) {
                "en" -> url = ApiService.tAndCUrl
                "hi" -> url = ApiService.tAndCUrl_Hindi
                "bn" -> url = ApiService.tAndCUrl_Bengali
                "kn" -> url = ApiService.tAndCUrl_Kannada
                "ml" -> url = ApiService.tAndCUrl_Malayalam
                "mr" -> url = ApiService.tAndCUrl_Marathi
                "ta" -> url = ApiService.tAndCUrl_Tamil
                "te" -> url = ApiService.tAndCUrl_Telugu
                else -> ApiService.tAndCUrl
            }
        } else {
            when (selectedLanguage) {
                "en" -> url = ApiService.retTAndCUrl
                "hi" -> url = ApiService.retTAndCUrl
                "bn" -> url = ApiService.retTAndCUrl
                "kn" -> url = ApiService.retTAndCUrl
                "ml" -> url = ApiService.retTAndCUrl
                "mr" -> url = ApiService.retTAndCUrl
                "ta" -> url = ApiService.retTAndCUrl
                "te" -> url = ApiService.retTAndCUrl
                else -> ApiService.tAndCUrl
            }
        }

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context?.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
//            showToast(context, context.getString(R.string.no_activity_found))
        }
    }

    fun showFaq(context: Context) {
        var url = ApiService.faqUrl
        val selectedLanguage = CacheUtils.getSelectedLanguage()
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            if (CacheUtils.getRishtaUser().professionId == 4) {
                url = ApiService.faqUrl_serviceEngineer
            } else {
                when (selectedLanguage) {
                    "en" -> url = ApiService.faqUrl
                    "hi" -> url = ApiService.faqUrl_Hindi
                    "bn" -> url = ApiService.faqUrl_Bengali
                    "kn" -> url = ApiService.faqUrl_Kannada
                    "ml" -> url = ApiService.faqUrl_Malayalam
                    "mr" -> url = ApiService.faqUrl_Marathi
                    "ta" -> url = ApiService.fagUrl_Tamil
                    "te" -> url = ApiService.faqUrl_Telugu
                    else -> ApiService.tAndCUrl
                }
            }
        } else {
            when (selectedLanguage) {
                "en" -> url = ApiService.retFaqUrl
                "hi" -> url = ApiService.retFaqUrl
                "bn" -> url = ApiService.retFaqUrl
                "kn" -> url = ApiService.retFaqUrl
                "ml" -> url = ApiService.retFaqUrl
                "mr" -> url = ApiService.retFaqUrl
                "ta" -> url = ApiService.retFaqUrl
                "te" -> url = ApiService.retFaqUrl
                else -> ApiService.tAndCUrl
            }
        }
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context?.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
//            showToast(context, context.getString(R.string.no_activity_found))
        }
    }

    fun openPDFWithBaseUrl(context: Context, fileName: String?) {
        val url = ApiService.downLoadURL + Uri.encode(fileName)
        try {
        /*    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)*/
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url),"application/pdf")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

 /*   fun openPDFWithUserUrl(context: Context, fileName: String?) {
        var url :String? = null
        if(CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE)
        {
         url = ApiService.downLoadURL+"retimg/" + Uri.encode(fileName)}
        else {
            url = ApiService.downLoadURL + Uri.encode(fileName)
        }
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }*/

    fun showPhoneDialer(context: Context, mobileNo: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + mobileNo)
        context.startActivity(intent)
    }

    fun getSelectedLangPos(): Int {
        val selectedLanguage = CacheUtils.getSelectedLanguage()
        when (selectedLanguage) {
            "en" -> return 1
            "hi" -> return 2
            "mr" -> return 3
            "te" -> return 4
            "bn" -> return 5
            "ml" -> return 6
            "kn" -> return 7
            "ta" -> return 8

        }
        return 1
    }

    fun getSelectedLangId(): Int {
        val selectedLanguage = CacheUtils.getSelectedLanguage()
        when (selectedLanguage) {
            "en" -> return 1
            "hi" -> return 2
            "bn" -> return 3
            "kn" -> return 4
            "ml" -> return 5
            "mr" -> return 6
            "ta" -> return 7
            "te" -> return 8
        }
        return 1
    }

    fun redirectToPlayStore(context: Context, versionReadme: String) {
        val builder = AlertDialog.Builder(context)
//        builder.setTitle(context.getString(R.string.update))
        builder.setMessage(versionReadme)
        builder.setPositiveButton(context.getString(R.string.visit_playstore)) { _, _ ->
            val appPackageName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (_: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    fun getSelfieUrl(): String {
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            return ApiService.selfieUrl
        } else {
            return ApiService.retSelfieUrl
        }
    }

    fun getIdCardUrl(): String {
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            return ApiService.idCardUrl
        } else {
            return ApiService.retIdCardUrl
        }
    }

    fun getPanCardUrl(): String {
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            return ApiService.panCardUrl
        } else {
            return ApiService.panCardUrlRet
        }
    }

    fun getGSTUrl(): String {
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            return ApiService.gstPicUrl
        } else {
            return ApiService.retGSTPath
        }
    }

    fun getChequeUrl(): String {
        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            return ApiService.chequeImagePath
        } else {
            return ApiService.retChequeImagePath
        }
    }

    fun `isValidGSTNo`(str: String?): Boolean {
        // Regex to check valid
        // GST (Goods and Services Tax) number
        val regex = ("^[0-9]{2}[A-Z]{5}[0-9]{4}"
                + "[A-Z]{1}[1-9A-Z]{1}"
                + "Z[0-9A-Z]{1}$")

        // Compile the ReGex
        val p: Pattern = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (str == null) {
            return false
        }

        // Pattern class contains matcher()
        // method to find the matching
        // between the given string
        // and the regular expression.
        val m: Matcher = p.matcher(str)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }

    fun isValidPanNo(str: String?): Boolean {
        // Regex to check valid
        // GST (Goods and Services Tax) number
        val regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}"

        // Compile the ReGex
        val p: Pattern = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (str == null) {
            return false
        }

        // Pattern class contains matcher()
        // method to find the matching
        // between the given string
        // and the regular expression.
        val m: Matcher = p.matcher(str)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }


    fun isAadhaarValid(aadhaarNo: String): Boolean {
        //Removed extra characters
        val newAadhaarNo = aadhaarNo.replace("-", "", true)

        if (newAadhaarNo.length != 12
            || newAadhaarNo.toBigIntegerOrNull() == null
            || newAadhaarNo.first().toString().toInt() in 0..1
        ) {
            return false
        }

        val firstMatrix = arrayOf(
            intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
            intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
            intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
            intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
            intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
            intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
            intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
            intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
            intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
        )
        val secondMatrix = arrayOf(
            intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
            intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
            intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
            intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
            intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
            intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
            intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8)
        )

        var isValid = 0

        //Created reverse array
        val reversedIntArray = newAadhaarNo.chunked(1).map { it.toInt() }.toIntArray().reversedArray()

        //Apply Verhoeff algorithm
        for (i in reversedIntArray.indices) {
            isValid = firstMatrix[isValid][secondMatrix[i % 8][reversedIntArray[i]]]
        }
        return isValid == 0
    }
}