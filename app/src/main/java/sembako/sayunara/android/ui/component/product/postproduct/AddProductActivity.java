package sembako.sayunara.android.ui.component.product.postproduct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.base.BaseActivity;
import sembako.sayunara.android.ui.component.product.postproduct.adapter.PodImageAdapter;
import sembako.sayunara.android.ui.component.product.postproduct.model.PodImage;
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.util.FileChooserHelper;
import sembako.sayunara.android.ui.util.PermissionHelper;

public class AddProductActivity extends BaseActivity implements View.OnClickListener,PodImageAdapter.DeletedItemListener{

    protected Toolbar toolbar;
    protected EditText et_name_product,et_type_product,et_price_product,et_weight_product,et_satuan_product,et_available_product,et_stock_product,et_description_product;
    protected AppCompatButton btn_Submit;
    protected RecyclerView recycler_photo;
    protected String name,type,weight,satuan,price,stock,description;
    private SharedPreferences permissionLokasi;

    List<String> urlsphoto = new ArrayList<>();

    //formatprices
    private NumberFormat formatter;
    private String clean;
    private double parsed;
    private String cleanString;
    private User user;
    protected ImageView iv_add_image;
    ArrayList<PodImage> mImageListPhoto = new ArrayList<>();
    public static final int PICK_IMAGE = 7458;
    public static final int CAPTURE_IMAGE = 7459;

    AlertDialog.Builder  builder ;
    String[] tagsList = new String[]{
            "Daging","Minuman","Bumbu","Buah","Sayuran"
    };
    boolean[] isSelectedArray;
    private ArrayList<Integer> mSelectedItems;
    private ArrayList<String> typenya = new ArrayList<>();
    PodImageAdapter mAdapterPhoto;

    private int imageCounter = 0;
    private StorageReference folderRef;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("memproses data");

