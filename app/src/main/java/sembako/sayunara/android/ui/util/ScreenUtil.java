package sembako.sayunara.android.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

@SuppressLint("SdCardPath")
public class ScreenUtil {
	private static int sScreenWidthDP = -1;
	private static int sScreenWidth = -1;
	private static int sScreenHeight = -1;

	public static int getScreenHeight(Context context) {
		if (sScreenHeight == -1) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sScreenHeight = size.y;
		}
		return sScreenHeight;
	}

	public static int getScreenHeightInDp(Context context) {
		if (sScreenHeight == -1) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sScreenHeight = size.y;
		}
		return pixelsToDp(context, sScreenHeight);
	}

	public static int getScreenWidthInDp(Context context) {
		if (sScreenWidthDP == -1) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sScreenWidthDP = pixelsToDp(context, size.x);
		}
		return sScreenWidthDP;
	}

	public static int getScreenWidth(Context context) {
		if (sScreenWidth == -1) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			sScreenWidth = size.x;
		}

		return sScreenWidth;
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static float dpToPixels(Context context, int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public static int pixelsToDp(Context context, float pixels) {
		float density = context.getResources().getDisplayMetrics().densityDpi;
		return Math.round(pixels / (density / 160f));
	}
}
