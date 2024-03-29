package sembako.sayunara.product.list;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity;
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter;
import sembako.sayunara.android.ui.component.product.listproduct.model.Product;

public class ListProductActivity extends BaseActivity implements ProductAdapter.OnClickListener  {

    protected FirebaseFirestore firebaseFirestore;
    protected ArrayList<Product> productArrayList = new ArrayList<>();
    protected ProductAdapter productAdapter;
    protected LinearLayout layout_progress;
    protected RelativeLayout layout_empty;
    protected SwipeRefreshLayout swipe_refresh;
    protected DocumentSnapshot mLastQueriedDocument;
    protected NestedScrollView nestedScrollView;
    protected String keyword;
    protected RelativeLayout rl_load_more;
    //View
    private RecyclerView recyclerView;
    private boolean stopload = false;
    private String type;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AppCompatEditText etSearch;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        if(getIntent().hasExtra("keyword")){
            keyword = getIntent().getStringExtra("keyword");
        }
        type = getIntent().getStringExtra("type");

        firebaseAuth = FirebaseAuth.getInstance();

        etSearch = findViewById(R.id.etSearchView);
        recyclerView = findViewById(R.id.recyclerView);
        layout_progress = findViewById(R.id.layoutProgress);
        layout_empty = findViewById(R.id.layoutEmpty);
        swipe_refresh =findViewById(R.id.swipeRefresh);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        rl_load_more = findViewById(R.id.rlLoadMore);
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


        // firebaseAuth.signInAnonymously();

        if(!isLogin()){
            firebaseAuth.signInAnonymously().addOnCompleteListener(ListProductActivity.this, task -> {
                if(task.isSuccessful()){

                }else{

                }
            });
        }

        getList(keyword);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        rl_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList(keyword);
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
                    Intent intent = new Intent(this, SearcListProductActivity.class);
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
        getList(keyword);
    }

    void automaticLoadMore() {


        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(stopload==false){
                            getList(keyword);
                        }
                    }
                }, 500);
            }
        });
    }

    void getList(String keyword) {

        firebaseFirestore.setLoggingEnabled(true);
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.Collection.COLLECTION_PRODUCT);
        collectionReference.getFirestore().getFirestoreSettings();
        Query query = null;
        if(mLastQueriedDocument!=null){
            Log.d("urlnya", "pakai ini ");

            if(type.equals("all")){
                query = collectionReference.whereEqualTo("status.active",true)
                        .orderBy("createdAt.timestamp",Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument);
            }else {
                query = collectionReference.whereArrayContains("type",keyword)
                        .whereEqualTo("status.active",true)
                        .orderBy("createdAt.timestamp",Query.Direction.DESCENDING)
                        .limit(10)
                        .startAfter(mLastQueriedDocument);
            }


        }else {
            if(type.equals("all")){
                query = collectionReference.whereEqualTo("status.active",true)
                        .limit(10)
                        .orderBy("createdAt.timestamp",Query.Direction.DESCENDING);
            }else {
                query = collectionReference.whereArrayContains("type",keyword)
                        .whereEqualTo("active",true)
                        .limit(10)
                        .orderBy("createdAt.timestamp",Query.Direction.DESCENDING);
            }

        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Product product = doc.toObject(Product.class);
                        productArrayList.add(product);
                    }
                    Log.d("total",String.valueOf(productArrayList.size()));

                    if(mLastQueriedDocument==null){
                        if(task.getResult().size()<=9){
                            rl_load_more.setVisibility(GONE);
                            stopload = true;

                        }else {
                            stopload = false;
                            rl_load_more.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if(task.getResult().size()<=9){
                            rl_load_more.setVisibility(GONE);
                            stopload = true;

                        }else {
                            stopload = false;
                            rl_load_more.setVisibility(View.VISIBLE);
                        }
                    }
                    swipe_refresh.setRefreshing(false);
                    updateList(productArrayList);

                    if(task.getResult().size()!=0){
                        mLastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                    }

                } else {
                    Toast.makeText(getActivity(),"gagal"+ task.getException(),Toast.LENGTH_SHORT).show();
                    Log.d("urlnya", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void updateList(final ArrayList<Product>historyList) {

        showList();
        layout_progress.setVisibility(GONE);
        productAdapter = new ProductAdapter(this,false,false,true,this);
        productAdapter.setData(historyList);
        recyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        if(productAdapter.getDataItemCount()>0){
            layout_empty.setVisibility(GONE);
        }else {
            layout_empty.setVisibility(View.VISIBLE);
        }

        automaticLoadMore();
    }

    public void showList(){
        layout_progress.setVisibility(GONE);
    }



    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


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

    @Override
    public void onClickDetail(int position, @NonNull Product product) {
        setToast("kilik");
       /* Intent intent = new Intent(getActivity(), DetailProductActivity.class);
        intent.putExtra("product",product);
        startActivityForResult(intent, Constant.Code.CODE_LOAD);*/
    }
}

