package sembako.sayunara.android.ui.base;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.brouding.simpledialog.SimpleDialog;

import sembako.sayunara.android.App;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.util.CommonDialogs;


@SuppressLint("Registered")
public class ConnectionActivity extends BaseActivity {

    private boolean isConnectionOnProgress = false;
    public SimpleDialog progressDialog;

    public boolean isConnectionOnProgress() {
        return isConnectionOnProgress;
    }

    public void setConnectionOnProgress(boolean status) {
        isConnectionOnProgress = status;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new CommonDialogs(ConnectionActivity.this)
                .commonProgress(Constant.Progress.DEFAULT_PROGRESS_TEXT, false);
    }


    public void setProgressDialogMessage() {
        progressDialog = new CommonDialogs(this)
                .commonProgress(Constant.Progress.DEFAULT_PROGRESS_TEXT, false);
    }

    public void showProgressDialog() {
        progressDialog.show();
    }



    public void hideProgressDialog() {
        progressDialog.dismiss();
    }


    public void setDialog(boolean status){
        if(status){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        if (!isConnectionOnProgress()) {
            super.onBackPressed();
        }
    }


    public sembako.sayunara.android.App getApp() {
        return ((App) getApplication());
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
