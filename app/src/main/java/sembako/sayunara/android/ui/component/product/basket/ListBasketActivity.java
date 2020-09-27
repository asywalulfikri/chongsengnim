package sembako.sayunara.android.ui.component.product.basket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.component.basket.Basket;
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity;
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity;
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter;
import sembako.sayunara.android.ui.component.product.listproduct.model.Product;
import sembako.sayunara.android.ui.component.product.postproduct.PostProductActivity;

import static android.view.View.GONE;

public class ListBasketActivity extends BaseActivity  {

    protected FirebaseFirestore firebaseFirestore;
    protected ArrayList<Product> productArrayList = new ArrayList<>();
    protected ProductAdapter productAdapter;
    protected ProgressBar progress_bar;
    protected LinearLayout ll_no_product;
    protected SwipeRefreshLayout swipe_refresh;
    protected FloatingActionButton floating_action_button;
    protected DocumentSnapshot mLastQueriedDocument;
    protected NestedScrollView nestedScrollView;
    protected RelativeLayout rl_load_more;
    //View
    private RecyclerView recyclerView;
    private boolean stopload = false;
    private User user;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AppCompatEditText etSearch;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);


        firebaseAuth = FirebaseAuth.getInstance();

        etSearch = findViewById(R.id.etSearchView);
        recyclerView = findViewById(R.id.recyclerView);
        progress_bar = findViewById(R.id.progress_bar);
        ll_no_product = findViewById(R.id.ll_no_product);
        swipe_refresh =findViewById(R.id.swipeRefresh);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        rl_load_more = findViewById(R.id.rl_load_more);
        floating_action_button =findViewById(R.id.floating_action_button);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // setupToolbar(toolbar,"List Produk "+ type);
        firebaseFirestore = FirebaseFirestore.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        if(isLogin()==true){
            user = getGetUsers();
            if(user.getProfile().getType().equals("admin")||user.getProfile().equals("mitra")){
                floating_action_button.setVisibility(View.VISIBLE);
            }else {
                floating_action_button.setVisibility(GONE);
            }
        }else {
            floating_action_button.setVisibility(GONE);
        }

        floating_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostProductActivity.class);
                startActivityForResult(intent,Constant.Code.CODE_LOAD);
            }
        });


        ArrayList xx = new ArrayList<Basket>();
        for( int i = 0 ; i<xx.size()-1 ; i ++){

        }


        // firebaseAuth.signInAnonymously();

        if(!isLogin()){
            firebaseAuth.signInAnonymously().addOnCompleteListener(ListBasketActivity.this, task -> {
                if(task.isSuccessful()){

                }else{

                }
            });
        }

        getList();

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        rl_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });


        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(etSearch.getText().toString()!=""){
                    mLastQueriedDocument = null;
                    rl_load_more.setVisibility(GONE);
                    productAdapter.getData().clear();

                    String text = etSearch.getText().toString();
                    String[]text1 = text.split(" ");
                    hideKeyboard();
                    Intent intent = new Intent(this,SearcListProductActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("keyword",text1[0].toLowerCase());
                    startActivityForResult(intent,11);
                }
                return true;
            }
            return false;
        });

    }
    public void refresh(){
        mLastQueriedDocument = null;
        rl_load_more.setVisibility(GONE);
        productAdapter.getData().clear();
        getList();
    }

    void automaticLoadMore() {


        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(stopload==false){
                            getList();
                        }
                    }
                }, 500);
            }
        });
    }

    void getList() {

        //ambil list product yang collection basket nya terdapat userid yg login

        firebaseFirestore.setLoggingEnabled(true);
        //CollectionReference collectionReference = firebaseFirestore.collection(Constant.Collection.COLLECTION_PRODUCT+"/"+Constant.Collection.COLLECTION_BASKET);
        //collectionReference.getFirestore().getFirestoreSettings();

        String db = Constant.Collection.COLLECTION_PRODUCT+"/"+Constant.Collection.COLLECTION_BASKET;
        DocumentReference docRef = firebaseFirestore.collection(db).document(getGetUsers().getProfile().getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("isinya bos", "DocumentSnapshot data: " + document.getData()+"---");
                    } else {
                        Log.d("isinya bos", "No such document");
                    }
                } else {
                    Log.d("isinya bos", "get failed with ", task.getException());
                }
            }
        });

    }

    private void updateList(final ArrayList<Product>historyList) {

        showList();
        progress_bar.setVisibility(GONE);
        productAdapter = new ProductAdapter(this,false);
        productAdapter.setData(historyList);
        recyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        if(productAdapter.getDataItemCount()>0){
            ll_no_product.setVisibility(GONE);
        }else {
            ll_no_product.setVisibility(View.VISIBLE);
        }
        productAdapter.actionQuestion(new ProductAdapter.OnItemClickListener() {

            @Override
            public void OnActionClickQuestion(View view, int position) {
                Product product = historyList.get(position);
                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                intent.putExtra("product",product);
                startActivityForResult(intent, Constant.Code.CODE_LOAD);
            }
        });


        automaticLoadMore();
    }

    public void showList(){
        progress_bar.setVisibility(GONE);
    }




   /* override fun onCreateOptionsMenu(menu:Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.Code.CODE_LOAD && resultCode == RESULT_OK){
            boolean load = data.getBooleanExtra(Constant.IntentExtra.isLoad,false);
            if(load){
                refresh();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

