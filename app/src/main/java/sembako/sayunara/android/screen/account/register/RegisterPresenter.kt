package sembako.sayunara.android.screen.account.register

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.util.Log
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.MultiTextWatcher
import sembako.sayunara.android.helper.OnTextWatcher
import sembako.sayunara.android.screen.base.BasePresenter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RegisterPresenter : BasePresenter<RegisterContract.SignUpView>(),RegisterContract.PostActionListener ,OnTextWatcher{

    private var messageError = 0
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseFirestore = FirebaseFirestore.getInstance()


    override fun checkData() {
        if(validation()){
            registerUser()
        }else{
            view?.showErrorValidation(messageError)
        }

    }

    private fun validation(): Boolean {
        val i: Boolean
        var emailValidator = EmailValidator()
        val mUserName = view?.mEtUserName?.text.toString().trim()
        val mEmail = view?.mEtEmail?.text.toString().trim()
        val mPassword = view?.mEtPassword?.text.toString().trim()
        val mConfirmPassword = view?.mEtConfirmPassword?.text.toString().trim()
        val mPhoneNumber = view?.mEtPhoneNumber?.text.toString().trim()

        if (mUserName.length>3 && mUserName.isNotEmpty()
                && mEmail.isNotEmpty()&& emailValidator.isValid(mEmail)
                && mPassword.isNotEmpty()&&mPassword.length>5
                && mConfirmPassword.isNotEmpty()
                &&mPhoneNumber.isNotEmpty()&&mPhoneNumber.length>=7) {
            i = true
            view?.setColorButton(R.color.color_btn_enable)

        } else {
            view?.setColorButton(R.color.color_btn_disable)
            i = false
            when {

                mUserName.isEmpty() -> messageError = R.string.text_user_name_empty
                mUserName.length<3-> messageError = R.string.text_user_name_to_short
                mEmail.isEmpty() -> messageError = R.string.text_email_empty
                mPassword.isEmpty() -> messageError = R.string.text_password_empty
                mPassword.length<6 -> messageError = R.string.text_password_to_short
                mConfirmPassword.isEmpty() -> messageError = R.string.text_confirm_password_empty
                mPhoneNumber.isEmpty() -> messageError = R.string.text_phone_number_empty
                mPhoneNumber.length<7 -> messageError = R.string.text_phone_number_to_short
                mPassword != mConfirmPassword -> messageError = R.string.text_password_not_same
                !emailValidator.isValid(mEmail) -> messageError= R.string.text_email_invalid

            }
        }

        return i

    }

    @SuppressLint("SimpleDateFormat")
    override fun registerUser() {
        firebaseAuth.createUserWithEmailAndPassword(view!!.mEtEmail.text.toString().trim(), view!!.mEtPassword.text.toString().trim()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid.toString()
                val storeId = UUID.randomUUID().toString()
                val documentReference: DocumentReference = firebaseFirestore.collection("users").document(userId)
                val user: MutableMap<String, Any> = HashMap()

                user[Constant.USER_KEY.username] = view!!.mEtUserName.text.toString().trim()
                user[Constant.USER_KEY.email] = view!!.mEtEmail.text.toString().trim()
                user[Constant.USER_KEY.avatar] = Constant.DEFAULT_AVATAR
                user[Constant.USER_KEY.isActive] = true
                user[Constant.USER_KEY.isVerfied] = false
                user["phoneNumber"] = view!!.mEtPhoneNumber.text.toString().trim()
                user["type"] = "user"
                user["userId"] = userId
                user["storeId"] = "StoreId-$storeId"
                user["isPartner"] = false

                val information = (Build.MANUFACTURER
                        + " " + Build.MODEL + " , Version OS :" + Build.VERSION.RELEASE)
                val phone: MutableMap<String, Any?> = HashMap()
                phone["appVersion"] = BuildConfig.VERSION_NAME
                phone["androidVersion"] = Build.VERSION.SDK_INT.toString()
                phone["codeVersion"] = BuildConfig.VERSION_CODE
                phone["devicesId"] =  view?.mDevicesId
                phone["devicesDetail"] = information
                user["devices"] = phone

                val coordinate: MutableMap<String, String?> = HashMap()
                coordinate["latitude"] = view?.mLatitude
                coordinate["longitude"] = view?.mLongitude
                user["coordinate"] = coordinate


                val tsLong = System.currentTimeMillis() / 1000
                val tz = TimeZone.getTimeZone("UTC")
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
                df.timeZone = tz
                val nowAsISO = df.format(Date())
                val datePublishedData: MutableMap<String, Any> = HashMap()
                datePublishedData["iso"] = nowAsISO
                datePublishedData["timestamp"] = tsLong
                user["createdAt"] = datePublishedData
                val dateSubmittedData: MutableMap<String, Any> = HashMap()
                dateSubmittedData["iso"] = nowAsISO
                dateSubmittedData["timestamp"] = tsLong
                user["updatedAt"] = dateSubmittedData
                documentReference.set(user).addOnSuccessListener { Log.d("response", "onSuccess: user Profile is created for $userId") }.addOnFailureListener { e -> Log.d("response", "onFailure: $e") }

                onRequestSuccess()
            } else {
                onRequestFailed(task.exception?.message,task.exception.hashCode())
            }
        }
    }

    fun checkEditText(){
        setTextWatcher(view!!.mEtUserName)
        setTextWatcher(view!!.mEtEmail)
        setTextWatcher(view!!.mEtPhoneNumber)
        setTextWatcher(view!!.mEtPassword)
        setTextWatcher(view!!.mEtConfirmPassword)
    }


    private fun setTextWatcher(editText: EditText){

        MultiTextWatcher()
                .setEditText(editText)
                .setOnTextWatcher(this)
    }
    override fun onRequestSuccess() {
        //view?.onBack()
    }

    override fun onRequestFailed(message:String?,error: Int) {
       Log.d("erornya",message)
    }


    override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(editText: EditText?, editable: Editable?) {
        validation()
    }

}