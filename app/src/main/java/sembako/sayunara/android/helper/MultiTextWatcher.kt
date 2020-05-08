package sembako.sayunara.android.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MultiTextWatcher : TextWatcher {
    private var onTextWatcher: OnTextWatcher? = null
    private var editText: EditText? = null
    fun setOnTextWatcher(onTextWatcher: OnTextWatcher?): MultiTextWatcher {
        this.onTextWatcher = onTextWatcher
        return this
    }

    fun setEditText(editText: EditText?): MultiTextWatcher {
        this.editText = editText
        this.editText!!.addTextChangedListener(this)
        return this
    }

    /**
     * This method is called to notify you that, within `s`,
     * the `count` characters beginning at `start`
     * are about to be replaced by new text with length `after`.
     * It is an error to attempt to make changes to `s` from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        onTextWatcher!!.beforeTextChanged(editText, s, start, count, after)
    }

    /**
     * This method is called to notify you that, within `s`,
     * the `count` characters beginning at `start`
     * have just replaced old text that had length `before`.
     * It is an error to attempt to make changes to `s` from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onTextWatcher!!.onTextChanged(editText, s, start, before, count)
    }

    /**
     * This method is called to notify you that, somewhere within
     * `s`, the text has been changed.
     * It is legitimate to make further changes to `s` from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use [-Spannable setSpan] in [.onTextChanged]
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param editable
     */
    override fun afterTextChanged(editable: Editable) {
        onTextWatcher!!.afterTextChanged(editText, editable)
    }
}