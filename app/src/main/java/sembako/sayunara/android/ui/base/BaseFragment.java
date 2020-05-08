/*
package sembako.sayunara.android.ui.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.component.mainmenu.MainMenuActivity;


public class BaseFragment extends Fragment {

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

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		View v = getActivity().getCurrentFocus();
		if (v != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}


	public void setToast(String tipe){
		Toast.makeText(getActivity(),tipe,Toast.LENGTH_SHORT).show();
	}

	public void setToast(int tipe){
		Toast.makeText(getActivity(),getString(tipe),Toast.LENGTH_SHORT).show();
	}


	public String convertPhone(String phoneNumber){
		phoneNumber = phoneNumber.replaceAll("\\+62", "62");
		if(phoneNumber.startsWith("08")){
			phoneNumber = phoneNumber.replaceAll("08", "628");
		}
		return phoneNumber;
	}

	public void confirmSignout() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		@SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_info, null);

		TextView messageTv = view.findViewById(R.id.tv_message);

		messageTv.setText("Apakah anda akan keluar aplikasi ?");

		AlertDialog.Builder builder = getBuilder(getActivity());

		builder.setView(view)
				.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
				.setPositiveButton("Ya",
						(dialog, which) -> {
							((BaseActivity)getActivity()).clearUser();
							Intent intent = new Intent(getActivity(), MainMenuActivity.class);
							startActivity(intent);
						});

		builder.create().show();
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
}*/
