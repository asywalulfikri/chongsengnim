/*
package sembako.sayunara.android.screen.mainmenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.home.HomeFragment;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout homeParent, offerParent, profileParent, notificationParent;
    private RelativeLayout lastSelectedView;
    private LinearLayout bottomNavigation;
    private android.support.v7.widget.Toolbar toolbar;
    private RelativeLayout bottomSheetParentLayout;
    private LinearLayout bottomSheetTopLayout;
    private LinearLayout bottomView;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private RecyclerView recyclerViewBottom;
    private BottomRecyclerViewAdapter bottomRecyclerViewAdapter;
    private boolean isNightModeOn = false;


    //testing

    private LinearLayout bookNowButtonParent;
    private LinearLayout bookNowTextParent;
    private int bookNowButtonParentWidth = 0;
    private int bookNowTextParentWidth = 0;
    private int stateOfBottomSheet = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

     */
/*   if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

            setTheme(R.style.AppThemeDark);
            isNightModeOn = true;

        } else {
            setTheme(R.style.AppTheme);
            isNightModeOn = false;

        }
*//*

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();
        initialiseListeners();
        prepareBottomRecyclerView();

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

            isNightModeOn = true;

        } else {
            isNightModeOn = false;
        }

        setUpFragment(new HomeFragment(), "home");

    }


    protected void initialiseViews() {

        bottomNavigation = findViewById(R.id.bottom_navigation);
        homeParent = findViewById(R.id.home_parent);
        profileParent = findViewById(R.id.profile_parent);
        offerParent = findViewById(R.id.discount_offer_parent);
        notificationParent = findViewById(R.id.notification_parent);
        bottomSheetParentLayout = findViewById(R.id.bottom_sheet_parent);
        bottomSheetTopLayout = findViewById(R.id.bottom_sheet_top_layout);
        bottomView = findViewById(R.id.bottom_view);
        bookNowButtonParent = findViewById(R.id.book_now_parent);
        bookNowTextParent = findViewById(R.id.text_parent);
        lastSelectedView = homeParent;

        recyclerViewBottom = findViewById(R.id.recycler_view_bottom);

        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParentLayout);
        if (bottomNavigation != null) {
            TypedValue tv = new TypedValue();

            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                mBottomSheetBehaviour.setPeekHeight(actionBarHeight);

            }

        }

        bookNowButtonParentWidth = bookNowButtonParent.getWidth();
        bookNowTextParentWidth = bookNowTextParent.getWidth();


    }

    protected void initialiseListeners() {

        homeParent.setOnClickListener(this);
        offerParent.setOnClickListener(this);
        profileParent.setOnClickListener(this);
        notificationParent.setOnClickListener(this);
        bottomView.setOnClickListener(this);

    }

    private void prepareBottomRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity2.this);
        recyclerViewBottom.setLayoutManager(linearLayoutManager);
        bottomRecyclerViewAdapter = new BottomRecyclerViewAdapter();
        recyclerViewBottom.setAdapter(bottomRecyclerViewAdapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.home_parent:

                if (!getVisibleFragment("home")) {
                    setUpBottomNavigation(homeParent);
                    setUpFragment(new HomeFragment(), "home");
                }

                break;

            case R.id.discount_offer_parent:

                if (!getVisibleFragment("offers")) {
                    setUpBottomNavigation(offerParent);
                    setUpFragment(new OffersFragment(), "offers");
                }

                break;

            case R.id.profile_parent:

                if (!getVisibleFragment("profile")) {
                    setUpBottomNavigation(profileParent);
                    setUpFragment(new ProfileFragment(), "profile");
                }

                break;

            case R.id.notification_parent:

                if (!getVisibleFragment("notification")) {
                    setUpBottomNavigation(notificationParent);
                    setUpFragment(new NotificationFragment(), "notification");
                }

                break;


            case R.id.bottom_view:


                if (isNightModeOn) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    restartApp();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();

                }


                break;


        }

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

    public void setDefaultLayoutParams() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.5f
        );

        bookNowButtonParent.setLayoutParams(param);
        bookNowTextParent.setLayoutParams(param1);
    }

    private void scaleViewWithDragValue(float slideOffset) {

        if (stateOfBottomSheet == 0) {
            bookNowButtonParent.setLayoutParams(new LinearLayout.LayoutParams(bookNowButtonParent.getWidth() + (int) (2 * slideOffset), bookNowButtonParent.getHeight()));
            bookNowTextParent.setLayoutParams(new LinearLayout.LayoutParams(bookNowTextParent.getWidth() - (int) (2 * slideOffset), bookNowButtonParent.getHeight()));

        } else {
            bookNowButtonParent.setLayoutParams(new LinearLayout.LayoutParams(bookNowButtonParent.getWidth() - (int) (2 * slideOffset), bookNowButtonParent.getHeight()));
            bookNowTextParent.setLayoutParams(new LinearLayout.LayoutParams(bookNowTextParent.getWidth() + (int) (2 * slideOffset), bookNowButtonParent.getHeight()));

        }


    }

    public int containerWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double ratio = 1.5;
        return (int) (dm.widthPixels / ratio);
    }


    private void restartApp() {

        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();


    }


    public boolean getVisibleFragment(String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        assert currentFragment.getTag() != null;
        if (currentFragment.getTag().equals(tag)) {
            return true;
        }
        return false;

    }

    */
