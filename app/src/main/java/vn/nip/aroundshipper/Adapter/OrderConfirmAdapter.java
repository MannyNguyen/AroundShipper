package vn.nip.aroundshipper.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.R;


/**
 * Created by viminh on 10/7/2016.
 */

public class OrderConfirmAdapter extends RecyclerView.Adapter<OrderConfirmAdapter.OrderViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;

    public OrderConfirmAdapter() {
    }

    public OrderConfirmAdapter(FragmentActivity activity, RecyclerView recycler) {
        this.activity = activity;
        this.recycler = recycler;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_form, parent, false);
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_order_form_drop, parent, false);
        }
        return new OrderViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == CmmVariable.points.size() - 1) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        try {
            BeanPoint item = CmmVariable.points.get(position);
            holder.address.setText(item.getAddress());
            holder.note.setText(item.getItem_name() + "");

            holder.number.setText(position + 1 + "");
        } catch (Exception e) {
            CmmFunc.showError("OrderAdapter", "onBindViewHolder", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return CmmVariable.points.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView note;
        private TextView number;

        public OrderViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.location);
            note = (TextView) view.findViewById(R.id.package_name);
            number = (TextView) view.findViewById(R.id.number);
        }
    }
}