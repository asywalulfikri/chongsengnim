package sembako.sayunara.android.screen.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.screen.base.BaseFragment;
import sembako.sayunara.android.screen.profile.model.User;
import sembako.sayunara.android.screen.verification.VerificationActivity;

public class SignUpFragment extends BaseFragment {

    protected EditText et_first_name, et_last_name, et_email, et_phone_number, et_password, et_confirm_password;
    protected AppCompatButton btn_register;
    protected TextView tv_login;
    protected Toolbar toolbar;
    protected String firstname, lastname, email, phonenumber, password, confirmpassword, username,otp;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("memproses data");
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        et_first_name = view.findViewById(R.id.et_first_name);
        et_last_name = view.findViewById(R.id.et_last_name);
        et_email = view.findViewById(R.id.et_email);
        et_phone_number = view.findViewById(R.id.et_phone_number);
        et_password = view.findViewById(R.id.et_password);
        et_confirm_password = view.findViewById(R.id.et_confirm_password);
        btn_register = view.findViewById(R.id.btn_register);
        tv_login = view.findViewById(R.id.tv_login);


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
                Intent intent = new Intent(getActivity(), VerificationActivity.class);
                intent.putExtra("user", (Serializable) getUser());
                intent.putExtra("otp",otp);
                startActivity(intent);

                // Save verification ID and resending token so we can use them later

                //mResendToken = token;
                // ...
            }
        };

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = et_first_name.getText().toString().trim();
                lastname = et_last_name.getText().toString().trim();
                email = et_email.getText().toString().trim();
                phonenumber = convertPhone(et_phone_number.getText().toString().trim());
                password = et_password.getText().toString().trim();
                confirmpassword = et_confirm_password.getText().toString().trim();
                username = firstname + " " + lastname;

                if (firstname.equals("") || lastname.equals("")) {
                    Toast.makeText(getActivity(), "Silakan isi nama depan dan nama belakang", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isEmailValid(email)) {
                    Toast.makeText(getActivity(), "Masukkan Email yang Valid", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getActivity(), "Password Minimal 6 Karakter", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmpassword)) {
                    Toast.makeText(getActivity(), "Password yang Anda masukkan tidak sama", Toast.LENGTH_SHORT).show();
                } else if (phonenumber.length() < 8) {
                    Toast.makeText(getActivity(), "Nomor telepon tidak valid", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                     checkPhone();
                }
            }
        });
        return view;
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
        Query query = firebaseFirestore.collection(Constant.COLLECTION_USER).whereEqualTo("phoneNumber", phonenumber);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Nomor Telepon telah terdaftar  silakan coba nomor lain", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    //sendOTP();
                    Intent intent = new Intent(getActivity(), VerificationActivity.class);
                    intent.putExtra("user",  getUser());
                    intent.putExtra("otp","123456");
                    startActivity(intent);
                }
            }
        });
    }


    public User getUser(){
        final String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setUsername(firstname + " " + lastname);
        user.setUserId(userId);
        user.setType("user");
        user.setAvatar(Constant.DEFAULT_AVATAR);
        user.setEmail(email);
        user.setMitra(false);
        user.setHasStore(false);
        user.setActive(true);
        user.setPhoneNumber(phonenumber);
        user.setLaltitude("0.0");
        user.setLongitude("0.0");
        user.setCoordinate(user.getLaltitude()+","+user.getLongitude());
        user.setPassword(password);

        return user;
    }


}

