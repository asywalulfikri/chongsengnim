package sembako.sayunara.android.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import sembako.sayunara.android.R

class SizeAwareTextView: AppCompatTextView {

    private var lastTextSize: Float = 0F
    private var viewRefs: TypedArray? = null
    private var views = mutableListOf<SizeAwareTextView>()
    var onTextSizeChangedListener: OnTextSizeChangedListener? = object : OnTextSizeChangedListener {
        @SuppressLint("RestrictedApi")
        override fun onTextSizeChanged(view: SizeAwareTextView, textSize: Float) {
            resolveViews()
            views.forEach {
                if (view != it && view.textSize != it.textSize) {
                    it.setAutoSizeTextTypeUniformWithPresetSizes(intArrayOf(textSize.toInt()), TypedValue.COMPLEX_UNIT_PX)
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
        lastTextSize = textSize
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        lastTextSize = textSize
        val a = context.obtainStyledAttributes(attrs, R.styleable.SizeAwareTextView)
        a.getResourceId(R.styleable.SizeAwareTextView_group, 0).let {
            if (it > 0) {
                viewRefs = resources.obtainTypedArray(it)
            }
        }
        a.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lastTextSize = textSize
        val a = context.obtainStyledAttributes(attrs, R.styleable.SizeAwareTextView)
        a.getResourceId(R.styleable.SizeAwareTextView_group, 0).let {
            if (it > 0) {
                viewRefs = resources.obtainTypedArray(it)
            }
        }
        a.recycle()
    }

    fun resolveViews() {
        viewRefs?.let {
            var root = parent
            while (root.parent is View) {
                root = root.parent
            }
            for (i in 0 until it.length()) {
                val resId = it.getResourceId(i, 0)
                val v = (root as View).findViewById<SizeAwareTextView>(resId)
                if (v != null) {
                    views.add(v)
                } else {
                    Log.w(TAG, "Resource: $resId not found at idx: $i")
                }
            }
            it.recycle()
            viewRefs = null
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (lastTextSize != textSize) {
            lastTextSize = textSize
            onTextSizeChangedListener?.onTextSizeChanged(this, lastTextSize)
        }
    }

    interface OnTextSizeChangedListener {
        fun onTextSizeChanged(view: SizeAwareTextView, textSize: Float)
    }

    companion object {
        val TAG = SizeAwareTextView::class.java.simpleName
    }
}