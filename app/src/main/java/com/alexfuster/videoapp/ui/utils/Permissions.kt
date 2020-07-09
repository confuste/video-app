package com.alexfuster.videoapp.ui.utils

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


fun getRecorderPermissions(context: Context,
                           onPermissionsCheckedListener: ()-> Unit,
                           onPermissionDeniedListener: ()-> Unit) {
    Dexter.withContext(context)
        .withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                if(report.areAllPermissionsGranted())
                    onPermissionsCheckedListener()
                else
                    onPermissionDeniedListener()
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?,
                token: PermissionToken?) {
                token?.continuePermissionRequest()
            }
        }).check()
}

fun isCameraPermissionsGranted(context: Context): Boolean {
    return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
}

fun getGalleryPermissions(context: Context,
                          onPermissionGrantedListener: ()-> Unit,
                          onPermissionDeniedListener: ()-> Unit) {


    Dexter.withContext(context)
        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                onPermissionGrantedListener()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                onPermissionDeniedListener()
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
}

