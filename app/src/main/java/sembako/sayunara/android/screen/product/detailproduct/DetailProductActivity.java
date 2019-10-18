package sembako.sayunara.android.screen.product.detailproduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.product.detailproduct.widget.FlyBanner;
import sembako.sayunara.android.screen.product.listproduct.model.Product;
import sembako.sayunara.android.screen.product.listproduct.model.TotalCount;

public class DetailProductActivity extends BaseActivity {

    Product product;
    private FlyBanner mBannerLocal;
    protected TextView tv_product_name;
    protected TextView tv_product_price;
    protected TextView tv_product_description;
    protected TextView tv_product_stock;
    protected TextView textview_product_total_sell;
    protected AppCompatButton button_submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar(toolbar);


        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_price = findViewById(R.id.tv_product_price);
        tv_product_description = findViewById(R.id.tv_product_description);
        tv_product_stock = findViewById(R.id.tv_product_stock);
        textview_product_total_sell =findViewById(R.id.textview_product_total_sell);
        button_submit = findViewById(R.id.button_submit);

        product = (Product)getIntent().getSerializableExtra("product");
        mBannerLocal = findViewById(R.id.banner_1);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < product.getImages().size(); i++) {
            list.add(product.getImages().get(i));
        }

        mBannerLocal.setImagesUrl(list);
        getDetail();
    }


    public void getDetail(){
        firebaseFirestore.collection("product").document(product.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    Product product = new Product();
                    // product.setCreatedAt(doc.getString("createdAt"));
                    product.setDescription(task.getResult().getString("description"));
                    product.setName(task.getResult().getString("name"));
                    product.setPrice(task.getResult().getLong("price"));
                    product.setStock(task.getResult().getLong("stock"));
                    product.setDiscount(task.getResult().getLong("discount"));
                    TotalCount totalCount = new TotalCount();
                    HashMap<String, Object> total = (HashMap<String, Object>) task.getResult().getData().get("count");
                    totalCount.setTotalSell((long) total.get("totalSell"));
                    product.setId(task.getResult().getString("id"));
                    product.setImages((ArrayList<String>) task.getResult().get("images"));
                    updateview(product);
                }else {

                }
            }
        });
    }


    public void updateview(final Product product){

        tv_product_name.setText(product.getName());
        tv_product_price.setText("Rp"+convertPrice(Integer.parseInt(String.valueOf(product.getPrice()))));
        tv_product_description.setText(product.getDescription());
        textview_product_total_sell.setText(convertPrice(Integer.parseInt(String.valueOf(product.getCount().getTotalSell()))));
        tv_product_stock.setText(convertPrice(Integer.parseInt(String.valueOf(product.getStock()))));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),DetailTransaction.class);
                intent.putExtra("product",product);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
