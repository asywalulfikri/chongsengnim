package sembako.sayunara.android.ui.component.account.pin

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_insert_email.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity




class ForgotPassword1Activity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_email)

        setupToolbar(toolbar, getString(R.string.text_verification))

       /* FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }*/

        btnSubmit.setOnClickListener {

        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}