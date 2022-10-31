package com.example.background.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        val message = "Image is blurring..."
        makeStatusNotification(message, appContext)

        sleep()

        try {

            if (TextUtils.isEmpty(resourceUri)){
                throw IllegalArgumentException("invalid URI input")
            }
            val picture = BitmapFactory.decodeStream(
                appContext.contentResolver.openInputStream(Uri.parse(resourceUri)))

            val bitmap = blurBitmap(
                picture,
                appContext)

            val bitmapUri = writeBitmapToFile(
                appContext,
                bitmap)

            val outputUri = workDataOf(KEY_IMAGE_URI to bitmapUri.toString())

            makeStatusNotification(bitmapUri.toString(), appContext)

            return Result.success(outputUri)
        }catch (e: java.lang.Exception){
            Log.e(TAG, "doWork: Error while blurring image", )
            return Result.failure()
        }
    }
}