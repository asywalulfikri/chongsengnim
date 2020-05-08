package sembako.sayunara.android.ui.component.verification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.base.BaseActivity;

public class VerificationActivity extends BaseActivity {

    protected EditText et_first_name, et_last_name, et_email, et_phone_number, et_password, et_confirm_password;
    protected AppCompatButton btn_register;
    protected TextView tv_login;
    protected Toolbar toolbar;
    protected String firstname, lastname, email, phonenumber, password, confirmpassword, username;
    protected String otp="";
    protected FirebaseFirestore firebaseFirestore;

    //signup phonenumber
    protected FirebaseAuth mAuth;
    private ValueEventListener valueEvent;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("ListPhone");
    ProgressDialog progressDialog;
    private HashMap<String, Boolean> hm;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public void onStart() {
        super.onStart();
        valueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hm = (HashMap) dataSnapshot.getValue();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(valueEvent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_otp);
        toolbar = findViewById(R.id.toolbar);
        toolbar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("memproses data");
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_email = findViewById(R.id.et_email);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tvLogin);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getActivity(),"Kode Verifikasi telah dikirim",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.d("erornya",String.valueOf(e));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Too Many Attempt",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("OTP", "onCodeSent:" + verificationId);
                otp = verificationId;
                Intent intent = new Intent(getActivity(), sembako.sayunara.android.ui.component.verification.VerificationActivity.class);
                intent.putExtra("user", (Serializable) getUser());
                intent.putExtra("otp",otp);
                startActivity(intent);

                // Save verification ID and resending token so we can use them later

                //mResendToken = token;
                // ...
            }
        };

    }

    public void sendOTP() {

        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+"+phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    public void checkPhone() {
        progressDialog.show();
        Query query = firebaseFirestore.collection(Constant.Collection.COLLECTION_USER).whereEqualTo("phoneNumber", phonenumber);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Nomor Telepon telah terdaftar  silakan coba nomor lain", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    //sendOTP();
                    Intent intent = new Intent(getActivity(), sembako.sayunara.android.ui.component.verification.VerificationActivity.class);
                    intent.putExtra("user",  getUser());
                    intent.putExtra("otp","123456");
                    startActivity(intent);
                }
            }
        });
    }

}

