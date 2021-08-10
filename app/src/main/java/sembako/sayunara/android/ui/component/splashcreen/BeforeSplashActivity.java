package sembako.sayunara.android.ui.component.splashcreen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import sembako.sayunara.android.BuildConfig;
import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.main.MainActivity;
import sembako.sayunara.android.ui.component.splashcreen.model.AppColor;

public class BeforeSplashActivity extends BaseActivity {

    AppColor app;
    Dialog dialog;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(BeforeSplashActivity.this);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        fetchMainConfig();

    }


    private void showUpdateDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_dialog_update));
        builder.setPositiveButton("Update", (dialog, which) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                    ("market://details?id="+ BuildConfig.APPLICATION_ID)));
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.text_cancel), (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        dialog = builder.show();
    }

    private void fetchMainConfig() {
//        mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));

        long cacheExpiration = 3600; // 1 hour in seconds.

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("responsex","Fetch Succeeded");
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.d("responsex",task.getException().getMessage());
                        }
                        updateConfig();
                    }
                });
    }

    private void updateConfig () {
        AppColor app = new AppColor();

        app.app_name = mFirebaseRemoteConfig.getString(Constant.UserKey.versionCode);
        app.app_version = mFirebaseRemoteConfig.getDouble(Constant.UserKey.versionCode);
        app.required_update = mFirebaseRemoteConfig.getBoolean(Constant.UserKey.requiredUpdate);

        int appVersion = BuildConfig.VERSION_CODE;

        if (appVersion < app.app_version && app.required_update) {
            showUpdateDialog();
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

    public void onResume(){
        super.onResume();
        fetchMainConfig();
    }
}