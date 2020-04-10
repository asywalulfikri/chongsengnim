package sembako.sayunara.android.ui.component.product.detailproduct;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.product.detailproduct.widget.FlyBanner;
import sembako.sayunara.android.ui.component.product.listproduct.model.Product;

public class DetailProductActivity extends BaseActivity {

    Product product;
    private FlyBanner mBannerLocal;
    protected TextView tv_product_name;
    protected TextView tv_product_price;
    protected TextView tv_product_description;
    protected TextView tv_product_stock;
    protected TextView textview_product_total_sell;
    protected AppCompatButton button_submit;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar(toolbar);


       // ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
      //  layoutParams.height = (int) (parent.getHeight() * 0.3);
       // itemView.setLayoutParams(layoutParams);
        tv_product_name = findViewById(R.id.tvProductName);
        tv_product_price = findViewById(R.id.tvProductPrice);
        tv_product_description = findViewById(R.id.tvProductDescription);
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
                   /* TotalCount totalCount = new TotalCount();
                    HashMap<String, Object> total = (HashMap<String, Object>) task.getResult().getData().get("count");
                    totalCount.setTotalSell((long) total.get("totalSell"));*/
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
        //textview_product_total_sell.setText(convertPrice(Integer.parseInt(String.valueOf(product.getCount().getTotalSell()))));
        tv_product_stock.setText(convertPrice(Integer.parseInt(String.valueOf(product.getStock()))));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{
                    PackageManager packageManager = getActivity().getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String message = "Hallo admin sayunara saya ingin membeli "+ product.getName()+ " mohon di response segera";
                    String url = "https://api.whatsapp.com/send?phone="+ "6282298099577" +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }else {
                       // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    }
                } catch(Exception e) {
                    Log.e("ERROR WHATSAPP",e.toString());
                   // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }


           /*     Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hallo admin sayunara saya ingin membeli "+ product.getName()+ " mohon di response segera");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(sendIntent, ""));
                startActivity(sendIntent);
                //opens the portfolio details class*/
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
