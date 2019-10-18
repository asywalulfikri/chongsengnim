package sembako.sayunara.android.screen.verification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.mukesh.OtpView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.mainmenu.MainMenuActivity;
import sembako.sayunara.android.screen.profile.model.User;

public class VerificationActivity extends BaseActivity {

    protected OtpView mOtpView;
    protected String phonenumber,otp,otp2;

    boolean autoRead = false;

    CountDownTimer mTimer;
    private static final long MAX_TIMER = 1000 * (60 * 2);
    private static final long COUNTER_INTERVAL = 1000;
    private long timerValue = MAX_TIMER;
    protected TextView tv_resend;
    protected AppCompatButton btn_verification;
    protected User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("memproses data");
        mOtpView = findViewById(R.id.otp_view);
        tv_resend = findViewById(R.id.tv_resend);
        btn_verification = findViewById(R.id.btn_verification);

        user = (User)getIntent().getSerializableExtra("user");

        setupViews();

        otp = getIntent().getStringExtra("otp");


        btn_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(otp.equals(otp2)){
                   register();
                }else {
                    Toast.makeText(getActivity(), "Kode OTP Salah", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }


    public void setupViews() {


        mOtpView.setOtpCompletionListener(verificationNumber -> {
            if (!autoRead) {
                //todo: set to view
                otp2 = verificationNumber;
            } else {
                // todo: wait completion
                // debug autocomplete
            }
        });

        mTimer = new CountDownTimer(MAX_TIMER, COUNTER_INTERVAL) {
            @Override
            public void onTick(long interval) {
                timerValue -= COUNTER_INTERVAL;
                tv_resend.setText(String.valueOf(timerValue / 1000));
            }

            @Override
            public void onFinish() {
                tv_resend.setText("KIRIM ULANG");
               // tv_resend.setOnClickListener(VerificationActivity.this);
            }
        }.start();

    }


     private void register() {
        progressDialog.show();
        Long tsLong = System.currentTimeMillis() / 1000;
        final String uuid = UUID.randomUUID().toString();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        Map<String, Object> obj = new HashMap<>();

        obj.put("username", user.getUsername());
        obj.put("userId", user.getUserId());
        obj.put("type", user.getType());
        obj.put("avatar", user.getAvatar());
        obj.put("email", user.getEmail());
        obj.put("isMitra", user.getIsMitra());
        obj.put("hasStore", user.isHasStore());
        obj.put("isActive", user.isActive());
        obj.put("phoneNumber", user.getPhoneNumber());

        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("latitude", user.getLaltitude());
        coordinate.put("longitude", user.getLongitude());
        obj.put("coordinate", user.getCoordinate());


        obj.put("password", user.getPassword());

        String informasi = Build.MANUFACTURER
                + " " + Build.MODEL + " , Versi OS :" + Build.VERSION.RELEASE;

        Map<String, Object> phone = new HashMap<>();
        phone.put("androidVersion", getVersionName(getActivity()));
        phone.put("appVersion", String.valueOf(Build.VERSION.SDK_INT));
        phone.put("phoneName", informasi);
        obj.put("phoneDetail", phone);


        Map<String, Object> datePublishedData = new HashMap<>();
        datePublishedData.put("iso", nowAsISO);
        datePublishedData.put("timestamp", tsLong);
        obj.put("createdAt", datePublishedData);

        Map<String, Object> dateSubmittedData = new HashMap<>();
        dateSubmittedData.put("iso", nowAsISO);
        dateSubmittedData.put("timestamp", tsLong);
        obj.put("updatedAt", dateSubmittedData);


        firebaseFirestore.collection(Constant.COLLECTION_USER).document(uuid)
                .set(obj)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Pendaftaran Sukses", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),MainMenuActivity.class);
                    saveUser(user);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                   // progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Pendaftaran Gagal", Toast.LENGTH_SHORT).show();
                });

    }
}
