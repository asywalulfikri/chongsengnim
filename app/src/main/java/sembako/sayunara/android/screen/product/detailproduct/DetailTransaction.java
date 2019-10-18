package sembako.sayunara.android.screen.product.detailproduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.base.BaseActivity;
import sembako.sayunara.android.screen.product.listproduct.model.Product;
import sembako.sayunara.android.screen.profile.model.User;

public class DetailTransaction extends BaseActivity {
    int minteger = 1;
    Product product;
    protected ImageView iv_product;
    protected TextView tv_product_name;
    protected TextView tv_product_price;
    protected TextView tv_total_payment;
    protected int total = 0;
    int price = 0;
    protected AppCompatButton button_submit;
    protected User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar(toolbar);

        if(isLogin==true){
           user = getUser();
        }

        product = (Product)getIntent().getSerializableExtra("product");

        iv_product = findViewById(R.id.iv_product);
        tv_product_name =findViewById(R.id.tv_product_name);
        tv_product_price =findViewById(R.id.tv_product_price);
        tv_total_payment= findViewById(R.id.tv_total_payment);
        button_submit =findViewById(R.id.button_submit);

        price = Integer.parseInt(String.valueOf(product.getPrice()));
        updateview();
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin==false){
                    Toast.makeText(getActivity(),"Anda harus login untuk menyelesaikan transaksi",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    createOrder();
                }
            }
        });


    }

    public void  updateview(){
        Glide.with(this)
                .load(product.getImages().get(0))
                .centerCrop()
                .into(iv_product);
        tv_product_name.setText(product.getName());
        tv_product_price.setText("Rp"+convertPrice(Integer.parseInt(String.valueOf(product.getPrice()))));

        total = price;
        tv_total_payment.setText("Rp"+String.valueOf(convertPrice(total)));
    }

    public void updatePrice(){
        tv_total_payment.setText("Rp"+convertPrice(Integer.parseInt(String.valueOf(total))));
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
        total = price*minteger;
        updatePrice();

    }public void decreaseInteger(View view) {
        if(minteger!=1){
            minteger = minteger - 1;
            display(minteger);
            total = price*minteger;
            updatePrice();
        }
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private void createOrder() {
        Long tsLong = System.currentTimeMillis() / 1000;
        final String uuid = UUID.randomUUID().toString();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        Map<String, Object> obj = new HashMap<>();

        obj.put("userId",user.getUserId());
        obj.put("productId", product.getId());
        obj.put("id",uuid);
        obj.put("type", "order");
        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("latitude",latitude);
        coordinate.put("longitude", longitude);
        obj.put("coordinate",coordinate);
        obj.put("payment_status",false);
        obj.put("discount",0);
        obj.put("price",product.getPrice());
        obj.put("timestamp",timestamp);
        obj.put("rekening","23493840938492");


        String informasi = Build.MANUFACTURER
                + " " + Build.MODEL + " , Versi OS :" + Build.VERSION.RELEASE;

        Map<String, Object> phone = new HashMap<>();
        phone.put("androidVersion", getVersionName(this));
        phone.put("appVersion", String.valueOf(Build.VERSION.SDK_INT));
        phone.put("phoneName",informasi);
        obj.put("phoneDetail", phone);


        Map<String, Object> datePublishedData = new HashMap<>();
        datePublishedData.put("iso", nowAsISO);
        datePublishedData.put("timestamp", tsLong);
        obj.put("createdAt", datePublishedData);

        Map<String, Object> dateSubmittedData = new HashMap<>();
        dateSubmittedData.put("iso", nowAsISO);
        dateSubmittedData.put("timestamp", tsLong);
        obj.put("updatedAt", dateSubmittedData);


        firebaseFirestore.collection("purchase").document(uuid)
                .set(obj)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Produk berhasil di tambah", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("load",true);
                    setResult(RESULT_OK,intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Gagal menambah produk",Toast.LENGTH_SHORT).show();
                });

    }


}
