package sembako.sayunara.android.screen.mainmenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.home.HomeFragment;
import sembako.sayunara.android.screen.inbox.InboxFragment;
import sembako.sayunara.android.screen.login.LoginFragment;
import sembako.sayunara.android.screen.mainmenu.util.CustomViewPager;
import sembako.sayunara.android.screen.mainmenu.util.ViewPagerAdapter;
import sembako.sayunara.android.screen.profile.ProfileFragment;
import sembako.sayunara.android.screen.profile.model.User;
import sembako.sayunara.android.screen.setting.SettingFragment;
import sembako.sayunara.android.screen.signup.SignUpFragment;

public class MainMenuActivity extends BaseActivity {

    protected CustomViewPager viewPager;
    protected BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    protected EditText et_searchview;
    protected RelativeLayout rl_searchview;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private User user;
    private LinearLayout bottomSheetParentLayout;
    private int stateOfBottomSheet = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if(isLogin==true){
            user = getUser();
        }
        //printHashKey();
        setContentView(R.layout.cc);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        et_searchview = findViewById(R.id.et_searchview);
        rl_searchview = findViewById(R.id.rl_searchview);

        bottomSheetParentLayout = findViewById(R.id.bottom_sheet);

        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParentLayout);

        if (bottomNavigationView != null) {

            mBottomSheetBehaviour.setPeekHeight(bottomNavigationView.getHeight() + 90);

        }
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParentLayout);
        if (bottomNavigationView != null) {
            TypedValue tv = new TypedValue();

            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                mBottomSheetBehaviour.setPeekHeight(actionBarHeight);

            }

        }

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home_menu:
                                rl_searchview.setVisibility(View.VISIBLE);
                                viewPager.setCurrentItem(0);
                                break;

                            case R.id.setting_menu:
                                rl_searchview.setVisibility(View.GONE);
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.inbox:
                                rl_searchview.setVisibility(View.GONE);
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.account_menu:
                                rl_searchview.setVisibility(View.GONE);
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.disableScroll(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }


    public void onBottomSheetDragListener() {
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Called every time when the bottom sheet changes its state.

                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:


                        stateOfBottomSheet = 1;

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        stateOfBottomSheet = 0;

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:


                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Called when the bottom sheet is being dragged


            }

        });
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("HASH", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.i("HASH","printHashKey()", e);
        } catch (Exception e) {
            Log.i("HASH", "printHashKey()", e);
        }
    }


    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HomeFragment homeFragment =new HomeFragment();
        SignUpFragment signUpFragmentt =new SignUpFragment();
        LoginFragment loginFragment = new LoginFragment();
        SettingFragment settingFragment = new SettingFragment();
        InboxFragment inboxFragment = new InboxFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(settingFragment);
        adapter.addFragment(inboxFragment);
        if(isLogin==true){
            adapter.addFragment(profileFragment);
        }else  {
            adapter.addFragment(loginFragment);
        }

        viewPager.setAdapter(adapter);
    }
}
