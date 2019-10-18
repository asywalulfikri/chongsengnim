package sembako.sayunara.android.screen.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.mainmenu.banner.FlyBanner;
import sembako.sayunara.android.screen.mitra.ListMitraActivity;
import sembako.sayunara.android.screen.product.detailproduct.DetailProductActivity;
import sembako.sayunara.android.screen.product.listproduct.ListProductActivity2;
import sembako.sayunara.android.screen.product.listproduct.adapter.ProductAdapter2;
import sembako.sayunara.android.screen.product.listproduct.model.Product;


public class HomeFragment extends Fragment {

    private FlyBanner mBannerLocal;
    private LinearLayout ll_meat, ll_drinks, ll_seasoning, ll_fruits, ll_basic_food, ll_vegetables;
    private RecyclerView recyclerView;
    protected ProductAdapter2 productAdapter;
    protected FirebaseFirestore firebaseFirestore;
    //Menu
    private LinearLayout linear_mitra;
    private boolean isLoad = true;

    protected ArrayList<Product> productArrayList = new ArrayList<>();

    public static String[] titles = new String[]{
            "Sayur Murah dan Segar",
            "Install Lewat PlayStore",
            "Sayur Murah dan Segar",
            "Sayur Murah dan Segar",
            "Sayur Murah dan Segar",
            "Sayur Murah dan Segar",
    };


    public static String[] urls = new String[]{//750x500
            "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/banner%2Fbanner%20apps.jpg?alt=media&token=411b8b35-d764-423f-9ba7-425b5a4fa1df",
            "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/banner%2Fbanner2.jpg?alt=media&token=35a626e2-39a6-434e-9823-518d6072ec64",
            "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/banner%2Fbanner2.jpg?alt=media&token=35a626e2-39a6-434e-9823-518d6072ec64",
            "https://firebasestorage.googleapis.com/v0/b/sayunara-483b4.appspot.com/o/banner%2Fbanner2.jpg?alt=media&token=35a626e2-39a6-434e-9823-518d6072ec64"
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        linear_mitra = view.findViewById(R.id.linear_mitra);
        ll_meat = view.findViewById(R.id.ll_meat);
        ll_drinks = view.findViewById(R.id.ll_drinks);
        ll_seasoning = view.findViewById(R.id.ll_seasoning);
        ll_fruits = view.findViewById(R.id.ll_fruits);
        ll_basic_food = view.findViewById(R.id.ll_basic_food);
        ll_vegetables = view.findViewById(R.id.ll_vegetables);
        recyclerView = view.findViewById(R.id.recyclerview);
        firebaseFirestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);

        getList();

        ll_meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Daging");
            }
        });


        ll_drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Minuman");
            }
        });

        ll_seasoning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Bumbu");
            }
        });

        ll_fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Buah");
            }
        });


        ll_basic_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Sembako");
            }
        });

        ll_vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent("Sayuran");
            }
        });
        mBannerLocal = view.findViewById(R.id.banner_1);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            list.add(urls[i]);
        }

        mBannerLocal.setImagesUrl(list);


        linear_mitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListMitraActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void intent(String type) {

        Intent intent = new Intent(getActivity(), ListProductActivity2.class);
        intent.putExtra("type", type);
        startActivity(intent);

    }

    void getList() {

        CollectionReference collectionReference = firebaseFirestore.collection("product");
        Query query = collectionReference
                .whereEqualTo("isActive", true)
                .whereEqualTo("isHighLight", true)
                .limit(10)
                .orderBy("createdAt.timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Product product = new Product();
                        product.setDescription(doc.getString("description"));
                        product.setName(doc.getString("name"));
                        product.setPrice(doc.getLong("price"));
                        product.setDiscount(doc.getLong("discount"));
                        product.setId(doc.getString("id"));
                        product.setImages((ArrayList<String>) doc.get("images"));
                        productArrayList.add(product);
                    }
                    updateList(productArrayList);
                    isLoad = false;

                } else {
                    Toast.makeText(getActivity(), "gagal" + task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d("urlnya", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void updateList(final ArrayList<Product>historyList) {
        productAdapter = new ProductAdapter2(getActivity());
        productAdapter.setData(historyList);
        recyclerView.setAdapter(productAdapter);

        productAdapter.actionQuestion(new ProductAdapter2.OnItemClickListener() {

            @Override
            public void OnActionClickQuestion(View view, int position) {
                Product product = historyList.get(position);
                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                intent.putExtra("product",product);
                startActivity(intent);
            }
        });

    }
}