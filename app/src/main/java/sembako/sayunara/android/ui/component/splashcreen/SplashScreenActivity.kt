package sembako.sayunara.android.ui.component.splashcreen

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.main.MainMenuActivity
import sembako.sayunara.android.ui.util.*

class SplashScreenActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            checkLocationPermission()
        }, 1500)
    }

    private fun toDashboard() {
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    }

    private fun checkLocationPermission() {
        if (checkCameraPermission() && checkWriteExStoragePermission() && checkReadExtStoragePermission() && checkPermissionFineLocation() && checkPermissionCoarseLocation()) {
            toDashboard()
        } else {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                toDashboard()
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                // permission is denied permanently, navigate user to app settings
                                showSettingsDialog()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    })
                    .withErrorListener {
                        //TODO change text
                        Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
                    }
                    .onSameThread()
                    .check()
        }
    }
}