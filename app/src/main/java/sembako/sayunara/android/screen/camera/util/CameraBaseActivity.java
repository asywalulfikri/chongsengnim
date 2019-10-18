package sembako.sayunara.android.screen.camera.util;

import android.os.Bundle;

import sembako.sayunara.android.screen.base.BaseActivity;


public class CameraBaseActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.getInst().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInst().removeActivity(this);
    }

}
