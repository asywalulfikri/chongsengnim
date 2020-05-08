package sembako.sayunara.android.ui.component.account.login.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login_first.*
import sembako.sayunara.android.R
import sembako.sayunara.android.helper.blur.BlurBehind.Companion.instance
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.register.RegisterActivity

class LoginFirstActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_first)
        instance!!.withAlpha(80)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this)


        layout.setOnClickListener {
            finish()
        }

        tvLogin.setOnClickListener {
            val intentY = Intent(activity, LoginActivity::class.java)
            startActivity(intentY)
            finish()
        }
        tvRegister.setOnClickListener {
            val intentY = Intent(activity, RegisterActivity::class.java)
            startActivity(intentY)
            finish()
        }
        ivClose.setOnClickListener { finish() }
    }
}