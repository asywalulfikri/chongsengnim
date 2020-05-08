package sembako.sayunara.android

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import sembako.sayunara.android.ui.util.storage.TinyDB

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        tinyDB = TinyDB(application)
        instance = this
        Fabric.with(this, Crashlytics())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        var application: Application? = null
            private set
        var tinyDB: TinyDB? = null
            private set

        @get:Synchronized
        var instance: App? = null
            protected set

        val app: App?
            get() = if (instance != null) {
                instance
            } else {
                instance = App()
                instance!!.onCreate()
                instance
            }

    }
}