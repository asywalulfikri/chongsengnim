package sembako.sayunara.android.ui.component.splashcreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.component.mainmenu.MainMenuActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(() -> {
            checkLocationPermission();
            finish();
        }, 4000);

    }

    @Override
    public void onBackPressed() {

    }

    void toDashboard() {
        startActivity(new Intent(SplashScreenActivity.this, MainMenuActivity.class));
    }

    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             toDashboard();
        } else {
            toDashboard();
        }
    }

}
