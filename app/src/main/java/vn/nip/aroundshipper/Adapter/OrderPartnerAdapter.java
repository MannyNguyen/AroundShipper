package vn.nip.aroundshipper.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import vn.nip.aroundshipper.Bean.BeanOrderPartner;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.Dialog.ConfirmDialogFragment;
import vn.nip.aroundshipper.Fragment.Partner.OrderPartnerFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;


/**
 * Created by viminh on 10/7/2016.
 */

public class OrderPartnerAdapter extends RecyclerView.Adapter<OrderPartnerAdapter.OrderViewHolder> implements View.OnClickListener {

    OrderPartnerFragment fragment;
    RecyclerView recycler;
    List<BeanOrderPartner> items;

    public OrderPartnerAdapter(OrderPartnerFragment fragment, RecyclerView recycler, List<BeanOrderPartner> items) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.items = items;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_partner, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        try {
            final BeanOrderPartner item = items.get(position);
            if (item != null) {
                holder.position.setText((position + 1) + StringUtils.EMPTY);
                holder.orderCode.setText(item.getOrder_code() + StringUtils.EMPTY);
                holder.type.setText(fragment.getString(BeanOrderPartner.getNameIDByType(item.getOrder_type())));
                holder.total.setText(CmmFunc.formatMoney(item.getTotal()) + StringUtils.EMPTY);
                holder.address.setText(fragment.getString(R.string.take_the_goods_at) + " " + item.getAddress());
                DateTime dateTime = DateTime.parse(item.getDate(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                holder.time.setText(dateTime.toString("hh:mm a dd/MM/yyyy") + StringUtils.EMPTY);
                if (item.getAction() == BeanOrderPartner.ACTION_DONE) {
                    holder.doneIcon.setImageResource(R.drawable.ic_done_order_partner);
                    holder.doneTitle.setText(fragment.getString(R.string.done_order_partner));
                } else if (item.getAction() == BeanOrderPartner.ACTION_RUN) {
                    holder.doneIcon.setImageResource(R.drawable.ic_run_order_partner);
                    holder.doneTitle.setText(fragment.getString(R.string.run_order_partner));
                }

                if (item.getPhone().equals("") || item.getPhone().equals(null)) {
                    holder.phoneIcon.setImageResource(R.drawable.call_icon_off);
                } else {
                    holder.phoneIcon.setImageResource(R.drawable.ic_call_order_partner);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //       FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, FullOrderCommonFragment.newInstance(item.getId(), false));
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, FullOrderFragment.newInstance(item.getId(), false));
                    }
                });

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.getPhone().equals("") || item.getPhone().equals(null)){
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getPhone(), null));
                        fragment.getActivity().startActivity(intent);
                    }
                });

                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.beanOrderPartner = item;
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, BillFragment.newInstance(item.getId(), BillFragment.CANCEL, StringUtils.EMPTY));
                    }
                });

                holder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            fragment.beanOrderPartner = item;
                            if (item.getAction() == BeanOrderPartner.ACTION_RUN) {
                                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
                                confirmDialogFragment.setMessage(fragment.getString(R.string.confirm_start));
                                confirmDialogFragment.setOkCallback(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment.showProgress();
                                        SmartFoxHelper.start(item.getId());
                                    }
                                });
                                confirmDialogFragment.show(fragment.getActivity().getSupportFragmentManager(), confirmDialogFragment.getClass().getName());

                            } else if (item.getAction() == BeanOrderPartner.ACTION_DONE) {
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, BillFragment.newInstance(item.getId(), BillFragment.FINISH, StringUtils.EMPTY));
                            }
                        }
                });
            }
        } catch (Exception e) {
            CmmFunc.showError("OrderAdapter", "onBindViewHolder", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {

    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView position, type, total, address, time, orderCode;
        View done, cancel, call;
        TextView doneTitle;
        ImageView doneIcon, phoneIcon;

        public OrderViewHolder(View view) {
            super(view);
            position = (TextView) view.findViewById(R.id.position);
            type = (TextView) view.findViewById(R.id.type);
            total = (TextView) view.findViewById(R.id.total);
            address = (TextView) view.findViewById(R.id.address);
            time = (TextView) view.findViewById(R.id.time);
            orderCode = (TextView) view.findViewById(R.id.order_code);
            done = view.findViewById(R.id.done);
            cancel = view.findViewById(R.id.cancel);
            call = view.findViewById(R.id.call);
            doneTitle = (TextView) view.findViewById(R.id.done_title);
            doneIcon = (ImageView) view.findViewById(R.id.done_icon);
            phoneIcon = (ImageView) itemView.findViewById(R.id.phone_icon);
        }
    }
}