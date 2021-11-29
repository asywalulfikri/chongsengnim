package sembako.sayunara.constant

import sembako.sayunara.android.BuildConfig

class valueApp {


    interface AppInfo{
        companion object {
            const val apkType = "customer"
            const val versionCode = 1
            const val applicationId = "sembako.sayunara.android"
            const val channelId = "sayunara"
            const val versionName = BuildConfig.VERSION_NAME
        }
    }

}