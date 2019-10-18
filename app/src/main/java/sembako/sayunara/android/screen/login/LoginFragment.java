package sembako.sayunara.android.screen.login;

import android.content.Intent;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.base.BaseFragment;
import sembako.sayunara.android.screen.mainmenu.MainMenuActivity;
import sembako.sayunara.android.screen.product.listproduct.model.Product;
import sembako.sayunara.android.screen.product.listproduct.model.TotalCount;
import sembako.sayunara.android.screen.profile.model.User;
import sembako.sayunara.android.screen.signup.SignUpActivity;

import static com.facebook.FacebookSdk.getApplicationContext;
import static io.fabric.sdk.android.Fabric.TAG;

public class LoginFragment extends BaseFragment {
   protected LoginButton loginButton;
   protected EditText et_phone_number,et_password;
   protected AppCompatButton btn_login;
   protected FirebaseAuth auth;
   protected CallbackManager mCallbackManager;
   protected FirebaseFirestore firebaseFirestore;
   protected TextView tv_register;
   protected String phonenumber,password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = view.findViewById(R.id.facebook_login);
        btn_login = view.findViewById(R.id.btn_login);
        et_phone_number = view.findViewById(R.id.et_phone_number);
        et_password = view.findViewById(R.id.et_password);
        tv_register = view.findViewById(R.id.tv_register);
        FacebookSdk.sdkInitialize(getActivity());
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "SudahLogin",
                    Toast.LENGTH_SHORT).show();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumber = convertPhone(et_phone_number.getText().toString().trim().replace(".",""));
                password = et_password.getText().toString().trim();

                if (phonenumber.equals("")) {
                    Toast.makeText(getActivity(), "Isi nomor telepon Anda", Toast.LENGTH_SHORT).show();
                    return;
                }else if(phonenumber.length()<8){
                    Toast.makeText(getActivity(), "Nomor telepon tidak valid", Toast.LENGTH_SHORT).show();
                    return;
                }else  if(password.length()<6){
                    Toast.makeText(getActivity(), "Password terlalu singkat", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    login();
                }

            }
        });
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("tokenya", "facebook:onSuccess:" + loginResult);
                loginButton.setEnabled(false);
                Toast.makeText(getActivity(), "lanjut",
                        Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getActivity(), "cancel",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getActivity(), "erorx",
                        Toast.LENGTH_SHORT).show();
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    public void login() {
        Query docRef = firebaseFirestore.collection(Constant.COLLECTION_USER).whereEqualTo("phoneNumber",phonenumber);
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        User user = new User();
                        user.setAvatar(doc.getString("avatar"));
                        user.setLogin(true);
                        user.setEmail(doc.getString("email"));
                        user.setType(doc.getString("type"));
                        user.setPhoneNumber(doc.getString("phoneNumber"));
                        user.setUserId(doc.getString("userId"));
                        user.setUsername(doc.getString("username"));

                        Log.d("isinya",user.getAvatar());
                        ((BaseActivity)getActivity()).saveUser(user);
                        Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                        startActivity(intent);
                    }


                } else {
                    Toast.makeText(getActivity(),"gagal"+ task.getException(),Toast.LENGTH_SHORT).show();
                    Log.d("urlnya", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            et_phone_number.setText(task.getResult().getUser().getEmail());
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tokenya", "signInWithCredential:success");
                            loginButton.setEnabled(true);
                            //login();
                            Toast.makeText(getActivity(), "Login sukses",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

