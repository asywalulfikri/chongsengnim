package sembako.sayunara.constant

import sembako.sayunara.android.BuildConfig

class valueApp {


    interface AppInfo{
        companion object {
            const val apkType = "seller"
            const val versionCode = BuildConfig.VERSION_CODE
            const val applicationId = "sembako.sayunara.seller"

        }
    }

}