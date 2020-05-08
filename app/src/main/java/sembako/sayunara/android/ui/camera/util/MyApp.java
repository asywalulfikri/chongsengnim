package sembako.sayunara.android.ui.camera.util;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.util.DisplayMetrics;

//import sembako.sayunara.android.dagger.PresentationComponent;
//import sembako.sayunara.android.dagger.PresentationModule;

public class MyApp extends Application {
	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */


//	private static final String TAG = "MyApp";

	public static int GENERAL_TRACKER = 0;

	static boolean calledAlready = false;
//	PresentationComponent appComponent;
	protected static MyApp       mInstance;
	private DisplayMetrics displayMetrics = null;


	public static MyApp getApp() {
		if (mInstance != null && mInstance instanceof MyApp) {
			return mInstance;
		} else {
			mInstance = new MyApp();
			mInstance.onCreate();
			return mInstance;
		}
	}

	public MyApp() {
		mInstance = this;
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {

		// Determine which lifecycle or system event was raised.
		switch (level) {

			case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

				break;

			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
			case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

				break;

			case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
			case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
			case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

				break;

			default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
				break;
		}

		super.onTrimMemory(level);
	}


	public float getScreenDensity() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.density;
	}

	public int getScreenHeight() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.widthPixels;
	}

	public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
		this.displayMetrics = DisplayMetrics;
	}

	public int dp2px(float f)
	{
		return (int)(0.5F + f * getScreenDensity());
	}

	public int px2dp(float pxValue) {
		return (int) (pxValue / getScreenDensity() + 0.5f);
	}

	//获取应用的data/data/....File目录
	public String getFilesDirPath() {
		return getFilesDir().getAbsolutePath();
	}

	//获取应用的data/data/....Cache目录
	public String getCacheDirPath() {
		return getCacheDir().getAbsolutePath();
	}


}
