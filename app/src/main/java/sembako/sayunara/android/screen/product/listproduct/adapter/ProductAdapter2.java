package sembako.sayunara.android.screen.product.listproduct.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.desmond.squarecamera.SquareImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.product.listproduct.model.Product;
import sembako.sayunara.android.util.HFRecyclerViewAdapter;


public class ProductAdapter2 extends HFRecyclerViewAdapter<Product, ProductAdapter2.ItemViewHolder> {
    private OnItemClickListener onItemClickListener;
    int minteger = 0;
    protected int total = 0;
    int price = 0;
    Context context;
    interface OnItemSelected {
        void onSelect(Product model);
    }

    private OnItemSelected listener;


    public ProductAdapter2(Context context1) {
        super(context1);
        context = context1;
    }

    @Override
    public void footerOnVisibleItem() {

    }


    @Override
    public ItemViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindDataItemViewHolder(ItemViewHolder holder, final int position) {
        Product product = getData().get(position);

        holder.name.setText(product.getName());

        Picasso.get()
                .load(product.getImages().get(0))
                .into(holder.image);
       /* Glide
                .with(context)
                .load(product.getImages().get(0))
                .centerCrop()
                .into(holder.image);
*/
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
            holder.price.setText("Rp "+k+"/"+product.getUnit());

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
            holder.textview_price_discount.setText("Rp "+k+"/"+product.getUnit());

            holder.price.setText("Rp "+df.format(amount));
        }

        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minteger = 1;
                holder.tv_integer_number.setText(String.valueOf(minteger));
                holder.btn_buy.setVisibility(View.GONE);
                holder.linear_quantity.setVisibility(View.VISIBLE);
            }
        });

        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minteger = minteger + 1;
                holder.tv_integer_number.setText(String.valueOf(minteger));
            }
        });


        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(minteger!=0){
                    minteger = minteger - 1;
                    holder.tv_integer_number.setText(String.valueOf(minteger));
                    if(minteger==0){
                        holder.btn_buy.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnActionClickQuestion(v,position);
            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView name,price,textview_price_discount,tv_integer_number;
        SquareImageView image;
        CardView cv;
        Button button_discount,btn_buy,btn_increase,btn_decrease;
        LinearLayout linear_quantity;

        public ItemViewHolder(View view) {
            super(view);
            name   =  view.findViewById(R.id.tv_product_name);
            price  = view.findViewById(R.id.tv_product_price);
            image  = view.findViewById(R.id.iv_product);
            cv     = view.findViewById(R.id.cv_product);
            button_discount = view.findViewById(R.id.button_discount);
            btn_buy = view.findViewById(R.id.btn_buy);
            btn_increase = view.findViewById(R.id.btn_increase);
            btn_decrease = view.findViewById(R.id.btn_decrease);
            tv_integer_number = view.findViewById(R.id.tv_integer_number);

            textview_price_discount = view.findViewById(R.id.tv_product_discount);
            linear_quantity = view.findViewById(R.id.linear_quantity);
        }

        @Override
        public void onClick(View v) {

        }
    }
    public void actionQuestion(OnItemClickListener actionQuestion) {
        onItemClickListener = actionQuestion;
    }public interface OnItemClickListener {
        void OnActionClickQuestion(View view, int position);
    }

}