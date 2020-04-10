package sembako.sayunara.android.ui.component.product.listproduct;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.admin.product.postproduct.AddProductActivity;
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.component.account.register.RegisterActivity;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.product.detailproduct.DetailProductActivity;
import sembako.sayunara.android.ui.component.product.listproduct.adapter.ProductAdapter;
import sembako.sayunara.android.ui.component.product.listproduct.model.Product;

import static android.view.View.GONE;

public class ListProductActivity extends BaseActivity  {

    protected FirebaseFirestore firebaseFirestore;
    protected ArrayList<Product> productArrayList = new ArrayList<>();
    protected ProductAdapter productAdapter;
    protected ProgressBar progress_bar;
    protected LinearLayout ll_no_product;
    protected SwipeRefreshLayout swipe_refresh;
    protected FloatingActionButton floating_action_button;
    protected DocumentSnapshot mLastQueriedDocument;
    protected NestedScrollView nestedScrollView;
    protected String type;
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

        type = getIntent().getStringExtra("type");
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        progress_bar = findViewById(R.id.progress_bar);
        ll_no_product = findViewById(R.id.ll_no_product);
        swipe_refresh =findViewById(R.id.swipe_refresh);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        rl_load_more = findViewById(R.id.rl_load_more);
        floating_action_button =findViewById(R.id.floating_action_button);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar,"List Produk "+ type);
        firebaseFirestore = FirebaseFirestore.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        if(isLogin()==true){
            user = getUser();
            if(user.getType().equals("admin")||user.equals("mitra")){
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
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivityForResult(intent,1313);
            }
        });



   firebaseAuth.signInWithEmailAndPassword("sayunara@gmail.com","123456")
                .addOnCompleteListener(ListProductActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           setToast("sukses");
                            getList(type);
                        }else{
                            //Log.i("FirebaseAuth", task.getException().toString());
                        }
                    }
                });

        //deslogando o usuario

        //Verificar se o usuario esta logado
        if(firebaseAuth.getCurrentUser()!=null){
            Log.i("usuarioLogado", "Usuario Logado");
        }else {
            Log.i("usuarioLogado", "Usuario Deslogado");
        }


       // firebaseAuth.signInAnonymously();
        firebaseAuth.signInAnonymously().addOnCompleteListener(ListProductActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    setToast("sukses");
                    //getList(type);
                }else{
                    //Log.i("FirebaseAuth", task.getException().toString());
                }
            }
        });
        getList(type);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        rl_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList(type);
            }
        });

    }
    public void refresh(){
        mLastQueriedDocument = null;
        rl_load_more.setVisibility(GONE);
        productAdapter.getData().clear();
        getList(type);
    }

    void automaticLoadMore() {


        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(stopload==false){
                            getList(type);
                        }
                    }
                }, 500);
            }
        });
    }

    void getList(String type) {

        firebaseFirestore.setLoggingEnabled(true);
        CollectionReference collectionReference = firebaseFirestore.collection("product");
        collectionReference.getFirestore().getFirestoreSettings();
        Query query = null;
        if(mLastQueriedDocument!=null){
            Log.d("urlnya", "pakai ini ");
            query = collectionReference.whereArrayContains("type",type)
                    .whereEqualTo("isActive",true)
                    .orderBy("createdAt.timestamp",Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(mLastQueriedDocument);

        }else {
            Log.d("urlnya", "pakai ono ");
            query = collectionReference.whereArrayContains("type",type)
                    .whereEqualTo("isActive",true)
                    .limit(10)
                    .orderBy("createdAt.timestamp",Query.Direction.DESCENDING);

        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                     for (DocumentSnapshot doc : task.getResult()) {
                         Product product = doc.toObject(Product.class);
                       /* Product product = new Product();
                        // product.setCreatedAt(doc.getString("createdAt"));
                        product.setDescription(doc.getString("description"));
                        product.setName(doc.getString("name"));
                        product.setPrice(doc.getLong("price"));
                        product.setDiscount(doc.getLong("discount"));
                        product.setId(doc.getString("id"));
                        product.setUnit(doc.getString("unit"));
                        product.setImages((ArrayList<String>) doc.get("images"));
                        // int ultimaVersion = (int) dataSnapshot.getValue();*/
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
        progress_bar.setVisibility(GONE);
        productAdapter = new ProductAdapter(this,false);
        productAdapter.setData(historyList);
        recyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();

        if(productAdapter.getItemCount()==0){
            ll_no_product.setVisibility(View.VISIBLE);
        }
        productAdapter.actionQuestion(new ProductAdapter.OnItemClickListener() {

            @Override
            public void OnActionClickQuestion(View view, int position) {
                Product product = historyList.get(position);
                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                intent.putExtra("product",product);
                startActivity(intent);
            }
        });


        automaticLoadMore();
    }

    public void showList(){
        progress_bar.setVisibility(GONE);
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
        if(requestCode == 1313 && resultCode == RESULT_OK){
            boolean load = data.getBooleanExtra("load",false);
            Log.d("loadnya",String.valueOf(load));
            if(load==true){
                refresh();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
