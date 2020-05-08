package sembako.sayunara.android.ui.camera.util;

import android.os.Bundle;

import sembako.sayunara.android.ui.base.BaseActivity;


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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
