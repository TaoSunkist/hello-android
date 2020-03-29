package com.zhimeng.battery.utilities

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import io.reactivex.Observable
import java.io.*

object ImageUtils {

    private val sCanvas = Canvas()

    fun createBitmapFromView(view: View): Bitmap {
        if (view is ImageView) {
            val drawable = view.drawable
            if (drawable != null && drawable is BitmapDrawable) {
                return drawable.bitmap
            }
        }
        view.clearFocus()
        val bitmap = createBitmapSafely(view.width, view.height, Bitmap.Config.ARGB_8888, 1)
        synchronized(sCanvas) {
            val canvas = sCanvas
            canvas.setBitmap(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
        }
        return bitmap
    }

    private fun createBitmapSafely(
        width: Int,
        height: Int,
        config: Bitmap.Config,
        retryCount: Int
    ): Bitmap {
        lateinit var bitmap: Bitmap
        try {
            bitmap = Bitmap.createBitmap(width, height, config)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            if (retryCount > 0) {
                System.gc()
                return createBitmapSafely(width, height, config, retryCount - 1)
            }
        }
        return bitmap
    }

    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convert(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }


    /* 图片过大时, rx异步处理 */
    fun compressImageAsync(
        imageFile: File,
        targetWidth: Float,
        targetHeight: Float,
        compressFormat: Bitmap.CompressFormat,
        quality: Int,
        destinationPath: String
    ): Observable<File> {
        return Observable.create {

            var fileOutputStream: FileOutputStream? = null
            val file = File(destinationPath).parentFile
            if (!file.exists()) {
                file.mkdirs()
            }

            try {
                fileOutputStream = FileOutputStream(destinationPath)
                decodeSampledBitmapFromFile(imageFile, targetWidth, targetHeight)!!.compress(
                    compressFormat,
                    quality,
                    fileOutputStream
                )
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush()
                    fileOutputStream.close()
                }
            }
            val destinationFile = File(destinationPath)
            if (!destinationFile.exists()) {
                it.onError(FileNotFoundException("file not found"))
            } else {
                it.onNext(destinationFile)
            }
        }
    }

    private fun decodeSampledBitmapFromFile(
        imageFile: File,
        reqWidth: Float,
        reqHeight: Float
    ): Bitmap? {

        var scaledBitmap: Bitmap? = null
        var bmp: Bitmap?

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        bmp = BitmapFactory.decodeFile(imageFile.path, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = reqWidth / reqHeight

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            /* If Height is greater */
            when {
                imgRatio < maxRatio -> {
                    imgRatio = reqHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = reqHeight.toInt()

                }  /* If Width is greater */
                imgRatio > maxRatio -> {
                    imgRatio = reqWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = reqWidth.toInt()
                }
                else -> {
                    actualHeight = reqHeight.toInt()
                    actualWidth = reqWidth.toInt()
                }
            }
        }

        /* Calculate inSampleSize */
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
//        options.inDither = false /* android.os.Build.VERSION_CODES#N ignore */
//        options.inPurgeable = true /* android.os.Build.VERSION_CODES#LOLLIPOP ignore */
//        options.inInputShareable = true /* android.os.Build.VERSION_CODES#LOLLIPOP ignore */
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            bmp = BitmapFactory.decodeFile(imageFile.absolutePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

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
            bmp!!,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        bmp.recycle()
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0, scaledBitmap.width,
                scaledBitmap.height, matrix, true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return scaledBitmap
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
            inSampleSize *= 2
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun addImageToGallery(filePath: String, context: Context, mimeType: String) {
        val values = ContentValues()
        if (Build.VERSION.SDK_INT >= 29) {
            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
        values.put(Images.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.MediaColumns.DATA, filePath)
        context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun generateBitmapFromText(context: Context, text: String): Bitmap {
        val paint = TextPaint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.alpha = 255

        val value = 20f
        val pixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).toInt()
        paint.textSize = pixel.toFloat()
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.LEFT
        paint.strokeWidth = 5f

        val baseline = (-paint.ascent() + 1f).toInt().toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        var boundWidth = bounds.width() + 20
        val textMaxWidth = paint.measureText(text).toInt()
        if (boundWidth > textMaxWidth) {
            boundWidth = textMaxWidth
        }
//        StaticLayout.Builder.obtain(
//                text, 0,
//                text.length, paint,
//                textMaxWidth
//        ).build()

        val staticLayout = StaticLayout(
            text,
            0, text.length,
            paint, textMaxWidth, Layout.Alignment.ALIGN_NORMAL, 2.0f,
            2.0f, false
        )

        val lineCount = staticLayout.lineCount
        val height = (baseline + paint.descent() + 3f).toInt() * lineCount
        var image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        if (boundWidth > 0 && height > 0) {
            image = Bitmap.createBitmap(boundWidth, height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(image)
        canvas.drawColor(Color.TRANSPARENT)
        staticLayout.draw(canvas)
        return image
    }

    fun addWatermark(
        context: Context,
        drawableResId: Int,
        srcBitmap: Bitmap
    ): Bitmap {
        val w = srcBitmap.width
        val h = srcBitmap.height
        val result = Bitmap.createBitmap(w, h, srcBitmap.config)
        val canvas = Canvas(result).also { it.drawBitmap(srcBitmap, 0f, 0f, null) }
        val watermarkPadding = w / 30f
        val waterMark = BitmapFactory.decodeResource(context.resources, drawableResId)
        val newWatermarkWidth = w / 6
        val newWatermarkHeight = waterMark.height * newWatermarkWidth / waterMark.width
        val newWatermark =
            Bitmap.createScaledBitmap(waterMark, newWatermarkWidth, newWatermarkHeight, true)

        canvas.drawBitmap(
            newWatermark,
            watermarkPadding,
            watermarkPadding, null
        )

        return result
    }
}
