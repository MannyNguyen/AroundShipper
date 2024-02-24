package vn.nip.aroundshipper.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import vn.nip.aroundshipper.Bean.BeanOrder;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.OrderCODFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.R;

public class OrderCODAdapter extends RecyclerView.Adapter<OrderCODAdapter.MyViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanOrder> list;

    public OrderCODAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanOrder> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_cod, parent, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final BeanOrder item = list.get(position);
            if (item != null) {
                holder.order_code.setText(item.getOrder_code());
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getDate(), df);
                holder.order_date.setText(dateTime.toString("dd/MM/yyyy"));
                holder.address_receiver.setText(item.getAddress());
                holder.price.setText(CmmFunc.formatMoney(item.getCost()));
                if (item.getStatus() == 1) {
                    holder.status.setText(R.string.success);
                    holder.status.setTextColor(ContextCompat.getColor(GlobalClass.getContext(), R.color.green_500));
                } else if (item.getStatus() == -1) {
                    holder.status.setText(R.string.cancelled);
                    holder.status.setTextColor(ContextCompat.getColor(GlobalClass.getContext(), R.color.red_500));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition=recycler.getChildLayoutPosition(view);
            BeanOrder item = list.get(itemPosition);
            if (item!=null){
                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, FullOrderFragment.newInstance(item.getId_order(), true));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView order_code, order_date, address_receiver, status, price;
        public MyViewHolder(View itemView) {
            super(itemView);
            order_code = (TextView) itemView.findViewById(R.id.order_code);
            order_date = (TextView) itemView.findViewById(R.id.order_date);
            address_receiver = (TextView) itemView.findViewById(R.id.address_receiver);
            status = (TextView) itemView.findViewById(R.id.status);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
