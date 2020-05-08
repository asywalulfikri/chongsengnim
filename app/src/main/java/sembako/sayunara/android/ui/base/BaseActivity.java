/*
package sembako.sayunara.android.screen.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.screen.account.profile.model.User;
import sembako.sayunara.android.ui.util.GPSTracker;

public abstract class BaseActivity extends AppCompatActivity {
    public static final int REQUEST_LOCATION = 0;
    public static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public FirebaseFirestore firebaseFirestore;
    public static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

            Manifest.permission.CAMERA};
    public String latitude = "0.0";
    public String longitude = "0.0";
    public GPSTracker mGpsTracker;
    public ProgressDialog progressDialog;

    public SharedPreferences sharedPreferences;
    public boolean isLogin;
    public abstract int getLayoutId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        onActivityReady(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("login",0);
        isLogin = sharedPreferences.getBoolean("isLogin",false);


    }

    public abstract void onActivityReady(Bundle savedInstanceState);

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.UserKey.userId,user.getUserId());
        editor.putString(Constant.UserKey.type, user.getType());
        editor.putString(Constant.UserKey.username, user.getUsername());
        editor.putString(Constant.UserKey.phoneNumber, user.getPhoneNumber());
        editor.putString(Constant.UserKey.email, user.getEmail());
        editor.putBoolean(Constant.UserKey.isMitra,user.getIsMitra());
        editor.putBoolean(Constant.UserKey.isAvtive,user.isActive());
        editor.putBoolean(Constant.UserKey.hasStore,user.isHasStore());
        editor.putString(Constant.UserKey.avatar,user.getAvatar());
        editor.putBoolean(Constant.UserKey.isLogin,true);
        editor.apply();
    }


    public void clearUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.UserKey.userId,"");
        editor.putString(Constant.UserKey.type,"");
        editor.putString(Constant.UserKey.username, "");
        editor.putString(Constant.UserKey.phoneNumber, "");
        editor.putString(Constant.UserKey.email, "");
        editor.putBoolean(Constant.UserKey.isMitra,false);
        editor.putBoolean(Constant.UserKey.isAvtive,false);
        editor.putBoolean(Constant.UserKey.hasStore,false);
        editor.putString(Constant.UserKey.avatar,"");
        editor.putBoolean(Constant.UserKey.isLogin,false);
        editor.apply();
    }


    public void setToast(int message){
        Toast.makeText(getActivity(),getString(message),Toast.LENGTH_SHORT).show();
    }
    public User getUser() {

        User user = new User();

        user.setUserId(sharedPreferences.getString(Constant.UserKey.userId, ""));
        user.setType(sharedPreferences.getString(Constant.UserKey.type, ""));
        user.setUsername(sharedPreferences.getString(Constant.UserKey.username, ""));
        user.setPhoneNumber(sharedPreferences.getString(Constant.UserKey.phoneNumber, "" ));
        user.setEmail(sharedPreferences.getString(Constant.UserKey.email, ""));
        user.setAvatar(sharedPreferences.getString(Constant.UserKey.avatar, ""));
        user.setMitra(sharedPreferences.getBoolean(Constant.UserKey.isMitra, false));
        user.setActive(sharedPreferences.getBoolean(Constant.UserKey.isAvtive,false));
        user.setHasStore(sharedPreferences.getBoolean(Constant.UserKey.hasStore,false));
        user.setAvatar(sharedPreferences.getString(Constant.UserKey.avatar,""));
        user.setLogin(sharedPreferences.getBoolean(Constant.UserKey.isLogin,false));

        return user;
    }



    public boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public String convertPhone(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("\\+62", "62");
        if(phoneNumber.startsWith("08")){
            phoneNumber = phoneNumber.replaceAll("08", "628");
        }
        return phoneNumber;
    }


    @SuppressLint("ResourceAsColor")
    public void setToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(R.color.black);
        toolbar.setTitleTextAppearance(this, R.style.textSizeToolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    @SuppressLint("NewApi")
    public void toolbar(Toolbar toolbar){
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.WHITE);
       // toolbar.setTitleTextColor(R.color.black);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    public void setToolbar(Toolbar toolbar,String title){

        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.GREEN);
       // toolbar.setTitleTextColor(R.color.green_1);
        toolbar.setTitleTextAppearance(this, R.style.textSizeToolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    public AlertDialog.Builder getBuilder(Context context) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        return builder;
    }

    public String convertPrice(int price){
        double harga = Double.parseDouble(String.valueOf(price));
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String prices = df.format(harga);

        return prices;
    }


    public void  getGpsTracker() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            mGpsTracker = new GPSTracker(getActivity());
            latitude = String.valueOf(mGpsTracker.getLatitude());
            longitude = String.valueOf(mGpsTracker.getLongitude());
        }
    }


    public void setColorTintButton(MaterialButton button, int color){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList stateList = ColorStateList.valueOf(color);
            button.setBackgroundTintList(stateList);
        } else {
            button.getBackground().getCurrent().setColorFilter(
                    new PorterDuffColorFilter(color,
                            PorterDuff.Mode.MULTIPLY));
        }
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }


    public BaseActivity getActivity() {
        return this;
    }

}
*/
