/*
package sembako.sayunara.android.ui.component.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.indosatooredoo.Imkasapp.Indosat;
import com.indosatooredoo.Imkasapp.R;
import com.indosatooredoo.Imkasapp.apiService.ApiConfigs;
import com.indosatooredoo.Imkasapp.base.AppActivity;
import com.indosatooredoo.Imkasapp.config.AppKeys;
import com.indosatooredoo.Imkasapp.config.IConfig;
import com.indosatooredoo.Imkasapp.model.general.MainMenu;
import com.indosatooredoo.Imkasapp.model.general.UserDetail;
import com.indosatooredoo.Imkasapp.model.response.ActivityHistoryResponse;
import com.indosatooredoo.Imkasapp.model.response.BannerResponse;
import com.indosatooredoo.Imkasapp.model.response.DetailHistoryResponse;
import com.indosatooredoo.Imkasapp.model.response.Insurance.InsuranceListResponse;
import com.indosatooredoo.Imkasapp.model.response.SignOutResponse;
import com.indosatooredoo.Imkasapp.model.response.TransactionHistoryResponse;
import com.indosatooredoo.Imkasapp.model.response.UserDetailResponse;
import com.indosatooredoo.Imkasapp.model.response.billPayment.indomaret.IndomaretInquiryResponse;
import com.indosatooredoo.Imkasapp.model.temp.investment.InvestmentProduct;
import com.indosatooredoo.Imkasapp.presenter.dao.SignOutDao;
import com.indosatooredoo.Imkasapp.support.Connectivity;
import com.indosatooredoo.Imkasapp.support.Preferences;
import com.indosatooredoo.Imkasapp.support.widget.recyclerView.ItemClickSupport;
import com.indosatooredoo.Imkasapp.support.widget.snackbar.TopSnackbar;
import com.indosatooredoo.Imkasapp.ui.activity.account.pedeCash.PedeCashDetailActivity;
import com.indosatooredoo.Imkasapp.ui.activity.account.pedePlus.scan.UpgradeKYCActivity;
import com.indosatooredoo.Imkasapp.ui.activity.billPayment.BillerProductType;
import com.indosatooredoo.Imkasapp.ui.activity.billPayment.bpjs.BPJSActivity;
import com.indosatooredoo.Imkasapp.ui.activity.billPayment.pulsaPaket.PulsaPaketActivity;
import com.indosatooredoo.Imkasapp.ui.activity.billPayment.waterListrik.WaterProductActivity;
import com.indosatooredoo.Imkasapp.ui.activity.insurance.InsuranceActivity;
import com.indosatooredoo.Imkasapp.ui.activity.investment.InvestmentActivity;
import com.indosatooredoo.Imkasapp.ui.activity.investment.InvestmentIntroActivity;
import com.indosatooredoo.Imkasapp.ui.activity.investment.pedeHasilLebih.InvestmentPedeHasilLebihDetailActivity;
import com.indosatooredoo.Imkasapp.ui.activity.loan.LoanActivity;
import com.indosatooredoo.Imkasapp.ui.activity.nearbyMerchant.NearbyMerchantActivity;
import com.indosatooredoo.Imkasapp.ui.activity.qr.QrContainerActivity;
import com.indosatooredoo.Imkasapp.ui.activity.splash.SplashActivity;
import com.indosatooredoo.Imkasapp.ui.activity.topUp.TopUpActivity;
import com.indosatooredoo.Imkasapp.ui.adapter.MainMenuAdapter;
import com.indosatooredoo.Imkasapp.ui.fragment.home.HomeFragmentNew;
import com.indosatooredoo.Imkasapp.ui.fragment.inbox.InboxFragment;
import com.indosatooredoo.Imkasapp.ui.fragment.profile.ProfileFragment;
import com.indosatooredoo.Imkasapp.ui.fragment.qrcode.QrcodeFragment;
import com.indosatooredoo.Imkasapp.ui.fragment.transactionHistory.HistoryFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.netcore.android.Smartech;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.beelabs.com.codebase.base.BaseActivity;
import app.beelabs.com.codebase.base.BaseDao;
import app.beelabs.com.codebase.base.response.BaseResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

import static com.indosatooredoo.Imkasapp.Indosat.getContext;

public class MainActivity extends AppActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public static final String TAG_UPGRADE_STATUS_NONE = "NONE";
    public static final String TAG_UPGRADE_STATUS_APPROVED = "APPROVED";
    public static final String TAG_UPGRADE_STATUS_PENDING = "PENDING";
    public static final String TAG_UPGRADE_STATUS_REJECTED = "REJECTED";

    @BindView(R.id.tab_layout)
    LinearLayout tabLayout;

    @BindView(R.id.tab_home)
    LinearLayout tabHome;

    @BindView(R.id.tab_riwayat)
    LinearLayout tabRiwayat;

    @BindView(R.id.tab_qrcode)
    LinearLayout tabQrcode;

    @BindView(R.id.tab_inbox)
    LinearLayout tabInbox;

    @BindView(R.id.tab_profile)
    LinearLayout tabProfile;

    @BindView(R.id.iv_tab_home)
    ImageView ivTabHome;

    @BindView(R.id.iv_tab_riwayat)
    ImageView ivTabRiwayat;

    @BindView(R.id.iv_tab_qrcode)
    ImageView ivTabQrcode;

    @BindView(R.id.iv_tab_inbox)
    ImageView ivTabInbox;

    @BindView(R.id.iv_tab_profile)
    ImageView ivTabProfile;

    @BindView(R.id.tv_tab_home)
    TextView tvTabHome;

    @BindView(R.id.tv_tab_riwayat)
    TextView tvTabRiwayat;

    @BindView(R.id.tv_tab_qrcode)
    TextView tvTabQrcode;

    @BindView(R.id.tv_tab_inbox)
    TextView tvTabInbox;

    @BindView(R.id.tv_tab_profile)
    TextView tvTabProfile;

    @BindView(R.id.more_menu_layout)
    LinearLayout moreMenuLayout;

    @BindView(R.id.rl_qris)
    RelativeLayout rlQris;

    @BindView(R.id.bottom_menu_overlay)
    View bottomMenuOverlay;

    @BindView(R.id.recycler_view_main_menu)
    RecyclerView rvMainMenu;

    @BindView(R.id.ic_close)
    View ic_close;

    @BindView(R.id.fab_bottom_appbar)
    ImageView ivQris;

    private int mTabPosition = 99;

    private boolean isBottomMenuOpened = false;
    private boolean mIsUserHavePFMScore = false;
    private boolean mIsSuccessLoadUserDetail = false;

    private boolean mDoubleBackToExitPressedOnce = false;

    private Preferences mPreferences;

    private String accessToken;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, this);
        setupLightStatusBarMode();
        getFirebaseToken();
        activity = com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this;
        mPreferences = new Preferences(this);
        accessToken = mPreferences.getAccessToken();
        ButterKnife.bind(this);
        int tabPosition = (int) getIntent().getIntExtra(IConfig.KEY_TAB_POSITION, 0);
        if (getIntent().hasExtra("pushnotification")) {
            tabPosition = 1;
        }

        initView();
        initRecyclerView();

        rlQris.setOnClickListener(v -> {
            startActivityForResult(new Intent(com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this, QrContainerActivity.class), AppKeys.GOTO_QRCONT);
        });

        setupFullFeature();

        displayMainMenu();
        if (tabPosition > 0) {
            String[] menu = getResources().getStringArray(R.array.home_bottom_tab_menu);
            tabSelected(tabPosition, menu[tabPosition]);
        } else {
            tabSelected(0, "Beranda");
        }

    }

    private void initPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                report.areAllPermissionsGranted();// do you work now
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }

        }).onSameThread().check();
    }

    private void setupFullFeature(){
        if(IConfig.FullFeature){
            ivQris.setVisibility(View.VISIBLE);
        }else {
            ivQris.setVisibility(View.GONE);
        }
    }


    private void initView() {
        tabHome.setOnClickListener(this);
        tabRiwayat.setOnClickListener(this);
        tabQrcode.setOnClickListener(this);
        tabInbox.setOnClickListener(this);
        tabProfile.setOnClickListener(this);
        ic_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String[] menu = getResources().getStringArray(R.array.home_bottom_tab_menu);

        int id = v.getId();
        if (id == tabHome.getId()) {
            tabSelected(0, menu[0]);
        } else if (id == tabRiwayat.getId()) {
            tabSelected(1, menu[1]);
        } else if (id == tabQrcode.getId()) {
            tabSelected(2, menu[2]);
        }else if (id == tabInbox.getId()) {
            tabSelected(3, menu[3]);
        } else if (id == tabProfile.getId()) {
            tabSelected(4, menu[4]);
        } else if (id == ic_close.getId()) {
            showMoreMenu(false);
        }
    }

    public void gotoInsurance() {
        startActivity(new Intent(this, InsuranceActivity.class));
    }

    public void gotoLoan() {
        Intent intent = new Intent(this, LoanActivity.class);
        intent.putExtra(AppKeys.KEY_LOAN_FROM_HOME, "FROM_HOME");
        startActivity(intent);
    }

    public void gotoIUR() {
        startActivity(new Intent(this, InvestmentActivity.class));
    }

    public void gotoPHLIntro() {
        EventBus.getDefault().postSticky(getPHLProduct());
        startActivity(new Intent(this, InvestmentIntroActivity.class));
    }

    public void gotoTopUpGuide() {
        startActivity(new Intent(this, TopUpActivity.class));
    }

    public void gotoPedeCash() {
        if (ismIsSuccessLoadUserDetail()) {
            Intent intent = new Intent(this, PedeCashDetailActivity.class);
            startActivityForResult(intent, AppKeys.GOTO_PEDECASH);
        }
    }

    public void tabSelected(int position, String type) {
        if (!TextUtils.isEmpty(type)) {

            switch (type) {
                case "Beranda":
                    changeStatusBarColor(R.color.red);
                    displayFragment(new HomeFragmentNew(), R.id.fragment_container);
                    break;
                case "Riwayat":
                    changeStatusBarColor(R.color.white);
                    mPreferences.setSynchronize("0");
                    displayFragment(new HistoryFragment(), R.id.fragment_container);
                    break;
                case "Bayar":
                    changeStatusBarColor(R.color.white);
                    mPreferences.setSynchronize("0");
                    startActivityForResult(new Intent(this, QrContainerActivity.class), AppKeys.GOTO_QRCONT);
                    break;

                case "Inbox":
                    changeStatusBarColor(R.color.white);
                    mPreferences.setSynchronize("0");
                    displayFragment(new InboxFragment(), R.id.fragment_container);
                    break;

                case "Profil":
                    changeStatusBarColor(R.color.white);
                    mPreferences.setSynchronize("0");
                    displayFragment(new ProfileFragment(), R.id.fragment_container);
                    break;
                default:
                    break;
            }
            updateTabView(position);
            mTabPosition = position;
        }
    }

    */
