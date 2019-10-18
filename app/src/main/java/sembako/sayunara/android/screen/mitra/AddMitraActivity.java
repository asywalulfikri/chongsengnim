package sembako.sayunara.android.screen.mitra;


import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.common.io.BaseEncoding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.UUID;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.base.BaseActivity;

public class AddMitraActivity extends BaseActivity {

    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mitra);

        addMitra();

    }



    private void addMitra() {
        Long tsLong = System.currentTimeMillis() / 1000;
        final String uuid = UUID.randomUUID().toString();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        Map<String, Object> obj = new HashMap<>();

        obj.put("store_name", "Maju Mundur");
        obj.put("userId", "123456");
        obj.put("storeId",uuid);
        obj.put("addressDetail", "Tiku Indonesia");
        obj.put("images", "https://www.chainstoreage.com/wp-content/uploads/2018/11/AdoreMe_store.jpg");
        obj.put("totalSell","0");
        obj.put("rating","0");
        Map<String, Object> contentData = new HashMap<>();
        contentData.put("latitude", "");
        contentData.put("longitude", "");
        obj.put("coordinate", contentData);
        obj.put("approval",false);
        obj.put("status","nonaktif");

        obj.put("custom-androidVersion", getVersionName(this));
        obj.put("custom-appVersion", String.valueOf(Build.VERSION.SDK_INT));

        Map<String, Object> datePublishedData = new HashMap<>();
        datePublishedData.put("iso", nowAsISO);
        datePublishedData.put("timestamp", tsLong);
        obj.put("createdAt", datePublishedData);

        Map<String, Object> dateSubmittedData = new HashMap<>();
        dateSubmittedData.put("iso", nowAsISO);
        dateSubmittedData.put("timestamp", tsLong);
        obj.put("updatedAt", dateSubmittedData);


        firebaseFirestore.collection("stores").document(uuid)
                .set(obj)
                .addOnCompleteListener(task -> {

                    Toast.makeText(AddMitraActivity.this,"sukses",Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMitraActivity.this,"gagal",Toast.LENGTH_SHORT).show();
                });

    }
}
