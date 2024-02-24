package vn.nip.aroundshipper.Adapter;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.nip.aroundshipper.Bean.BeanBill;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Custom.CustomMoneyEditText;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Interface.ICallbackValue;
import vn.nip.aroundshipper.R;


/**
 * Created by viminh on 10/7/2016.
 */

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.OrderViewHolder> implements View.OnClickListener {

    BillFragment fragment;
    RecyclerView recycler;
    List<BeanBill> bills;

    public BillAdapter() {
    }

    public BillAdapter(BillFragment fragment, RecyclerView recycler, List<BeanBill> bills) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.bills = bills;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bill, parent, false);
        return new OrderViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        try {
            final BeanBill beanBill = bills.get(position);
            if (bills != null) {
                holder.error.setVisibility(View.GONE);
                holder.address.setText(beanBill.getAddress() + "");

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int value = getValue(editable.toString().trim());
                        BeanBill bean = bills.get(holder.getAdapterPosition());
                        bean.setPrice(value);
                        if (holder.error.getVisibility() == View.GONE) {
                            return;
                        }
                        holder.error.setVisibility(View.GONE);
                    }
                };
                holder.price.removeTextChangedListener(textWatcher);
                switch (beanBill.getPosition()) {
                    case 0:
                        holder.position.setBackgroundResource(R.drawable.ic_location_1);
                        holder.location.setText(fragment.getString(R.string.location_1));
                        break;
                    case 1:
                        holder.position.setBackgroundResource(R.drawable.ic_location_2);
                        holder.location.setText(fragment.getString(R.string.location_2));
                        break;
                    case 2:
                        holder.position.setBackgroundResource(R.drawable.ic_location_3);
                        holder.location.setText(fragment.getString(R.string.location_3));
                        break;
                }


                if (beanBill.getPrice() > 0) {
                    holder.price.setText(beanBill.getPrice() + "");
                } else {
                    holder.price.setText("");
                }

                if (beanBill.getImage() != null) {
                    Bitmap bitmap = CmmFunc.getBitmapFromByteArray(beanBill.getImage());
                    holder.image.setImageBitmap(bitmap);
                }


                if (beanBill.getPrice() == 0 && beanBill.getImage() != null) {
                    holder.error.setText(fragment.getString(R.string.error_bill_price));
                    holder.error.setVisibility(View.VISIBLE);
                    showError(holder.error);
                }

                if (beanBill.getPrice() > 0 && beanBill.getImage() == null) {
                    holder.error.setText(fragment.getString(R.string.error_bill_image));
                    showError(holder.error);
                }

                holder.price.addTextChangedListener(textWatcher);

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.ID_REQUEST = bills.indexOf(beanBill);
                        fragment.popup.setVisibility(View.VISIBLE);
                        CmmFunc.hideKeyboard(fragment.getActivity());
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (beanBill.getImage() == null) {
                            return;
                        }
                        beanBill.setImage(null);
                        holder.image.setImageResource(R.drawable.ic_image_bill);
                    }
                });

            }
        } catch (Exception e) {
            CmmFunc.showError("OrderAdapter", "onBindViewHolder", e.getMessage());
        }

    }

    private void showError(View error) {
        error.setVisibility(View.VISIBLE);
        final Animation animShake = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.anim_shake);
        error.startAnimation(animShake);
    }

    public int getValue(String s) {
        try {
            String value = s.replaceAll(",", "");
            int retValue = Integer.valueOf(value);
            return retValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView location;
        private TextView error;
        private CustomMoneyEditText price;
        private ImageView image;
        private ImageView remove;
        private View position;

        public OrderViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
            location = (TextView) view.findViewById(R.id.location);
            error = (TextView) view.findViewById(R.id.error);
            price = (CustomMoneyEditText) view.findViewById(R.id.actual_money);
            image = (ImageView) view.findViewById(R.id.pick);
            remove = (ImageView) view.findViewById(R.id.remove);
            position = view.findViewById(R.id.position);

        }
    }
}