        isSelectedArray = new boolean[200];
        Arrays.fill(isSelectedArray, Boolean.FALSE);
        builder =new AlertDialog.Builder(getActivity());
        permissionLokasi = getSharedPreferences("permissionLokasi", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            } else {
               // getGpsTracker();
            }
        } else {
            //getGpsTracker();
        }

        user = getUser();
        //firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        folderRef = storageRef.child("image_product");
        toolbar = findViewById(R.id.toolbar);
        toolbar(toolbar);



        et_name_product   = findViewById(R.id.etProductName);
        et_type_product   = findViewById(R.id.etProductType);
        et_weight_product = findViewById(R.id.etProductWeight);
        et_satuan_product = findViewById(R.id.etProductUnit);
        et_available_product = findViewById(R.id.etProductAvailable);
        et_price_product = findViewById(R.id.etProductPrice);
        et_stock_product = findViewById(R.id.etProductStock);
        et_description_product = findViewById(R.id.etProductDescription);
        recycler_photo = findViewById(R.id.recycler_photo);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_photo.setLayoutManager(layoutManager);
        mAdapterPhoto = new PodImageAdapter(getActivity(), mImageListPhoto, this,"photo");
        recycler_photo.setAdapter(mAdapterPhoto);


        iv_add_image = findViewById(R.id.ivImage);
        iv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage();
            }
        });


        btn_Submit        = findViewById(R.id.btnSubmit);


        et_type_product.setOnClickListener(v -> typedialog());
        et_satuan_product.setOnClickListener(v -> satuandialog());


        et_stock_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String current = "";
                if (!s.toString().equals(current)) {
                    et_stock_product.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    cleanString = s.toString().replaceAll(replaceable,
                            "");


                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    formatter = NumberFormat
                            .getCurrencyInstance(local);
                    //                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    et_stock_product.setText(clean);
                    et_stock_product.setSelection(clean.length());
                    et_stock_product.addTextChangedListener(this);
                }
            }

        });


        et_price_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String current = "";
                if (!s.toString().equals(current)) {
                    et_price_product.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    cleanString = s.toString().replaceAll(replaceable,
                            "");


                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    formatter = NumberFormat
                            .getCurrencyInstance(local);
                    //                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    et_price_product.setText(clean);
                    et_price_product.setSelection(clean.length());
                    et_price_product.addTextChangedListener(this);
                }
            }

        });

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name_product.getText().toString().trim();
                type = et_type_product.getText().toString().trim();
                weight = et_weight_product.getText().toString().trim();
                satuan = et_satuan_product.getText().toString().trim();
                price = et_price_product.getText().toString().trim().replace(".","");
                stock = et_stock_product.getText().toString().trim().replace(".","");
                description = et_description_product.getText().toString();

                if(name.equals("")||type.equals("")||weight.equals("")||satuan.equals("")||description.equals("")||stock.equals("")||price.equals("")){
                    Toast.makeText(getActivity(),"Silakan isi semua form",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    uploadImages(mImageListPhoto, getActivity());
                }
            }
        });

    }

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    // Check for camera permission in MashMallow
    public void requestForCameraPermission() {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                // Show permission rationale
            } else {
                // Handle the result in Activity#onRequestPermissionResult(int, String[], int[])
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
            startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
        }
    }

    public void takeImage() {

        if (mImageListPhoto.size() > 2) {
            Toast.makeText(this, "Maksimal Foto 3", Toast.LENGTH_SHORT).show();
        } else {
            androidx.appcompat.widget.PopupMenu popup = new PopupMenu(this, iv_add_image);
            popup.inflate(R.menu.menu_photo_chooser);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_browse:
                        EasyImage.openGallery(this, PICK_IMAGE);
                        break;
                    case R.id.action_take:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PermissionHelper.requestCameraStorage(getActivity(), accepted -> {
                                if (accepted) {
                                    Intent intent = new Intent(this, CameraActivity.class);
                                    startActivityForResult(intent, CAPTURE_IMAGE);

                                } else {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 123);
                                    Toast.makeText(getActivity().getBaseContext(), "Aktifkan izin kamera dan storage  untuk menggunakan fitur ini", Toast.LENGTH_LONG).show();
                                }

                            });
                        } else {
                            Intent intent = new Intent(this, CameraActivity.class);
                            startActivityForResult(intent, CAPTURE_IMAGE);
                        }
                        break;
                }
                return true;
            });
            popup.show();
        }


    }

    // Receive Uri of saved square photo
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE){
            String path = data.getStringExtra("path");
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            PodImage podImage = new PodImage();
            podImage.setSource(uri);
            mImageListPhoto.add(podImage);
            mAdapterPhoto.notifyDataSetChanged();
        }
        if (resultCode == RESULT_OK && (requestCode == PICK_IMAGE)) {
            Toast.makeText(getActivity(),"hellow",Toast.LENGTH_SHORT).show();
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    e.printStackTrace();
                }

                @Override
                public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {

                }

               /* @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    CropImage.activity(Uri.parse(String.valueOf(imageFile.toURI())))
                            .start(getActivity());
                }*/
            });


        } else if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
            PodImage podImage = new PodImage();
            podImage.setSource(uri);
            mImageListPhoto.add(podImage);
            mAdapterPhoto.notifyDataSetChanged();
        }else if(requestCode==REQUEST_CAMERA){

            Uri uri = data.getData();
            PodImage podImage = new PodImage();
            podImage.setSource(uri);
            mImageListPhoto.add(podImage);
            mAdapterPhoto.notifyDataSetChanged();

        }else if (requestCode == 1313 && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra("path");
            if (path != null || !path.equals("")) {
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PodImage podImage = new PodImage();
                podImage.setSource(uri);
                mImageListPhoto.add(podImage);
                mAdapterPhoto.notifyDataSetChanged();
            }

        }
    }


    private void satuandialog() {
        final String[] type = {"Ekor","Kg","Ons","Kwintal","Gram","Ml","Pon","Pack","Bungkus"};

        android.app.AlertDialog.Builder builder = getBuilder(getActivity());
        builder.setTitle("Jenis Satuan")
                .setItems(type, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        et_satuan_product.setText(type[which]);
                    }
                });

        builder.create().show();
    }

    private void typedialog() {
        mSelectedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pilih Kategori")
                .setMultiChoiceItems(tagsList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            isSelectedArray[which] = true;
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < isSelectedArray.length; i++) {
                            if (isSelectedArray[i] == true) {
                                mSelectedItems.add(i);
                            }
                        }
                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            int index = mSelectedItems.get(i);
                            String hasil = tagsList[index];
                            sb.append(hasil);
                            if (i < mSelectedItems.size() - 1) {
                                sb.append(", "); // Add a comma for separation

                            }

                        }
                        Log.d("yuhu1",sb.toString());
                        String strArray[] = sb.toString().split(",");
                        for(int ii=0; ii < strArray.length; ii++){
                           // String tipex;
                            Log.d("yuhu2",strArray[ii]);
                           // tipex = strArray[ii];
                            typenya.add(strArray[ii].trim());
                        }

                        et_type_product.setText(sb);
                        Log.e("tags", mSelectedItems.toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    private Map<String, Object> body(){
        Map<String, Object> obj = new HashMap<>();
        obj.put("name", name);
        return obj;
    }
    private void addMitra(List<String>arrayList) {

        Long tsLong = System.currentTimeMillis() / 1000;
        final String uuid = UUID.randomUUID().toString();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        Map<String, Object> obj = new HashMap<>();

        obj.put("name", name);
        obj.put("userId", user.getProfile().getUserId());
        obj.put("id",uuid);
        obj.put("type", typenya);
        obj.put("images",arrayList);
        Map<String, Object> coordinate = new HashMap<>();
       /* coordinate.put("latitude",latitude);
        coordinate.put("longitude", longitude);*/
        obj.put("coordinate",coordinate);
        obj.put("description",description);
        obj.put("weight",Integer.parseInt(weight));
        obj.put("units",satuan);
        obj.put("unit",satuan);
        obj.put("discount",0);
        obj.put("isActive",true);
        obj.put("isHighLight",false);
        obj.put("price",Integer.parseInt(price));
        obj.put("stock",Integer.parseInt(stock));
        obj.put("timestamp",timestamp);

        Map<String, Object> count = new HashMap<>();
        count.put("totalLikes",0);
        count.put("totalResponses",0);
        count.put("totalSell",0);
        count.put("totalViews",0);
        obj.put("count",count);


        obj.put("images",arrayList);


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


        /*firebaseFirestore.collection("product").document(uuid)
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
                });*/

    }


    void uploadImages(List<PodImage> filesPath, Context context) {
        progressDialog.show();
        urlsphoto = new ArrayList<>();
        imageCounter = 0;
        for (int i = 0; i < filesPath.size(); i++) {
            Uri file = (filesPath.get(i).isSignature()) ?
                    Uri.fromFile(new File(FileChooserHelper.getRealPathFromUri(context, filesPath.get(i).getSource()))) :
                    Uri.fromFile(new File(Objects.requireNonNull(filesPath.get(i).getSource().getPath())));
            StorageReference imageRef = folderRef.child(Objects.requireNonNull(file.getLastPathSegment()));
            UploadTask mUploadTask = imageRef.putFile(file);
            mUploadTask.addOnFailureListener(exception -> {
                progressDialog.dismiss();
                exception.printStackTrace();
            }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageCounter++;
                urlsphoto.add(uri.toString());
                if (imageCounter == filesPath.size()) {
                   // hideProgressDialog();
                   addMitra(urlsphoto);
                }
            }));
        }
    }


    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("load",false);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDeleted(PodImage model, int position, String type) {
        mImageListPhoto.remove(model);
        mAdapterPhoto.notifyItemRemoved(position);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

