package vn.nip.aroundshipper.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;

import vn.nip.aroundshipper.Bean.BeanSchedule;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.Dialog.ConfirmDialogFragment;
import vn.nip.aroundshipper.Fragment.ScheduleFragment;
import vn.nip.aroundshipper.Helper.DateTimeHelper;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;

import vn.nip.aroundshipper.R;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import sfs2x.client.requests.ExtensionRequest;

/**
 * Created by viminh on 10/6/2016.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;

    private List<BeanSchedule> list;
    boolean isMe;
    ScheduleFragment fragment;

    public ScheduleAdapter(FragmentActivity activity, ScheduleFragment fragment, RecyclerView recycler, List<BeanSchedule> list, boolean isMe) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
        this.isMe = isMe;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_schedule, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final BeanSchedule item = list.get(position);
            if (item != null) {
                if (isMe) {
                    holder.accept.setVisibility(View.GONE);
                    if (item.is_show_start()) {
                        holder.start.setVisibility(View.VISIBLE);
                    } else {
                        holder.start.setVisibility(View.GONE);
                    }
                    holder.start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                            confirmDialogFragment.setMessage(activity.getString(R.string.confirm_start));
                            confirmDialogFragment.setOkCallback(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.showProgress();
                                    SmartFoxHelper.start(item.getId());
                                }
                            });
                            confirmDialogFragment.show(activity.getSupportFragmentManager(), ConfirmDialogFragment.class.getName());
                        }
                    });
                } else {
                    holder.accept.setVisibility(View.VISIBLE);
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                            confirmDialogFragment.setMessage(activity.getString(R.string.confirm_accept));
                            confirmDialogFragment.setOkCallback(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.showProgress();
                                    fragment.showProgress();
                                    SFSObject sfsObject = new SFSObject();
                                    sfsObject.putUtfString("command", "ACCEPT_SCHEDULE_ORDER");
                                    sfsObject.putInt("id_order", item.getId());
                                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                                }
                            });
                            confirmDialogFragment.show(activity.getSupportFragmentManager(), ConfirmDialogFragment.class.getName());
                        }
                    });
                }
                holder.total.setText(CmmFunc.formatMoney(item.getTotal()) + "");
                holder.type.setText(BeanSchedule.getStringType(item));
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getCreate_date(), df);
                holder.date.setText(DateTimeHelper.parseDate(dateTime));
                holder.receiptName.setText(item.getRecipent_name());
                holder.receiptAddress.setText(item.getAddress());
                int h = dateTime.getHourOfDay();
                String ampm = "AM";
                if (h == 0) {
                    h = 12;
                    ampm = "AM";
                } else if (h == 12) {
                    h = 12;
                    ampm = "PM";
                } else if (h > 12) {
                    h = h - 12;
                    ampm = "PM";
                } else {
                    ampm = "AM";
                }
                holder.time.setText(StringUtils.leftPad(h + "", 2, "0") + ":" + StringUtils.leftPad(dateTime.getMinuteOfHour() + "", 2, "0"));
                holder.ampm.setText(ampm);
                holder.orderCode.setText(item.getOrder_code());

                if (item.getType().equals("GIFTING")) {
                    holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_history_gifting));
                    holder.line.setBackgroundColor(activity.getResources().getColor(R.color.gifting));
                } else {
                    holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_history_pickup));
                    holder.line.setBackgroundColor(activity.getResources().getColor(R.color.main));
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
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanSchedule item = list.get(itemPosition);
            if (item != null) {
                //      Fragment fragment = FullOrderCommonFragment.newInstance(item.getId(), false);
                Fragment fragment = FullOrderFragment.newInstance(item.getId(), false);
                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
            }
        } catch (Exception e) {
            Log.e("Schedule", "ViMT - onClick: " + e.getMessage());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView type, total, start, accept, date, time, ampm, orderCode, receiptName, receiptAddress;
        private ImageView icon;
        FrameLayout line;

        public MenuViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            total = (TextView) view.findViewById(R.id.total);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            ampm = (TextView) view.findViewById(R.id.ampm);
            orderCode = (TextView) view.findViewById(R.id.order_code);
            start = (TextView) view.findViewById(R.id.start);
            accept = (TextView) view.findViewById(R.id.accept);
            receiptName = (TextView) view.findViewById(R.id.receipt_name);
            receiptAddress = (TextView) view.findViewById(R.id.receipt_address);
            icon = (ImageView) view.findViewById(R.id.icon);
            line = (FrameLayout) view.findViewById(R.id.line);
        }
    }
}