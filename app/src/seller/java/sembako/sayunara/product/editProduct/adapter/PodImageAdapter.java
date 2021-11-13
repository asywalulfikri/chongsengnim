package sembako.sayunara.product.editProduct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.util.ZoomableImageView;
import sembako.sayunara.product.editProduct.model.PodImage;


public class PodImageAdapter extends RecyclerView.Adapter<PodImageAdapter.ViewHolder> {

    private ArrayList<PodImage> imageList;
    private Context context;
    DeletedItemListener listener;
    private String type;

    public interface DeletedItemListener{
        void onDeleted(PodImage model, int position, String type);
    }

    public PodImageAdapter(Context context, ArrayList<PodImage> imageList, DeletedItemListener listener, String type) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
        this.type = type;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pod_photo, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        PodImage model = imageList.get(position);

        holder.clearButton.setOnClickListener(view -> listener.onDeleted(model, position,type));
        Picasso.get().load(model.getSource()).into(holder.imagePod);

    }


    public void removeItem(int position) {
        imageList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ZoomableImageView imagePod;
        ImageButton clearButton;

        public ViewHolder(View view) {
            super(view);
            imagePod = view.findViewById(R.id.image_pod);
            clearButton = view.findViewById(R.id.image_clear);

        }
    }

}