/**
     * Display Fragment
     *
     * @param fragment
     * @param fragmentResourceID
     *//*

    public void displayFragment(Fragment fragment, int fragmentResourceID) {
        try {
            showFragment(fragment, fragmentResourceID);
        } catch (IllegalStateException e) {
            e.printStackTrace();

            showFragmentAllowingStateLoss(fragment, fragmentResourceID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission(activity);
        */
/*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initPermission();
        }else {

        }*//*


    }
    */
/**
     * @param showStatus true = show | false = close
     *//*

    public void showMoreMenu(boolean showStatus) {
        isBottomMenuOpened = showStatus;

        Animation animation;
        if (showStatus) {
            bottomMenuOverlay.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.bottom_up);
        } else {
            bottomMenuOverlay.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.bottom_down);
        }

        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.more_menu_layout);
        hiddenPanel.startAnimation(animation);
        if (showStatus) {
            hiddenPanel.setVisibility(View.VISIBLE);
            //tabLayout.setVisibility(View.GONE);
        } else {
            hiddenPanel.setVisibility(View.GONE);
            //tabLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateTabView(int position) {
        ivTabHome.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_home_non_active));
        tvTabHome.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
        tvTabHome.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        ivTabRiwayat.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_history_non_active));
        tvTabRiwayat.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
        tvTabRiwayat.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        ivTabQrcode.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rp_non_active));
        tvTabQrcode.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
        tvTabQrcode.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        ivTabInbox.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_inbox_non_active));
        tvTabInbox.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
        tvTabInbox.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        ivTabProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_profile_non_active));
        tvTabProfile.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
        tvTabProfile.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        switch (position) {
            case 1:
                ivTabRiwayat.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_history_active));
                tvTabRiwayat.setTextColor(ContextCompat.getColor(this, R.color.color_gray));
                tvTabRiwayat.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case 2:
                ivTabQrcode.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rp_active));
                tvTabQrcode.setTextColor(ContextCompat.getColor(this, R.color.color_gray));
                tvTabQrcode.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case 3:
                ivTabInbox.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_inbox_active_));
                tvTabInbox.setTextColor(ContextCompat.getColor(this, R.color.color_gray));
                tvTabInbox.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case 4:
                ivTabProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_profile_active));
                tvTabProfile.setTextColor(ContextCompat.getColor(this, R.color.color_gray));
                tvTabProfile.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            default:
                ivTabHome.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_home_active));
                tvTabHome.setTextColor(ContextCompat.getColor(this, R.color.color_gray));
                tvTabHome.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
        }
    }

    private void defaultTapView() {
        ivTabHome.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_home_active));
        tvTabHome.setTextColor(ContextCompat.getColor(this, R.color.color_gray));

        ivTabRiwayat.setBackground(ContextCompat.getDrawable(this, R.drawable.imcash_ic_riwayat_btmmenu));
        tvTabRiwayat.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));

        ivTabQrcode.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rp_non_active));
        tvTabQrcode.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));

        ivTabInbox.setBackground(ContextCompat.getDrawable(this, R.drawable.imcash_ic_inbox));
        tvTabInbox.setBackground(ContextCompat.getDrawable(this, R.color.indosat_grey_light_4));

        ivTabProfile.setBackground(ContextCompat.getDrawable(this, R.drawable.imcash_ic_profile_btmmenu));
        tvTabProfile.setTextColor(ContextCompat.getColor(this, R.color.indosat_grey_light_4));
    }

    private void initRecyclerView() {
        rvMainMenu.setHasFixedSize(true);

        final GridLayoutManager mainMenuLayoutManager = new GridLayoutManager(this, 4);
        rvMainMenu.setLayoutManager(mainMenuLayoutManager);
    }

    @SuppressLint("ResourceType")
    private void initBottomTabMenu() {
        String[] menu = getResources().getStringArray(R.array.home_bottom_tab_menu);
        Configuration conf = getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(Locale.getDefault());
        Context localizedContext = createConfigurationContext(conf);

        TypedArray icon = localizedContext.getResources().obtainTypedArray(R.array.home_bottom_tab_icon);

        setTab(menu[0], icon.getResourceId(0, -1), ivTabHome, tvTabHome);
        setTab(menu[1], icon.getResourceId(1, -1), ivTabRiwayat, tvTabRiwayat);
        setTab(menu[2], icon.getResourceId(2, -1), ivTabQrcode, tvTabQrcode);
        setTab(menu[3], icon.getResourceId(2, -1), ivTabInbox, tvTabInbox);
        setTab(menu[4], icon.getResourceId(3, -1), ivTabProfile, tvTabProfile);
    }

    public void setTab(String type, int icon, ImageView imageView, TextView textView) {
        imageView.setImageResource(icon);
        textView.setText(type);
    }

    private void displayMainMenu() {
        final List<MainMenu> mainMenuList = getMainMenuList();

        MainMenuAdapter adapter = new MainMenuAdapter(this, mainMenuList, R.layout.item_main_menu_more);
        rvMainMenu.setAdapter(adapter);

        ItemClickSupport.addTo(rvMainMenu).setOnItemClickListener((recyclerView, position, v) -> {
            showMoreMenu(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> mainMenuSelected(mainMenuList.get(position)), 300);
        });
    }

    private void mainMenuSelected(MainMenu mainMenu) {
        switch (mainMenu.getCode()) {
            case "mm_1":
                gotoInsurance();
                break;
            case "mm_2":
                gotoIUR();
                break;
            case "mm_3":
                gotoLoan();
                break;
            case "mm_4":
                EventBus.getDefault().postSticky(Integer.valueOf(BillerProductType.WATER));
                startActivity(new Intent(this, WaterProductActivity.class));
                break;
            case "mm_5":
                startActivity(new Intent(this, PulsaPaketActivity.class));
                break;
            case "mm_6":
                showComingSoonDialog();
                break;
            case "mm_7":
                EventBus.getDefault().postSticky(Integer.valueOf(BillerProductType.TV));
                startActivity(new Intent(this, WaterProductActivity.class));
                break;
            case "mm_8":
                startActivity(new Intent(this, BPJSActivity.class));
                break;
            case "mm_9":
                //startActivity(new Intent(this, DonationActivity.class));
                showComingSoonDialog();
                break;
            case "mm_10":
                showComingSoonDialog();
//                startActivity(new Intent(this, VoucherGameWebviewAcitivity.class));
                break;
            case "mm_11":
                showComingSoonDialog();
//                gotoInsurance();
                break;
            case "mm_12":
                String[] menu = getResources().getStringArray(R.array.home_bottom_tab_menu);
                tabSelected(4, menu[4]);
                break;
            default:
                break;
        }
    }

    public void gotoNearbyMerchant() {
        startActivity(new Intent(this, NearbyMerchantActivity.class));
    }

    public boolean isPedePlusApproved() {
        if (getUserDetail() != null && getUserDetail().getUpgradeStatus() != null) {
            if (getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_APPROVED)) {
                return true;
            } else if (getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_PENDING)) {
                showUpgradePedePendingDialog();
                return false;
            } else {
                showUpgradePedePlusDialog();
                return false;
            }
        } else {
            showUpgradePedePlusDialog();
            return false;
        }
    }

    private List<MainMenu> getMainMenuList() {

        String[] mainMenus;
        String[] mainMenuCodes;
        Integer[] menuIcons;

        mainMenus = getResources().getStringArray(R.array.more_main_menu_name);
        mainMenuCodes = getResources().getStringArray(R.array.more_main_menu_code);

        menuIcons = new Integer[]{
                R.drawable.ic_insurance_colored,
                R.drawable.ic_iur_colored,
                R.drawable.ic_loan,
                R.drawable.ic_air_dan_listrik,
                R.drawable.ic_pulsa_dan_paket,
                R.drawable.ic_charity,
                R.drawable.ic_tv_kabel,
                R.drawable.ic_bpjs,
                R.drawable.ic_travel_ticket,
                R.drawable.ic_voucher_game
        };

        List<MainMenu> mainMenuList = new ArrayList<>();
        for (int i = 0; i < mainMenus.length; i++) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setCode(mainMenuCodes[i]);
            mainMenu.setName(mainMenus[i]);
            mainMenu.setIcon(menuIcons[i]);
            mainMenuList.add(mainMenu);
        }

        return mainMenuList;
    }

    @Override
    protected void onApiResponseCallback(BaseResponse br, int responseCode, Response response) {
        super.onApiResponseCallback(br, responseCode, response);

        */
