package vn.nip.aroundshipper.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.requests.ExtensionRequest;
import vn.nip.aroundshipper.Bean.BeanBill;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Fragment.Dialog.VerifyCodeDialogFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullOrderCommonFragment extends BaseFragment implements View.OnClickListener {

    public static final int CANCEL = -1;
    public static final int FOLLOW = 0;
    public static final int FINISH = 1;
    public static final int SCHEDULE = -2;
    public static final int PAYMENT = -3;
    public static final int CONSTRUCT = -4;
    public static final int FAIL = -5;

    //region Variables
    public List<BeanPoint> locations = new ArrayList<>();
    //endregion

    //region Contructors
    public FullOrderCommonFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static FullOrderCommonFragment newInstance(int orderID, boolean isDone) {

        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        args.putBoolean("is_done", isDone);
        FullOrderCommonFragment fragment = new FullOrderCommonFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_full_order_common, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (!isLoaded) {
                View confirm = getView().findViewById(R.id.confirm);
                if (getArguments().getBoolean("is_done")) {
                    confirm.setOnClickListener(FullOrderCommonFragment.this);
                    confirm.setVisibility(View.VISIBLE);
                } else {
                    confirm.setOnClickListener(null);
                    confirm.setVisibility(View.GONE);
                }

                SmartFoxHelper.getFullOrder(getArguments().getInt("order_id"));
                isLoaded = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region Methods


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<BeanBill> bills = new ArrayList<>();
                        for (BeanPoint beanPoint : locations) {
                            if (beanPoint.getPickup_type() != BeanPoint.PURCHASE) {
                                continue;
                            }
                            BeanBill beanBill = new BeanBill();
                            beanBill.setId(beanPoint.getId());
                            beanBill.setAddress(beanPoint.getAddress());
                            beanBill.setImage(null);
                            beanBill.setPrice(0);
                            beanBill.setPosition(locations.indexOf(beanPoint));
                            bills.add(beanBill);
                        }
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, BillFragment.newInstance(getArguments().getInt("order_id"), BillFragment.FINISH, CmmFunc.tryParseObject(bills)));
                    }
                }).start();
                break;
        }
    }
    //endregion
}
