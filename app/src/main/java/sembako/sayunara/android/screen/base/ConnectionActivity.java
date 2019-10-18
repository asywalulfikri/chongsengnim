package sembako.sayunara.android.screen.base;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.brouding.simpledialog.SimpleDialog;

import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.util.CommonDialogs;


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
                .commonProgress(Constant.DEFAULT_PROGRESS_TEXT, false);
        //progressDialog.setOnCancelListener(dialogInterface -> AndroidNetworking.cancelAll());
    }


    public void setProgressDialogMessage() {
        progressDialog = new CommonDialogs(this)
                .commonProgress(Constant.DEFAULT_PROGRESS_TEXT, false);
    }

    public void showProgressDialog() {
        progressDialog.show();
    }



    public void hideProgressDialog() {
        progressDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        if (!isConnectionOnProgress()) {
            super.onBackPressed();
        }
    }

}
