package sembako.sayunara.android.ui.component.mainmenu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    private var disable: Boolean? = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.disable!!) false else super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.disable!!) false else super.onTouchEvent(event)
    }

    fun disableScroll(disable: Boolean?) {
        //When disable = true not work the scroll and when disble = false work the scroll
        this.disable = disable
    }
}
