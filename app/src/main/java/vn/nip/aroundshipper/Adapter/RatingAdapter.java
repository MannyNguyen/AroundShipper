package vn.nip.aroundshipper.Adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import vn.nip.aroundshipper.Bean.BeanRate;
import vn.nip.aroundshipper.Fragment.RatingInfoFragment;

import vn.nip.aroundshipper.R;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MenuViewHolder> {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;

    private List<BeanRate> list;
    RatingInfoFragment fragment;

    public RatingAdapter(FragmentActivity activity, RatingInfoFragment fragment, RecyclerView recycler, List<BeanRate> list) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rating_info, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        final BeanRate item = list.get(position);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(item.getCreate_date(), formatter);
        holder.createDate.setText(dateTime.getDayOfMonth() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getYear());
        holder.orderID.setText(item.getId_order());
        holder.rating.setRating(item.getRating());
        LayerDrawable stars = (LayerDrawable) holder.rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(fragment.getResources().getColor(R.color.main), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(fragment.getResources().getColor(R.color.gray_400), PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView createDate;
        private TextView orderID;
        private RatingBar rating;

        public MenuViewHolder(View view) {
            super(view);
            createDate = (TextView) view.findViewById(R.id.create_date);
            orderID = (TextView) view.findViewById(R.id.order_id);
            rating = (RatingBar) view.findViewById(R.id.rating);

        }
    }
}