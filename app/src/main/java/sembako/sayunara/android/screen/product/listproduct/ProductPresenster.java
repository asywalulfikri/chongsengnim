package sembako.sayunara.android.screen.product.listproduct;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import sembako.sayunara.android.screen.product.listproduct.model.Product;

public class ProductPresenster {
    FirebaseFirestore firebaseFirestore;
    ArrayList<Product>productArrayList;
    ProductListener productListener;
    protected int mPage = 1;


    void setOrderList(ProductListener listener,ArrayList<Product> products,FirebaseFirestore firestore) {
        this.productArrayList = products;
        this.firebaseFirestore = firestore;
        productListener = listener;
    }

    void getList(String type) {

        firebaseFirestore.collection("product").whereArrayContains("type",type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Integer rowList = 1;
                            for (DocumentSnapshot doc : task.getResult()) {

                                if (rowList <= (mPage * 10) && rowList > ((mPage - 1) * 10)) {
                                    Product product = new Product();
                                    // product.setCreatedAt(doc.getString("createdAt"));
                                    product.setDescription(doc.getString("description"));
                                    product.setName(doc.getString("name"));
                                    product.setPrice(doc.getLong("price"));
                                    product.setDiscount(doc.getLong("discount"));
                                    product.setId(doc.getString("id"));
                                    product.setImages((ArrayList<String>) doc.get("images"));
                                    // int ultimaVersion = (int) dataSnapshot.getValue();
                                    productArrayList.add(product);
                                }
                                rowList++;
                            }

                            productListener.onRequestSuccess(productArrayList);


                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
