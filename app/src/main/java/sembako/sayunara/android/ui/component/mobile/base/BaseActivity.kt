package sembako.sayunara.android.ui.component.mobile.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import sembako.sayunara.android.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}

@SuppressLint("ResourceAsColor")
fun setToolbar(toolbar: Toolbar) {
    toolbar.setTitleTextColor(Color.parseColor("#689F38"))
    toolbar.setTitleTextColor(Color.parseColor("#689F38"))
    toolbar.setTitle(R.string.app_name)
}