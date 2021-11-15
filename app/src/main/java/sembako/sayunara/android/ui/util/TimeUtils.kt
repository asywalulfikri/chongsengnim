package sembako.sayunara.android.ui.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {

    @SuppressLint("SimpleDateFormat")
    fun formatDateToIndonesia(dateTime: String?): String {
        val originalFormat = "yyyy-MM-dd'T'HH:mm'Z'"
        val fmt = SimpleDateFormat(originalFormat)
        var date: Date? = null
        try {
            date = fmt.parse(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val format = "dd MMMM yyyy"
        val fmtOut = SimpleDateFormat(format, Locale("ID"))
        return fmtOut.format(date)
    }
}