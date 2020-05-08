package sembako.sayunara.android.helper.blur

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.LruCache
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import sembako.sayunara.android.helper.blur.Blur.apply

class BlurBehind {
    private var mAlpha = CONSTANT_DEFAULT_ALPHA
    private var mFilterColor = -1

    private enum class State {
        READY, EXECUTING
    }

    private var mState = State.READY
    fun execute(activity: AppCompatActivity?, onBlurCompleteListener: OnBlurCompleteListener?) {
        if (mState == State.READY) {
            mState = State.EXECUTING
            cacheBlurBehindAndExecuteTask = CacheBlurBehindAndExecuteTask(activity, onBlurCompleteListener!!)
            cacheBlurBehindAndExecuteTask!!.execute()
        }
    }

    fun withAlpha(alpha: Int): BlurBehind {
        mAlpha = alpha
        return this
    }

    fun withFilterColor(filterColor: Int): BlurBehind {
        mFilterColor = filterColor
        return this
    }

    fun setBackground(activity: AppCompatActivity) {
        if (mImageCache.size() != 0) {
            val bd = BitmapDrawable(activity.resources, mImageCache[KEY_CACHE_BLURRED_BACKGROUND_IMAGE])
            bd.alpha = mAlpha
            if (mFilterColor != -1) {
                bd.setColorFilter(mFilterColor, PorterDuff.Mode.DST_ATOP)
            }
            activity.window.setBackgroundDrawable(bd)
            mImageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE)
            cacheBlurBehindAndExecuteTask = null
        }
    }

    private inner class CacheBlurBehindAndExecuteTask(private var activity: AppCompatActivity?, private val onBlurCompleteListener: OnBlurCompleteListener) : AsyncTask<Void?, Void?, Void?>() {
        private var decorView: View? = null
        private var image: Bitmap? = null
        override fun onPreExecute() {
            super.onPreExecute()
            decorView = activity!!.window.decorView
            decorView!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
            decorView!!.isDrawingCacheEnabled = true
            decorView!!.buildDrawingCache()
            image = decorView!!.drawingCache
        }


        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            decorView!!.destroyDrawingCache()
            decorView!!.isDrawingCacheEnabled = false
            activity = null
            onBlurCompleteListener.onBlurComplete()
            mState = State.READY
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val blurredBitmap = apply(activity, image!!, CONSTANT_BLUR_RADIUS)
            mImageCache.put(KEY_CACHE_BLURRED_BACKGROUND_IMAGE, blurredBitmap)
            return null
        }

    }


    fun getInstance(): BlurBehind? {
        if (mInstance == null) {
            mInstance = BlurBehind()
        }
        return mInstance
    }
    companion object {
        private const val KEY_CACHE_BLURRED_BACKGROUND_IMAGE = "KEY_CACHE_BLURRED_BACKGROUND_IMAGE"
        private const val CONSTANT_BLUR_RADIUS = 12
        private const val CONSTANT_DEFAULT_ALPHA = 100
        private val mImageCache = LruCache<String, Bitmap?>(1)
        private var cacheBlurBehindAndExecuteTask: CacheBlurBehindAndExecuteTask? = null
        private var mInstance: BlurBehind? = null
        val instance: BlurBehind?
            get() {
                if (mInstance == null) {
                    mInstance = BlurBehind()
                }
                return mInstance
            }
    }
}