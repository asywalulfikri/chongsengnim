package sembako.sayunara.android.helper

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import sembako.sayunara.android.R


class Preview

private constructor(context: Context, themeResId: Int = R.style.Preview_Dialog) :
    Dialog(context, themeResId) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dismiss()
                return true
            }
            else -> {}
        }
        return super.onTouchEvent(event)
    }


    override fun show() {
        super.show()
    }

    override fun dismiss() {
        super.dismiss()
    }


    class Builder(private val mContext: Context) {
        private val preview: Preview = Preview(mContext)
        private var mColor = 0x666666

        fun setContentView(@LayoutRes layoutResID: Int): Builder {
            preview.setContentView(layoutResID)
            return this
        }

        fun setContentView(view: View): Builder {
            preview.setContentView(view)
            return this
        }

        fun setBackground(@ColorInt colorResId: Int): Builder {
            mColor = colorResId
            return this
        }

        fun build(): Preview {
            val colorDrawable = ColorDrawable(mColor)
            val window = preview.window
            window?.setBackgroundDrawable(colorDrawable)
            return preview
        }
    }
}