/*if (response != null && responseCode == IConfig.KEY_API_USER_DETAIL) {
            UserDetailResponse userDetailResponse = (UserDetailResponse) response.body();
            Log.d(TAG, " Response: " + new Gson().toJson(userDetailResponse));

            if (userDetailResponse != null) {
                int statusCode = userDetailResponse.getCode();
                String message = userDetailResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    //displayProfileInfo(userDetailResponse);

                } else {
                    requestFailed(statusCode, responseCode, message);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        } else*//*

        if (response != null && responseCode == IConfig.KEY_API_SIGN_OUT) {
            SignOutResponse signOutResponse = (SignOutResponse) response.body();

            if (signOutResponse != null) {
                int statusCode = signOutResponse.getCode();
                String message = signOutResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    signOutSuccess(signOutResponse);
                } else {
                    requestFailed(statusCode, responseCode, message);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        } else if (response != null && responseCode == IConfig.KEY_API_INSU_LIST_MEMBER) {
            InsuranceListResponse insuranceListResponse = (InsuranceListResponse) response.body();

            if (insuranceListResponse != null) {
                int statusCode = insuranceListResponse.getCode();
                String message = insuranceListResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_ACCESS_TOKEN_EXPIRED) {
                    accessTokenExpired();
                } else if (statusCode == ApiConfigs.CODE_OTTOCASH_EXPIRED) {
                    ottoCashSessionExpired();
                } else {
                    getMyInsuranceListSuccess(insuranceListResponse);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        } else if (response != null && responseCode == IConfig.KEY_API_ACTIVITY_HISTORY) {
            ActivityHistoryResponse activityHistoryResponse = (ActivityHistoryResponse) response.body();

            if (activityHistoryResponse != null) {
                int statusCode = activityHistoryResponse.getCode();
                String message = activityHistoryResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    getActivityHistorySuccess(activityHistoryResponse);
                } else {
                    requestFailed(statusCode, responseCode, message);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        } else if (response != null && responseCode == IConfig.KEY_API_BANNER) {
            BannerResponse bannerResponse = (BannerResponse) response.body();

            if (bannerResponse != null) {
                int statusCode = bannerResponse.getCode();
                String message = bannerResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    getActivityBannerSuccess(bannerResponse);
                } else {
                    requestFailed(statusCode, responseCode, message);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        }
        else if (response != null && responseCode == IConfig.KEY_API_TRANSACTION_HISTORY) {
            TransactionHistoryResponse transactionHistoryResponse = (TransactionHistoryResponse) response.body();

            if (transactionHistoryResponse != null) {
                int statusCode = transactionHistoryResponse.getCode();
                String message = transactionHistoryResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    getTransactionHistorySuccess(transactionHistoryResponse);
                } else {
                    requestFailed(statusCode, responseCode, message);
                }
            } else {
                requestFailed(ApiConfigs.CODE_SERVER_ERROR, responseCode, getString(R.string.server_error));
            }
        }
        else if (response != null && responseCode == 666) {
            DetailHistoryResponse detailHistoryResponse = (DetailHistoryResponse) response.body();
            Log.d("Response", " Response: " + new Gson().toJson(detailHistoryResponse));

            if (detailHistoryResponse != null) {
                int statusCode = detailHistoryResponse.getCode();
                String message = detailHistoryResponse.getMessage();

                if (statusCode == ApiConfigs.CODE_SUCCESS) {
                    if(detailHistoryResponse.getCode()==512){
                        showSnackBar(detailHistoryResponse.getMessage());
                    }else {
                        getDetailTransactionSuccess(detailHistoryResponse);
                    }
                } else {
                    showSnackBar(message);
                }
            } else {
                showSnackBar(getString(R.string.server_error));
            }

        }else if (response != null && responseCode == IConfig.KEY_API_INDOMARET_INQUIRY) {
            IndomaretInquiryResponse indomaretInquiryResponse = (IndomaretInquiryResponse) response.body();
            getIndomaretInquiry(indomaretInquiryResponse);
        }
    }

    private void showSnackBar(String message){
        Snackbar.make(tabLayout, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }

    @Override
    protected void onApiFailureCallback(String message, BaseActivity ac) {
        if (message != null) {
            requestFailed(ApiConfigs.CODE_TIME_OUT, 0, getString(R.string.server_error_connection));
        }
    }

    public void requestFailed(int statusCode, int requestCode, String message) {
        if (statusCode == ApiConfigs.CODE_ACCESS_TOKEN_EXPIRED) {
            accessTokenExpired();
        } else if (statusCode == ApiConfigs.CODE_OTTOCASH_EXPIRED) {
            ottoCashSessionExpired();
        } else {
            TopSnackbar.showError(this, findViewById(R.id.snackbar_container), message);

            //Firebase Write Error
            firebaseWriteError(statusCode, message);
        }
    }

    private void getMyInsuranceListSuccess(InsuranceListResponse insuranceListResponse) {
        EventBus.getDefault().postSticky(insuranceListResponse);
    }

    private void getTransactionHistorySuccess(TransactionHistoryResponse transactionHistoryResponse) {
        EventBus.getDefault().postSticky(transactionHistoryResponse);
    }

    private void getDetailTransactionSuccess(DetailHistoryResponse detailHistoryResponse) {
        EventBus.getDefault().postSticky(detailHistoryResponse);
    }

    private void getActivityHistorySuccess(ActivityHistoryResponse activityHistoryResponse) {
        EventBus.getDefault().postSticky(activityHistoryResponse);
    }

    private void getActivityBannerSuccess(BannerResponse bannerResponse) {
        EventBus.getDefault().postSticky(bannerResponse);
    }

    private void getIndomaretInquiry(IndomaretInquiryResponse indomaretInquiryResponse) {
        QrcodeFragment fragment = (QrcodeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.getIndomaretInquiry(indomaretInquiryResponse);
    }

    @Override
    public void onBackPressed() {
        if (mTabPosition > 0)
            tabSelected(0, "Beranda");
        else
            doubleBackToExit();
    }

    private void doubleBackToExit() {
        if (mDoubleBackToExitPressedOnce) {
            finish();
            finishAffinity();
            System.exit(0);
            //super.onBackPressed();
            return;
        }

        this.mDoubleBackToExitPressedOnce = true;

        Toast.makeText(this, getString(R.string.main_activity_msg_double_back_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> mDoubleBackToExitPressedOnce = false, 2000);
    }

    private JSONObject setProfileNetcore(UserDetailResponse userDetailResponse) {
        JSONObject profile = new JSONObject();

        try {
            profile.put("NAME", userDetailResponse.getData().getFullName());
            profile.put("NAMETAG", userDetailResponse.getData().getNameTag());
            profile.put("MOBILE", userDetailResponse.getData().getMobilePhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public void signOut() {
        if (Connectivity.isNetworkAvailable(this)) {
            showApiProgressDialog(Indosat.getAppComponent(), new SignOutDao(com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this) {
                @Override
                public void call() {
                    String accessToken = "";
                    if (getAccessToken() != null)
                        accessToken = getAccessToken();

                    this.getSignOutDao(com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this, accessToken,
                            BaseDao.getInstance(com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this, IConfig.KEY_API_SIGN_OUT).callback);
                }
            }, getString(R.string.loading), false);
        }
    }

    private void signOutSuccess(SignOutResponse signOutResponse) {
        mPreferences.clearToken();
        mPreferences.logOutUserDetail();
        mPreferences.setLogin(false);
        EventBus.getDefault().removeAllStickyEvents();
        Smartech.getInstance(new WeakReference<>(this)).logoutAndClearUserIdentity(true);

        Intent intent = new Intent(com.indosatooredoo.Imkasapp.ui.activity.main.MainActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private InvestmentProduct getPHLProduct() {
        InvestmentProduct investmentProduct = new InvestmentProduct();
        investmentProduct.setName(getString(R.string.title_activity_investment_pede_hasil_lebih));
        investmentProduct.setDesc("");
        investmentProduct.setImage(R.drawable.investasi_hasil_lebih);

        return investmentProduct;
    }

    public void gotoChat() {
        if (preferences.getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_NONE)
                || preferences.getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_REJECTED)) {
            startActivity(new Intent(this, UpgradeKYCActivity.class));
        } else {
            if (!mIsUserHavePFMScore) {
                if (preferences.getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_APPROVED)) {
//                    startActivity(new Intent(this, PFMChatActivity.class));
                } else if (preferences.getUserDetail().getUpgradeStatus().equals(TAG_UPGRADE_STATUS_PENDING)) {
                    showUpgradePedePendingDialog();
                } else {
                    showUpgradePedePlusDialog();
                }
            }
        }
    }

    public void gotoPHL() {
//        if (mPreferences.getPFMAssessmentHaveScore()) {
//            if (mPreferences.getUserDetail().getExtraIncomeBalance() > 0) {
//                startActivity(new Intent(this, InvestmentPedeHasilLebihDetailActivity.class));
//            } else {
//                gotoPHLIntro();
//            }
//        } else {
//            showPFMAssessmentDialog(true);
//        }
        if (mPreferences.getUserDetail().getExtraIncomeBalance() > 0) {
            startActivity(new Intent(this, InvestmentPedeHasilLebihDetailActivity.class));
        } else {
            gotoPHLIntro();
        }
    }

    public void setmIsUserHavePFMScore(boolean mIsUserHavePFMScore) {
        this.mIsUserHavePFMScore = mIsUserHavePFMScore;
        mPreferences.savePFMAssessmentHaveScore(mIsUserHavePFMScore);
    }

    public void setmIsSuccessLoadUserDetail(boolean mIsSuccessLoadUserDetail) {
        this.mIsSuccessLoadUserDetail = mIsSuccessLoadUserDetail;
    }

    public boolean ismIsUserHavePFMScore() {
        return mIsUserHavePFMScore;
    }

    public boolean ismIsSuccessLoadUserDetail() {
        return mIsSuccessLoadUserDetail;
    }

    public int getmTabPosition() {
        return mTabPosition;
    }

    public String getmFirebaseToken() {
        getFirebaseToken();
        return mFirebaseToken;
    }

    public UserDetail getUser() {
        return getUserDetail();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppKeys.GOTO_PEDECASH) {
            if (resultCode == RESULT_OK) {
                int result = data.getIntExtra(AppKeys.class.getSimpleName(), 0);
                if (result == AppKeys.CALLBACK_BAYAR) {
                    String[] menu = getResources().getStringArray(R.array.home_bottom_tab_menu);
                    tabSelected(2, menu[2]);
                }
            }
        } else if (requestCode == AppKeys.GOTO_QRCONT) {
            defaultTapView();
        }
    }

    */
/**
     * Show Alert Dialog
     *//*



    public void showComingSoonDialog() {
        comingSoonDialog();
    }

    public void showUpgradePedePlusDialog() {
        upgradePedePlusDialog();
    }

    public void showUpgradePedePendingDialog() {
        upgradePedePendingDialog();
    }

    public void showPFMAssessmentDialog(boolean isPHL) {
        pfmAssessmentDialog(isPHL);
    }

    public void showInfoSaldo() {
        infoSaldo();
    }

    public void showEmptyEmoneyBalanceDialog() {
        emptyEmoneyBalanceDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}*/
