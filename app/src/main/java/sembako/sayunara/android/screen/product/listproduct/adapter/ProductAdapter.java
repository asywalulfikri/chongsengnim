package sembako.sayunara.android.screen.product.listproduct.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.product.listproduct.model.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Product> productArrayList;
    private ActionQuestion mActionSend;

    public ProductAdapter(ArrayList<Product> arrayList, Context context) {
        this.productArrayList = arrayList;
        this.context = context;
    }


    public void updateData(ArrayList<Product> arrayList) {
        productArrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Product product = productArrayList.get(position);

        holder.name.setText(product.getName());
        Glide
                .with(context)
                .load(product.getImages().get(0))
                .centerCrop()
                .into(holder.image);

        double harga = Double.parseDouble(String.valueOf(product.getPrice()));
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String k = df.format(harga);


        if(product.getDiscount()==0){
            holder.button_discount.setVisibility(View.GONE);
            holder.price.setText("Rp "+k);

        }else {
            holder.button_discount.setVisibility(View.VISIBLE);
            holder.textview_price_discount.setVisibility(View.VISIBLE);
            holder.button_discount.setText(String.valueOf(product.getDiscount())+ " %");
            int price = Integer.parseInt(String.valueOf(product.getPrice()));
            int discount = Integer.parseInt(String.valueOf(product.getDiscount()));

            int total = (discount*price)/100;
            int total2 = price-total;
            double amount = Double.parseDouble(String.valueOf(total2));
            holder.textview_price_discount.setPaintFlags(holder.textview_price_discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textview_price_discount.setText("Rp "+k);

            holder.price.setText("Rp "+df.format(amount));
        }


    }


    @Override
    public int getItemCount() {
        return productArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {


        TextView name,price,textview_price_discount;
        ImageView image;
        CardView cv;
        Button button_discount;

        ViewHolder(View view) {
            super(view);
            name   =  view.findViewById(R.id.tv_product_name);
            price  = view.findViewById(R.id.tv_product_price);
            image  = view.findViewById(R.id.iv_product);
            cv     = view.findViewById(R.id.cv_product);
            button_discount = view.findViewById(R.id.button_discount);
            textview_price_discount = view.findViewById(R.id.tv_product_discount);

        }
    }
    public void actionQuestion(ActionQuestion actionQuestion) {
        mActionSend = actionQuestion;
    }public interface ActionQuestion {
        void OnActionClickQuestion(View view, int position);
    }

}