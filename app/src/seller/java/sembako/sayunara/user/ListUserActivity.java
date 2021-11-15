package sembako.sayunara.user;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import sembako.sayunara.adapter.UserAdapter;
import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity;

public class ListUserActivity extends AppCompatActivity implements UserAdapter.OnClickListener{

    protected FirebaseFirestore firebaseFirestore;
    protected ArrayList<User> productArrayList = new ArrayList<>();
    protected UserAdapter userAdapter;
    protected LinearLayout layout_progress;
    protected RelativeLayout layout_empty;
    protected SwipeRefreshLayout swipe_refresh;
    protected DocumentSnapshot mLastQueriedDocument;
    protected NestedScrollView nestedScrollView;
    protected RelativeLayout rl_load_more;
    //View
    private RecyclerView recyclerView;
    private boolean stopload = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AppCompatEditText etSearch;
    private TextView pbText;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        firebaseAuth = FirebaseAuth.getInstance();

        etSearch = findViewById(R.id.etSearchView);
        recyclerView = findViewById(R.id.recyclerView);
        layout_progress = findViewById(R.id.layout_progress);
        layout_empty = findViewById(R.id.layout_empty);
        swipe_refresh =findViewById(R.id.swipeRefresh);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        rl_load_more = findViewById(R.id.rlLoadMore);
        ImageView ivBack = findViewById(R.id.ivBack);
        pbText = findViewById(R.id.pbText);
        ivBack.setVisibility(View.VISIBLE);

        etSearch.setHint("Cari User By email / Phone");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // setupToolbar(toolbar,"List Produk "+ type);
        firebaseFirestore = FirebaseFirestore.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);


        // firebaseAuth.signInAnonymously();

       /* if(!isLogin()){
            firebaseAuth.signInAnonymously().addOnCompleteListener(ListUserActivity.this, task -> {
                if(task.isSuccessful()){

                }else{

                }
            });
        }*/

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
                    userAdapter.getData().clear();

                    String text = etSearch.getText().toString();
                    String[]text1 = text.split(" ");
                    //hideKeyboard();
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
        userAdapter.getData().clear();
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

        FirebaseFirestore.setLoggingEnabled(true);
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.Collection.COLLECTION_USER);
        collectionReference.getFirestore().getFirestoreSettings();
        Query query = null;
        if(mLastQueriedDocument!=null){
            Log.d("urlnya", "pakai ini ");

            query = collectionReference
                    .whereEqualTo("profile.active",true)
                    .orderBy("createdAt.timestamp",Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument);

        }else {
            query = collectionReference
                    .whereEqualTo("profile.active",true)
                    .limit(10)
                    .orderBy("createdAt.timestamp",Query.Direction.DESCENDING);

        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("total1",task.getResult().toString()+"--");
                    for (DocumentSnapshot doc : task.getResult()) {
                        User product = doc.toObject(User.class);
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
                   // Toast.makeText(this,"gagal"+ task.getException(),Toast.LENGTH_SHORT).show();
                    Log.d("urlnya", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void updateList(final ArrayList<User>arrayList) {

        showList();
        layout_progress.setVisibility(GONE);
        userAdapter = new UserAdapter(getApplicationContext(),this);
        userAdapter.setData(arrayList);
        recyclerView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
        if(userAdapter.getDataItemCount()>0){
            layout_empty.setVisibility(GONE);
        }else {
            layout_empty.setVisibility(View.VISIBLE);
            pbText.setText("User tidak di temukan");
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
    public void onClickDetail(int position, User user) {
        Intent intent = new Intent(this,UserDetailActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}

