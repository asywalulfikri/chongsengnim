package sembako.sayunara.android.ui.util;

import android.content.Context;

import com.brouding.simpledialog.SimpleDialog;


public class CommonDialogs {

    public interface onDialogClickListener{
        void onRightClick();
        void onLeftClick();
    }

    private final Context context;

    public CommonDialogs(Context context) {
        this.context = context;
    }

    public SimpleDialog commonProgress(String message, boolean cancleAble){
        SimpleDialog.Builder dialogBuilder = new SimpleDialog.Builder(context);
        dialogBuilder.showProgress(true);
        dialogBuilder.setContent(message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setBtnCancelText("");
        //dialogBuilder.setBtnCancelShowTime(Constant.DEFAULT_CANCEL_BUTTON_SHOW);
        return dialogBuilder.build();
    }

    public SimpleDialog commonDialog(String message, boolean cancelAble, onDialogClickListener listener){
        SimpleDialog.Builder builder = new SimpleDialog.Builder(context);
        builder.setContent(message);
        builder.setCancelable(cancelAble);
        builder.setBtnConfirmText("OK", false);
        builder.setBtnCancelText("Batal", true);
        builder.onConfirm((dialog, which) -> listener.onRightClick());
        builder.onCancel((dialog, which) -> {
           dialog.dismiss();
           listener.onLeftClick();
        });
        return builder.build();
    }

}
