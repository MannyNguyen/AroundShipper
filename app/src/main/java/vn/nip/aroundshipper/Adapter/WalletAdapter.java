package vn.nip.aroundshipper.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.nip.aroundshipper.Bean.BeanWallet;
import vn.nip.aroundshipper.R;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanWallet> list;
    String language;

    public WalletAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanWallet> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_wallet, parent, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BeanWallet item = list.get(position);
        try {
            if (item != null) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime dt = DateTime.parse(item.getCreate_date(),formatter);
                holder.time.setText(dt.toString("hh:mm a\ndd/MM/yyyy"));

                //holder.time.setText(item.getCreate_date());
                language = Locale.getDefault().getDisplayLanguage().toString();
                if (language.equals("Tiếng Việt")) {
                    holder.info.setText(item.getVn_title());
                    holder.content.setText(item.getVn_description());
                }
                if (language.equals("English")) {
                    holder.info.setText(item.getTitle());
                    holder.content.setText(item.getDescription());
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

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView time, date, info, content;

        public MyViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            info = (TextView) itemView.findViewById(R.id.info);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
