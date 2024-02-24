package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import vn.nip.aroundshipper.Adapter.OrderHistoryAdapter;
import vn.nip.aroundshipper.Bean.BeanOrderHistory;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Custom.CustomDatePicker;
import vn.nip.aroundshipper.Helper.ErrorHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.IDateCallBack;
import vn.nip.aroundshipper.R;

import java.util.ArrayList;
import java.util.List;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistoryFragment extends BaseFragment implements View.OnClickListener {
    final String FORMAT_DATETIME = "yyyy-MM-dd";
    ImageView ivCalendar, chkCalendar;
    TextView history, historyDate, status;
    LinearLayout llDate;
    RecyclerView recycler;
    Boolean isCheck = true;
    DateTime dt = new DateTime();
    //region Variables
    List<BeanOrderHistory> originallist = new ArrayList<>();
    List<BeanOrderHistory> items = new ArrayList<>();
    //endregion

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    //region Override
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_history, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            showProgress();
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getActivity().getString(R.string.order_history));
            ivCalendar = (ImageView) view.findViewById(R.id.iv_calendar);
            ivCalendar.setBackgroundResource(R.drawable.ic_calendar_off);
            chkCalendar = (ImageView) view.findViewById(R.id.chk_calendar);
            llDate = (LinearLayout) view.findViewById(R.id.ll_date);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            history = (TextView) view.findViewById(R.id.history);
            historyDate = (TextView) view.findViewById(R.id.history_date);
            chkCalendar.setBackgroundResource(R.drawable.switch_button_off);
            status = (TextView) view.findViewById(R.id.status);
            chkCalendar.setOnClickListener(this);
            llDate.setOnClickListener(null);
            recycler.setVisibility(View.VISIBLE);
            OrderHistoryAdapter adapter = new OrderHistoryAdapter(getActivity(), recycler, items);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
            SmartFoxHelper.getHistoryOrder();
            isLoaded = true;
        }
    }
    //endregion

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chk_calendar:
                if (isCheck) {
                    items.clear();
                    new CustomDatePicker(getContext()).show(new IDateCallBack() {
                        @Override
                        public void excute(DateTime dateTime) {
                            chkCalendar.setBackgroundResource(R.drawable.switch_button);
                            history.setText(getString(R.string.history_today));
                            ivCalendar.setBackgroundResource(R.drawable.ic_calendar_on);
                            historyDate.setText(dateTime.getDayOfMonth() + " - " + dateTime.getMonthOfYear() + " - " + dateTime.getYear());
                            dt = dateTime;
                            llDate.setOnClickListener(OrderHistoryFragment.this);
                            String checkTime = dt.toString(FORMAT_DATETIME);
                            status.setVisibility(View.GONE);
                            for (BeanOrderHistory beanOrderHistory : originallist) {
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                DateTime beanDateTime = DateTime.parse(beanOrderHistory.getCreate_date(), formatter);
                                String strDateTime = beanDateTime.toString(FORMAT_DATETIME);
                                if (strDateTime.equals(checkTime)) {
                                    items.add(beanOrderHistory);
                                }
                            }
                            if (items.size() == 0) {
                                status.setVisibility(View.VISIBLE);
                            }

                            recycler.getAdapter().notifyDataSetChanged();
                            isCheck = false;
                        }
                    });
                } else {
                    items.clear();
                    history.setText(getString(R.string.all_history));
                    historyDate.setText("");
                    ivCalendar.setBackgroundResource(R.drawable.ic_calendar_off);
                    chkCalendar.setBackgroundResource(R.drawable.switch_button_off);
                    llDate.setOnClickListener(null);
                    items.addAll(originallist);
                    recycler.getAdapter().notifyDataSetChanged();
                    status.setVisibility(View.GONE);
                    if (items.size() == 0) {
                        status.setVisibility(View.VISIBLE);
                    }
                    isCheck = true;
                }
                break;
            case R.id.ll_date:
                if (dt != null) {
                    new CustomDatePicker(getContext(), dt).show(new IDateCallBack() {
                        @Override
                        public void excute(DateTime dateTime) {
                            items.clear();
                            dt = dateTime;
                            historyDate.setText(dateTime.getDayOfMonth() + " - " + dateTime.getMonthOfYear() + " - " + dateTime.getYear());
                            String checkTime = dt.toString(FORMAT_DATETIME);
                            status.setVisibility(View.GONE);
                            for (BeanOrderHistory beanOrderHistory : originallist) {
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                DateTime beanDateTime = formatter.parseDateTime(beanOrderHistory.getCreate_date());
                                String strDateTime = beanDateTime.toString(FORMAT_DATETIME);
                                if (strDateTime.equals(checkTime)) {
                                    items.add(beanOrderHistory);
                                }
                            }
                            if (items.size() == 0) {
                                status.setVisibility(View.VISIBLE);
                            }
                            recycler.getAdapter().notifyDataSetChanged();
                        }
                    });
                }
                break;
        }
    }

    //region Methods
    private void getHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_ORDER_HISTORY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

//                    List<BeanOrderHistory> items = new ArrayList<BeanOrderHistory>();
//                    ISFSArray array = sfsObject.getSFSArray("order_history");
//                    if (array.size() > 0) {
//                        for (int i = 0; i < array.size(); i++) {
//                            BeanOrderHistory bean = (BeanOrderHistory) CmmFunc.tryParseJson(array.getUtfString(i), BeanOrderHistory.class);
//                            items.add(bean);
//                        }
//                        recycler.setVisibility(View.VISIBLE);
//                        OrderHistoryAdapter adapter = new OrderHistoryAdapter(getActivity(), recycler, items);
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(GlobalClass.getContext(), LinearLayoutManager.VERTICAL, false);
//                        recycler.setLayoutManager(layoutManager);
//                        recycler.setAdapter(adapter);
//                    } else {
//                        TextView status = (TextView) view.findViewById(R.id.status);
//                        status.setVisibility(View.VISIBLE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //endregion

    public void executeHistoryData(SFSObject params) {
        try {
            ISFSArray array = params.getSFSArray("order_history");
            if (array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    BeanOrderHistory bean = (BeanOrderHistory) CmmFunc.tryParseJson(array.getUtfString(i), BeanOrderHistory.class);
                    originallist.add(bean);
                    items.add(bean);
                    status.setVisibility(View.GONE);
                }
                recycler.getAdapter().notifyDataSetChanged();
            } else {
                status.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
