package sembako.sayunara.android.helper

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import sembako.sayunara.android.R


class CostumeEditText : AppCompatEditText {
    private var mContext: Context
    private var isRequired: Boolean = false
    private var minLength: Int = 0
    private var validationType: Int = 0

    constructor(context: Context) : super(context) {
        mContext = context
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        mContext = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val attribute = mContext.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CostumeEditText,
                0,
                0
        )

        try {
            isRequired = attribute.getBoolean(R.styleable.CostumeEditText_isRequired, false)
            minLength = if (attribute.getString(R.styleable.CostumeEditText_minLength) != null) {
                attribute.getString(R.styleable.CostumeEditText_minLength)!!.toInt()
            } else {
                0
            }

            validationType = attribute.getInt(R.styleable.CostumeEditText_validationType, 0)
        } finally {
            attribute.recycle()
        }


    }
}