/**
     * @param fragment fragment to load
     *//*

    private void setUpFragment(HomeFragment fragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();


    }


    public void setUpBottomNavigation(RelativeLayout view) {
        if (lastSelectedView != null) {
            for (int i = 0; i < lastSelectedView.getChildCount(); i++) {
                View view1 = lastSelectedView.getChildAt(i);
                if (view1 instanceof ImageView) {
                    ((ImageView) view1).setColorFilter(getResources().getColor(R.color.colorDefault));

                } else {
                    ((TextView) view1).setTextColor(getResources().getColor(R.color.colorDefault));
//                    ((TextView) view1).setTextSize(11);
                    setTextChangeAnimation((TextView) view1, 12, 11);


                }

            }

        }
        for (int i = 0; i < view.getChildCount(); i++) {
            View view1 = view.getChildAt(i);
            if (view1 instanceof ImageView) {
                ((ImageView) view1).setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

            } else {
                ((TextView) view1).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                setTextChangeAnimation((TextView) view1, 11, 12);
            }

        }
        lastSelectedView = view;
    }

    public void setTextChangeAnimation(final TextView textChangeAnimation, int startValue, int endValue) {

        long animationDuration = 300; // Animation duration in ms

        ValueAnimator animator = ValueAnimator.ofFloat(startValue, endValue);
        animator.setDuration(animationDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                textChangeAnimation.setTextSize(animatedValue);
            }
        });

        animator.start();
    }

    @Override
    public void onBackPressed() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
        if (homeFragment != null && homeFragment.isVisible()) {
            setUpBottomNavigation(homeParent);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    private class BottomRecyclerViewAdapter extends AppRecyclerViewAdapter {

        private static final int TYPE_SEARCH = 0;
        private static final int TYPE_STICKY = 1;
        private static final int TYPE_FAVOURITE = 2;

        @Override
        public void add(Object object) {

        }

        @Override
        public void clear() {

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_SEARCH) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_search, parent, false);
                return new VHSearch(view);

            } else if (viewType == TYPE_STICKY) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sticky, parent, false);
                return new VHSticky(view);

            } else if (viewType == TYPE_FAVOURITE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_favourite, parent, false);
                return new VHFavourite(view);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof VHFavourite){
                VHFavourite vhFavourite = (VHFavourite) holder;
                Glide.with(MainActivity.this).load(R.drawable.test2).into(vhFavourite.imageView);
            }else {

            }

        }

        @Override
        public int getItemViewType(int position) {

            if (isPositionSearch(position)) {
                return TYPE_SEARCH;
            } else if (isPositionTitleSticky(position)) {
                return TYPE_STICKY;
            }
            return TYPE_FAVOURITE;
        }

        private boolean isPositionSearch(int position) {
            return position == 0;
        }

        private boolean isPositionTitleSticky(int position) {
            return position == 1;
        }


        @Override
        public int getItemCount() {
            return 10;
        }

        private class VHSearch extends RecyclerView.ViewHolder {
            public VHSearch(View view) {
                super(view);
            }
        }

        private class VHFavourite extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public VHFavourite(View view) {
                super(view);

                imageView = view.findViewById(R.id.home_footer_image);
            }
        }

        private class VHSticky extends RecyclerView.ViewHolder {
            public VHSticky(View view) {
                super(view);
            }
        }
    }


}*/
