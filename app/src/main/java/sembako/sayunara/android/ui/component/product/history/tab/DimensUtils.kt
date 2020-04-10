package sembako.sayunara.android.ui.component.product.history.tab

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt


object DimensUtils {

    fun spToPx(context: Context, sps: Int): Int {
        return (context.resources.displayMetrics.scaledDensity * sps).roundToInt()
    }

    fun dpToPx(context: Context, dps: Int): Int {
        return (context.resources.displayMetrics.density * dps).roundToInt()
    }

    fun spToPx(context: Context, sps: Float): Float {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sps, dm)
    }

    fun dpToPx(context: Context, dps: Float): Float {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, dm)
    }
}