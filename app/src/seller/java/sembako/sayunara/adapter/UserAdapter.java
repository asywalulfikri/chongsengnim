package sembako.sayunara.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.component.account.login.data.model.User;
import sembako.sayunara.android.ui.util.HFRecyclerViewAdapter;
import sembako.sayunara.android.ui.util.TimeUtils;


public class UserAdapter extends HFRecyclerViewAdapter<User, UserAdapter.ItemViewHolder> {

    private OnItemClickListener onItemClickListener;
    int minteger = 0;
    protected int total = 0;

    Context context;
    interface OnItemSelected {
        void onSelect(User model);
    }

    private OnItemSelected listener;


    public UserAdapter(Context context1) {
        super(context1);
        context = context1;
    }

    @Override
    public void footerOnVisibleItem() {

    }


    @Override
    public ItemViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new ItemViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindDataItemViewHolder(ItemViewHolder holder, final int position) {
        User user = getData().get(position);
        holder.tvUsername.setText(user.getProfile().getUsername());
        holder.tvJoinDate.setText("Bergabung "+new TimeUtils().formatDateToIndonesia(user.getCreatedAt().getIso()));
        holder.tvNumber.setText(String.valueOf(position+1));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tvUsername,tvJoinDate,tvNumber;

        public ItemViewHolder(View view) {
            super(view);
            tvUsername   =  view.findViewById(R.id.tv_username);
            tvJoinDate  = view.findViewById(R.id.tv_join);
            tvNumber = view.findViewById(R.id.tv_number);
        }

        @Override
        public void onClick(View v) {

        }
    }
    public void actionDetail(OnItemClickListener actionQuestion) {
        onItemClickListener = actionQuestion;
    }public interface OnItemClickListener {
        void OnActionClickQuestion(View view, int position);
    }

}