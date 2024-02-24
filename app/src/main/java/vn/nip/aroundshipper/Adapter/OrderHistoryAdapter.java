package vn.nip.aroundshipper.Adapter;


import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.nip.aroundshipper.Bean.BeanOrderHistory;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Helper.DateTimeHelper;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.R;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanOrderHistory> list;

    public OrderHistoryAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanOrderHistory> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final BeanOrderHistory item = list.get(position);
            if (item != null) {
                if (item.getStatus() == 1) {
                    holder.status.setTextColor(activity.getResources().getColor(R.color.green_500));
                    holder.status.setText(activity.getResources().getString(R.string.successfully));
                } else if (item.getStatus() == -1) {
                    holder.status.setTextColor(activity.getResources().getColor(R.color.red_500));
                    holder.status.setText(activity.getResources().getString(R.string.cancelled));
                }
                holder.total.setText(CmmFunc.formatMoney(item.getTotal()) + "");
                if (item.getIs_order_of_partner() == 1) {
//                        holder.type.setText(BeanOrderHistory.getStringType(item)+item.getPartner_fullname());
                    holder.type.setText(BeanOrderHistory.getStringType(item) + " - " + activity.getString(R.string.partner) + ": " + item.getPartner_fullname());
                }else {
                    holder.type.setText(BeanOrderHistory.getStringType(item));
                }
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getCreate_date(), df);
                holder.date.setText(DateTimeHelper.parseDate(dateTime));
                holder.time.setText(dateTime.toString("hh:mm"));
                holder.ampm.setText(dateTime.toString("a"));
                holder.orderCode.setText(item.getOrder_code());
                holder.address.setText(item.getAddress());

//                    BeanPoint b = item.getLocations().get(item.getLocations().size() - 1);
//                    holder.address.setText(b.getAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private List<BeanOrderHistory> filterDate() {
//        filterList = new ArrayList<>();
//        if (dt != null) {
//            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//            for (BeanOrderHistory beanOrderHistory : list) {
//                DateTime dateTime = formatter.parseDateTime(beanOrderHistory.getCreate_date());
//                String strDateTime = dateTime.toString(FORMAT_DATETIME);
////                if (strDateTime.equals(dt)) {
////                    filterList.add(beanOrderHistory);
////                }
//            }
//            return filterList;
//        }
//        return null;
//    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanOrderHistory item = list.get(itemPosition);
            if (item != null) {
//                Fragment fragment = FullOrderCommonFragment.newInstance(item.getId(), false);
//                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, FullOrderFragment.newInstance(item.getId(), false));
            }
        } catch (Exception e) {
            Log.e("OrderHistoryAdapter", "ViMT - onClick: " + e.getMessage());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView type, total, status, date, time, ampm, orderCode, address;

        public MenuViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            total = (TextView) view.findViewById(R.id.total);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            ampm = (TextView) view.findViewById(R.id.ampm);
            orderCode = (TextView) view.findViewById(R.id.order_code);
            status = (TextView) view.findViewById(R.id.status);
            address = (TextView) view.findViewById(R.id.address);
        }
    }
}