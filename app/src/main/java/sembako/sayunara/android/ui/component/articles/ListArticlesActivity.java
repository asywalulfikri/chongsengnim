package sembako.sayunara.android.ui.component.articles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.component.product.listproduct.SearcListProductActivity;
import sembako.sayunara.android.ui.component.product.postproduct.PostProductActivity;

import static android.view.View.GONE;

public class ListArticlesActivity extends BaseActivity implements ArticlesAdapter.OnClickListener  {

    protected FirebaseFirestore firebaseFirestore;
    protected ArrayList<Articles> articlesArrayList = new ArrayList<>();
    protected ArticlesAdapter articlesAdapter;
   // protected ProgressBar progress_bar;
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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);


        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
      //  progress_bar = findViewById(R.id.progress_bar);
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


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
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


        // firebaseAuth.signInAnonymously();

        if(!isLogin()){
            firebaseAuth.signInAnonymously().addOnCompleteListener(ListArticlesActivity.this, task -> {
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




    }
    public void refresh(){
        mLastQueriedDocument = null;
        rl_load_more.setVisibility(GONE);
        articlesAdapter.getData().clear();
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
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.Collection.COLLECTION_ARTICLES);
        collectionReference.getFirestore().getFirestoreSettings();
        Query query = null;
        if(mLastQueriedDocument!=null){
            Log.d("urlnya", "pakai ini ");

            query = collectionReference.whereEqualTo("isActive",true)
                   // .orderBy("createdAt",Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument);


        }else {
            query = collectionReference.whereEqualTo("isActive",true)
                    .limit(10);
                   // .orderBy("createdAt",Query.Direction.DESCENDING);

        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Articles articles = doc.toObject(Articles.class);
                        articlesArrayList.add(articles);
                    }
                    Log.d("total",String.valueOf(articlesArrayList.size()));

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
                    updateList(articlesArrayList);

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

    private void updateList(final ArrayList<Articles>historyList) {

        showList();
      //  progress_bar.setVisibility(GONE);
        articlesAdapter = new ArticlesAdapter();
        articlesAdapter.setItems(historyList,this);
        recyclerView.setAdapter(articlesAdapter);
        articlesAdapter.notifyDataSetChanged();
        if(articlesAdapter.getItemCount()>0){
            ll_no_product.setVisibility(GONE);
        }else {
            ll_no_product.setVisibility(View.VISIBLE);
        }
        /*articlesAdapter.actionQuestion(new ProductAdapter.OnItemClickListener() {

            @Override
            public void OnActionClickQuestion(View view, int position) {
                Product product = historyList.get(position);
                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                intent.putExtra("product",product);
                startActivityForResult(intent, Constant.Code.CODE_LOAD);
            }
        });*/


        automaticLoadMore();
    }

    public void showList(){
       // progress_bar.setVisibility(GONE);
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

    @Override
    public void onClickArticles(int position) {

    }
}

