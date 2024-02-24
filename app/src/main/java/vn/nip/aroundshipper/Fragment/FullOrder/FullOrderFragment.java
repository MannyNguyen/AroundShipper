package vn.nip.aroundshipper.Fragment.FullOrder;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Bean.BeanBill;
import vn.nip.aroundshipper.Bean.BeanItem;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullOrderFragment extends BaseFragment implements View.OnClickListener {


    //    ActionGetFullOrder actionGetFullOrder;
    TextView title, orderCode, deliveryTime, itemCost, serviceFee, shippingFee, totalFee,
            textPayment, itemCostName, verifyCode, totalCashDelivery;
    ImageView icPayment;
    LinearLayout llCashDelivery;
    View containerPurchaseFee;
    List<BeanPoint> points;

    public FullOrderFragment() {
        // Required empty public constructor
    }

    public static FullOrderFragment newInstance(int orderID, boolean isDone) {
        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        args.putBoolean("is_done", isDone);
        FullOrderFragment fragment = new FullOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_full_order2, container, false);
        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                title = (TextView) view.findViewById(R.id.title);
                orderCode = (TextView) view.findViewById(R.id.order_code);
                deliveryTime = (TextView) view.findViewById(R.id.delivery_time);
                itemCost = (TextView) view.findViewById(R.id.item_cost);
                itemCostName = (TextView) view.findViewById(R.id.item_fee_name);
                serviceFee = (TextView) view.findViewById(R.id.service_fee);
                shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                totalFee = (TextView) view.findViewById(R.id.total);
//                verifyCode = (TextView) view.findViewById(R.id.code);
                textPayment = (TextView) view.findViewById(R.id.text_payment);
                icPayment = (ImageView) view.findViewById(R.id.ic_payment);
                containerPurchaseFee = view.findViewById(R.id.container_purchase_fee);
                totalCashDelivery = (TextView) view.findViewById(R.id.total_cash_delivery);
                llCashDelivery = (LinearLayout) view.findViewById(R.id.ll_cash_delivery);

                LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                if (getArguments().getBoolean("is_done")) {
                    confirm.setOnClickListener(FullOrderFragment.this);
                    confirm.setVisibility(View.VISIBLE);
                } else {
                    confirm.setOnClickListener(null);
                    confirm.setVisibility(View.GONE);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartFoxHelper.getFullOrder(getArguments().getInt("order_id"));
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    public void executeFullOrder(final SFSObject data) {
        try {
            int code = data.getInt("code");
            if (code == 1) {
                points = new ArrayList<>();
                final ISFSArray arr = data.getSFSArray("locations");
                for (int i = 0; i < arr.size(); i++) {
                    ISFSObject sfsObject = arr.getSFSObject(i);
                    BeanPoint bean = new BeanPoint();
                    bean.setId(sfsObject.getInt("id"));
                    bean.setAddress(sfsObject.getUtfString("address"));
                    bean.setNote(sfsObject.getUtfString("note"));
                    bean.setRecipent_name(sfsObject.getUtfString("recipent_name"));
                    bean.setPhone(sfsObject.getUtfString("phone"));
                    bean.setPickup_type(sfsObject.getInt("pickup_type"));
                    bean.setItem_cost(sfsObject.getInt("item_cost"));
                    bean.setReceive_shop_product_status(sfsObject.getInt("receive_shop_product_status"));
                    bean.setX15(sfsObject.getBool("x15"));
                    bean.setBill_price(sfsObject.getInt("bill_price"));
                    bean.setBill_image(sfsObject.getUtfString("bill_image"));
                    bean.setShipper_fullname(sfsObject.getUtfString("shipper_fullname"));
                    bean.setShipper_phone(sfsObject.getUtfString("shipper_phone"));
                    List<BeanItem> items = (List<BeanItem>) CmmFunc.tryParseList(sfsObject.getUtfString("location_items"), BeanItem.class);
                    bean.setLocation_items(items);
                    points.add(bean);
                }
                final int status = data.getInt("status");

                final boolean isGift = data.getBool("show_gift");
                final String order_code = data.getUtfString("order_code");
                final String paymentType = data.getUtfString("payment_type");

                int day = data.getInt("delivery_day");
                int month = data.getInt("delivery_month");
                int year = data.getInt("delivery_year");
                int hour = data.getInt("delivery_hour");
                int minute = data.getInt("delivery_minute");
                DateTime delivery = new DateTime(year, month, day, hour, minute);

                final String ship = CmmFunc.formatMoney(data.getInt("shipping_fee"), true);
                final String service = CmmFunc.formatMoney(data.getInt("service_fee"), true);
                final String itemFee = CmmFunc.formatMoney(data.getInt("item_cost"), true);
                final String total = CmmFunc.formatMoney(data.getInt("total"), true);
                final String totalCOD = CmmFunc.formatMoney(data.getInt("total_cod"), true);

                TextView txtNotice = (TextView) view.findViewById(R.id.txt_notice);
                if (data.getBool("return_to_pickup") == true) {
                    txtNotice.setVisibility(View.VISIBLE);
                } else {
                    txtNotice.setVisibility(View.GONE);
                }

                orderCode.setText(order_code + StringUtils.EMPTY);
                deliveryTime.setText(delivery.toString("hh:mm a EEEE, dd/MM/yyyy"));
                if (isGift) {
                    title.setText(getString(R.string.gifting));
                    containerPurchaseFee.setVisibility(View.VISIBLE);
                    itemCostName.setText(getString(R.string.item_cost));
                    itemCost.setText(itemFee);
                    FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CommonFullOrderFragment.newInstance(CommonFullOrderFragment.GIFTING, CmmFunc.tryParseObject(points), status));

                } else {
                    int type = points.get(1).getPickup_type();
                    if (type == BeanPoint.COD) {
                        //add Fragment
                        FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CODFullOrderFragment.newInstance(CmmFunc.tryParseObject(points)));
                        llCashDelivery.setVisibility(View.VISIBLE);
                        title.setText(getString(R.string.cod_order_info));
                    } else {
                        title.setText(getString(R.string.pickup_delivery));
                        int t = points.get(0).getPickup_type();
                        if (t == BeanPoint.PURCHASE) {
                            llCashDelivery.setVisibility(View.GONE);
                            containerPurchaseFee.setVisibility(View.VISIBLE);
                            itemCostName.setText(getString(R.string.pay_on_behaft));
                            itemCost.setText(itemFee);
                        }
                        FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CommonFullOrderFragment.newInstance(CommonFullOrderFragment.PICKUP, CmmFunc.tryParseObject(points), status));
                        //add Fragment
                    }
                }

                shippingFee.setText(ship + StringUtils.EMPTY);
                serviceFee.setText(service + StringUtils.EMPTY);
                totalFee.setText(total + StringUtils.EMPTY);
                totalCashDelivery.setText(totalCOD+StringUtils.EMPTY);
                // verifyCode.setText(data.getUtfString("verify_code") + "");
                switch (paymentType) {
                    case "CASH":
                        icPayment.setImageResource(R.drawable.ic_cash_selected);
                        textPayment.setText(getString(R.string.cast));
                        break;
                    case "ONLINE":
                        icPayment.setImageResource(R.drawable.ic_online_selected);
                        textPayment.setText(getString(R.string.online_payment));
                        break;
                    case "AROUND_PAY":
                        icPayment.setImageResource(R.drawable.ic_around_box_selected);
                        textPayment.setText(getString(R.string.around_payment));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<BeanBill> bills = new ArrayList<>();
                        for (BeanPoint beanPoint : points) {
                            if (beanPoint.getPickup_type() != BeanPoint.PURCHASE) {
                                continue;
                            }
                            BeanBill beanBill = new BeanBill();
                            beanBill.setId(beanPoint.getId());
                            beanBill.setAddress(beanPoint.getAddress());
                            beanBill.setImage(null);
                            beanBill.setPrice(0);
                            beanBill.setPosition(points.indexOf(beanPoint));
                            bills.add(beanBill);
                        }
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, BillFragment.newInstance(getArguments().getInt("order_id"), BillFragment.FINISH, CmmFunc.tryParseObject(bills)));
                    }
                }).start();
                break;
        }
    }
}
