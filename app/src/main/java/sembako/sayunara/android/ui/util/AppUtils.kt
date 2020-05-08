package sembako.sayunara.android.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager

fun Context.checkConnection(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.activeNetworkInfo

    return !(info == null || !info.isConnected || !info.isAvailable)
}


fun Context.checkCameraPermission(): Boolean {
    val permission = Manifest.permission.CAMERA

    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.checkWriteExStoragePermission(): Boolean {
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.checkReadExtStoragePermission(): Boolean {
    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.checkPermissionFineLocation(): Boolean {
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.checkPermissionCoarseLocation(): Boolean {
    val permission = Manifest.permission.ACCESS_COARSE_LOCATION
    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Context.checkPermissionAudio(): Boolean {
    val permission = Manifest.permission.RECORD_AUDIO
    val res = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}


















