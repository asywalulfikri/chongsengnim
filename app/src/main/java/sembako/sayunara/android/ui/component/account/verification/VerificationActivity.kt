package sembako.sayunara.android.ui.component.account.verification

import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_verification_email.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener




class VerificationActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_email)

        setupToolbar(toolbar, getString(R.string.text_verification))
        btnSubmit.setOnClickListener {
          sendEmailVerification()
        }

       /* FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }*/

    }

    private fun sendEmailVerification() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        setToast(getString(R.string.text_email_send_success_to)+ " "+user.email)
                        onBackPressed()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    else {
                        setToast(getString(R.string.text_email_send_failed_to)+ " "+ user.email)
                        setLog("email failed", task.exception!!.message!!)
                    }
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