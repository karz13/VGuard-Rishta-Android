package com.tfl.vguardrishta.utils

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.remote.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Shanmuka on 3/29/2019.
 */
object FileUtils {

    const val idCardPhotoFront: Int = 1
    const val selfie: Int = 2
    const val shopPhoto: Int = 3
    const val chequeBookPhoto: Int = 4
    const val uploadOrder: Int = 5
    const val uploadInvoice: Int = 6
    const val uploadContestFirstPhoto: Int = 7
    const val uploadContestSecondPhoto: Int = 8
    const val idCardPhotoBack: Int = 9
    const val panCardFront: Int = 10
    const val panCardBack: Int = 11
    const val issueType: Int = 12
    const val errorCouponPhoto: Int = 13
    const val warrantyPhoto: Int = 14
    const val billDetails: Int = 15
    const val GSTNo: Int = 16

    fun setToFileUploader(
        requestCode: Int,
        compressImage: String
    ) {
        val fileUploader = CacheUtils.getFileUploader()
        val file = File(compressImage)
        val part = setUploader(file)
        when (requestCode) {
            idCardPhotoFront -> {
                fileUploader.setIdProofFront(part)
                fileUploader.setIdProofFile(file)
            }

            idCardPhotoBack -> {
                fileUploader.setIdProofBack(part)
                fileUploader.setIdProofBackFile(file)
            }

            panCardFront -> {
                fileUploader.setPanCardFront(part)
                fileUploader.setPanCardFrontFile(file)
            }
            panCardBack -> {
                fileUploader.setPanCardBack(part)
                fileUploader.setPanCardBackFile(file)
            }

            selfie -> {
                fileUploader.setUserPhoto(part)
                fileUploader.setUserPhotoFile(file)
            }
            shopPhoto -> {
                fileUploader.setShopPhoto(part)
                fileUploader.setShopPhotoFile(file)
            }
            chequeBookPhoto -> {
                fileUploader.setChequeBook(part)
                fileUploader.setChequeBookPhotoFile(file)
            }
            uploadOrder -> {
                fileUploader.setUploadOrder(part)
                fileUploader.setUploadOrderFile(file)
            }
            uploadInvoice -> {
                fileUploader.setInvoicePhotoFile(file)
            }
            uploadContestFirstPhoto -> {
                fileUploader.setContestFirstPhotoFile(file)
            }

            uploadContestSecondPhoto -> {
                fileUploader.setContestSecondPhotoFile(file)
            }

            issueType -> {
                fileUploader.setIssuePhoto(part)
                fileUploader.setIssuePhotoFile(file)
            }
            errorCouponPhoto -> {
                fileUploader.setErrorCouponFile(file)
            }
            billDetails -> {
                fileUploader.setBillDetailsFile(file)
            }
            warrantyPhoto -> {
                fileUploader.setWarrantyPhotoFile(file)
            } GSTNo -> {
                fileUploader.setGSTPhotoFile(file)
            }
        }

        CacheUtils.setFileUploader(fileUploader)
    }

    private fun setUploader(file: File): MultipartBody.Part {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    fun sendImage(file: File?, string: String, userType: String): String? {
        val uri = ""
        try {
            val requestURL = ApiService.prodUrl + "file"
            val params = HashMap<String, String>(2)
            params[Constants.IMAGE_RELATED] = string
            val rishtaUser = CacheUtils.getRishtaUser()
            val x  = rishtaUser.roleId
            params[Constants.USER_ROLE] = x!!
           // params[Constants.USER_ROLE] = CacheUtils.getRishtaUserType()
            val result = ImageSendUtil().multipartRequest(
                requestURL,
                params,
                file!!.path,
                "file",
                "file/jpg"
            )
            if (!result.isNullOrEmpty()) {
                val status =
                    Gson().fromJson(result, com.tfl.vguardrishta.models.Status::class.java)
                if (status != null && status.code == 200) {
                    if (!status.entityUid.isNullOrEmpty()) {
                        return status.entityUid
                    }
                }
            }

        } catch (e: IOException) {
            return uri
        } catch (e: Exception) {
            return uri
        }
        return uri
    }

    fun sendImageFromOutSide(file: File?, string: String, userType: String): String? {
        val uri = ""
        try {
            val requestURL = ApiService.prodUrl + "file"
            val params = HashMap<String, String>(2)
            params[Constants.IMAGE_RELATED] = string
            params[Constants.USER_ROLE] = CacheUtils.getRishtaUserType()
            val result = ImageSendUtil().multipartRequest(
                requestURL,
                params,
                file!!.path,
                "file",
                "file/jpg"
            )
            if (!result.isNullOrEmpty()) {
                val status =
                    Gson().fromJson(result, com.tfl.vguardrishta.models.Status::class.java)
                if (status != null && status.code == 200) {
                    if (!status.entityUid.isNullOrEmpty()) {
                        return status.entityUid
                    }
                }
            }

        } catch (e: IOException) {
            return uri
        } catch (e: Exception) {
            return uri
        }
        return uri
    }


    fun compressImage(context: Context, imageUri: String): String {
        Log.d("compress-image", imageUri)
        val filePath = getRealPathFromURI(context, imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            val stream = File(filePath!!).inputStream()
            BitmapFactory.decodeStream(stream, null, options);
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            // do something
        }
        //  var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        // try {
        val bmp = BitmapFactory.decodeFile(filePath, options)
        //  } catch (exception: OutOfMemoryError) {
        //      exception.printStackTrace()
        //  }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath ?: "")

            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90F)
                3 -> matrix.postRotate(180F)
                8 -> matrix.postRotate(270F)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val out: FileOutputStream?
        val filename = getFilename(context)
        try {
            out = FileOutputStream(filename)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename
    }

    fun getFilePath(image: Bitmap, context: Context): String {
        Log.d("compress-image", "getFilePath")

        val fileName = getFilename(context)
        try {
            val outputStream = FileOutputStream(fileName)
            var boolean = image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush()
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return fileName
    }

    fun getFilename(context: Context): String {
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path, "vGuard")
        try {
            if (!file.exists()) {
                file.mkdirs()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun getRealPathFromURI(context: Context, contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    fun zoomImage(context: Context, photoURL: String) {
        val dialog = Dialog(context)
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dilog_image_zoom)
        val ivOrderImage = dialog.findViewById(R.id.ivOrderImage) as TouchImageView
        Glide.with(context).load(photoURL).placeholder(R.drawable.no_image).into(ivOrderImage)
        dialog.show()
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
    }

    fun zoomFileImage(context: Context, file: File?) {
        val dialog = Dialog(context)
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dilog_image_zoom)
        val ivOrderImage = dialog.findViewById(R.id.ivOrderImage) as TouchImageView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ivOrderImage.setImageURI(Uri.parse(file.toString()));
        } else {
            ivOrderImage.setImageURI(Uri.fromFile(file));
        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
    }

}