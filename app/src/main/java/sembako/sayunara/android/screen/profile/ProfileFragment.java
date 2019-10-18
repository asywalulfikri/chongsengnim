package sembako.sayunara.android.screen.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.squareup.picasso.Picasso;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.base.BaseFragment;
import sembako.sayunara.android.screen.mainmenu.MainMenuActivity;
import sembako.sayunara.android.screen.profile.model.User;

public class ProfileFragment extends BaseFragment {

    protected User user;
    protected EditText et_first_name, et_last_name, et_email, et_phone_number;
    protected ImageView iv_avatar;
    protected AppCompatButton btn_logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = ((BaseActivity)getActivity()).getUser();
        et_first_name = view.findViewById(R.id.et_first_name);
        et_last_name = view.findViewById(R.id.et_last_name);
        et_email = view.findViewById(R.id.et_email);
        et_phone_number = view.findViewById(R.id.et_phone_number);
        iv_avatar = view.findViewById(R.id.iv_avatar);
        btn_logout = view.findViewById(R.id.btn_logout);

        et_first_name.setText(user.getUsername());
        et_email.setText(user.getEmail());
        et_phone_number.setText(user.getPhoneNumber());

        Picasso.get()
                .load(user.getAvatar())
                .into(iv_avatar);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSignout();
            }
        });
        return view;
    }
}

